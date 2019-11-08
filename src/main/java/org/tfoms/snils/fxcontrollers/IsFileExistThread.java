package org.tfoms.snils.fxcontrollers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.tfoms.snils.dao.PersonDAO;
import org.tfoms.snils.dao.SnilsDAO;
import org.tfoms.snils.dao.SnilsSaveDAO;
import org.tfoms.snils.model.SnilsSaveResponse;
import org.tfoms.snils.model.TablePerson;
import org.tfoms.snils.model.ui.Settings;
import org.tfoms.snils.model.ui.StatusBar;
import org.tfoms.snils.xmls.XmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author esa
 * @see java.lang.Thread
 * @see java.util.concurrent.ExecutorService;
 * */
public class IsFileExistThread extends Thread{
    private static Logger LOG = LoggerFactory.getLogger(IsFileExistThread.class);

    private boolean shutdownTask = false;

    // Для обновлнеия статус бара
    private StatusBar statusBar;

    // Енп по которым посылается запрос
    private final ArrayList<String> enps = new ArrayList<>();

    // Енп по которым пришел ответ
    private final ArrayList<String> enpsGood = new ArrayList<>();

    // Для обновления таблицы ( когда приходит ответ, снилс записывается в таблицу)

    private TableView<TablePerson> tableView;

    private XmlParser parser;

    private Settings settings;


    private SnilsSaveDAO snilsSaveDAO;

    private String directorySnils;

    private String directoryError;

    private Long timeWait;

    private boolean saveResponse;

    private Long scanningPeriod = 2000L;



    public IsFileExistThread(StatusBar s,TableView<TablePerson> tableView){
        statusBar = s;
        this.tableView = tableView;

        settings = new Settings();
        parser = new XmlParser(settings);
    }

    /**
     * Отправка запроса (создать xml-файлы запросов в определенной папке)
     * Запрос посылается для всех людей, которые есть в таблице
     * @see #tableView
     *
     * */
    private boolean putDocuments(){
        ObservableList<TablePerson> dataCust;

        // Берем людей из таблицы
        synchronized (tableView) {
             dataCust = tableView.getItems();
        }

        for(TablePerson person : dataCust){
            try {
                if(parser.createDocument(person)){
                    enps.add(person.getEnp());
                }
            } catch (Exception e){
                Platform.runLater(() -> {
                    statusBar.update("Ошибка","Ошибка при создании документа для:" + person.getEnp() + "|-->" + e.getMessage(),0);
                });
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Сканируем директорию, и, если есть ответы
     * записыаем их в enpsGood и удаляем файл (
     * если в настройках не выбрано "сохранять ответы")
     * @param stream директория, которую сканируем
     * */
    private void checkFilesFromStream(DirectoryStream<Path> stream) throws IOException, SAXException, ParserConfigurationException {
        File oiFile;

        List<Path> responseFiles = new ArrayList<>();

        for (Path file : stream) {
            oiFile = file.toFile();
            if (oiFile.isFile() && oiFile.canRead()) {
                String fileEnp = checkEnpsInsideFile(oiFile);
                if(!fileEnp.equals("")){
                    responseFiles.add(file);
                }
            }
        }

        if(!saveResponse){
            LOG.info("DELETE FILES");
            for(Path p : responseFiles){
                LOG.info("Deleting :" + p);
                Files.delete(p);
            }
        }
    }

    /**
     * Сканируем папку для ответов и
     * папку для ошибок
     * */
    private void checkGood(){
        Path pathSnils = Paths.get(directorySnils);
        Path pathSnils1 = Paths.get(directoryError);

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(pathSnils);
            DirectoryStream<Path> stream1 = Files.newDirectoryStream(pathSnils1)){
            checkFilesFromStream(stream);
            checkFilesFromStream(stream1);
        } catch (Exception e){
            Platform.runLater(() -> {
                statusBar.update("Ошибка. Проверка ответов",e.toString(),0);
            });
            e.printStackTrace();
        }
    }

    /**
     * Проверяем пришедший ответ
     * Если файл
     * @return считанный енп или пустую строку, если не нашли енп
     * */
    private String checkEnpsInsideFile(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(file);//в переменной документ лежит результат парсинга

        NodeList clientsId = document.getElementsByTagName("clientId");
        String clientId = clientsId.item(0).getTextContent();

        for(String enp : enps){
            if(clientId.startsWith(enp)){
                enpsGood.add(enp);
                enps.remove(enp);

                String snils = "";
                NodeList snilsNode = document.getElementsByTagName("ns2:Snils");

                if(snilsNode.getLength() == 1){
                    snils = snilsNode.item(0).getTextContent();
                } else {
                    NodeList descriptionNode = document.getElementsByTagName("description");
                    if(descriptionNode.getLength() == 1) {
                        snils = descriptionNode.item(0).getTextContent();
                    }else{
                        NodeList descriptionNodeError = document.getElementsByTagName("ns2:description");
                        if(descriptionNodeError.getLength() == 1){
                            snils = "ошибка";
                        }
                    }
                }

                updateTableRow(enp,snils);

                return enp;
            }
        }
        return "";
    }

    /**
     * Обновляем строку в tableView
     * @param enp енп в таблице tableView
     * @param snils снилс, который записываем в таблцу tableView
     * */
    private void updateTableRow(String enp, String snils){
        synchronized (tableView){
            ObservableList<TablePerson> data = tableView.getItems();

            for(TablePerson p : data){
                if(p.getEnp().equals(enp)){
                    p.setSnils(snils);

                    SnilsDAO.insertPerson(p);
//                    personDAO.insertPerson(p);
//                    snilsSaveDAO.insertPerson(p);
                    break;
                }
            }
            tableView.setItems(data);

            tableView.refresh();
        }
    }



    @Override
    public void run() {

        directoryError = settings.getErrorFolder();

        directorySnils = settings.getResponseFolder();

        timeWait = Long.valueOf(settings.getTimeWait());

        saveResponse = settings.isSaveResponse();

        LOG.info("saveResponse:" + saveResponse);

        Thread thread = Thread.currentThread();
        Platform.runLater(()->{
            statusBar.update("Отправка запроса","",-1);
        });
        //'делаем запрос'
        //создаем документы в папке srv-term03/542202_3s/out
        putDocuments();

        Platform.runLater(()->{
            statusBar.update("Ожидание ответа","",-1);
        });

        int all = enps.size();
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            Runnable fileTask = () -> {
                if (enps.isEmpty() || shutdownTask){
                    LOG.info("task and thread -S-H-U-T-D-O-W-N- ");

                    executorService.shutdownNow();
                    Platform.runLater(()->{
                        statusBar.update("Получено ответов:"+ enpsGood.size() + "/" + all,"",0);
                    });

                    thread.interrupt();
                }
                checkGood();
            };

            long delay = 5000L;

            //запускаем задачу , которая ищет ответы со снилсами, либо ошибки
            executorService.scheduleWithFixedDelay(fileTask,delay,scanningPeriod,TimeUnit.MILLISECONDS);
            Thread.sleep(timeWait * 1000);
            executorService.shutdownNow();
            executorService.awaitTermination(10,TimeUnit.SECONDS);

            throw new InterruptedException("timeout");
        } catch (InterruptedException e){
            Platform.runLater(() -> statusBar.update("Получено ответов:" + enpsGood.size()+ "/" + all,"ожидание остановлено",0));
        }
    }


    /**
     * Остановить поток
     * */
    public void stopThread(){
        shutdownTask = true;
    }

}
