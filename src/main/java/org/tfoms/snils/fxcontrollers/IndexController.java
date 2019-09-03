package org.tfoms.snils.fxcontrollers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.tfoms.snils.dao.FindSnilsDAO;
import org.tfoms.snils.dao.SnilsDAO;
import org.tfoms.snils.hibernateDB.HibernateUtil;
import org.tfoms.snils.model.FindSnils;
import org.tfoms.snils.model.TablePerson;
import org.tfoms.snils.xmls.XmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IndexController {
    private final ObservableList<TablePerson> personData = FXCollections.observableArrayList();

    @FXML
    BorderPane parent;

    @FXML
    ProgressBar progressBar;

    @FXML
    Label statusLabel;

    @FXML
    MenuItem menuExport;

    @FXML
    MenuItem menuImport;


    @FXML
    private TableView<TablePerson> personTableview;

    @FXML
    private TableColumn<TablePerson, String> snilsCol;
    @FXML
    private TableColumn<TablePerson,String> enpCol;
    @FXML
    private TableColumn<TablePerson,String> famCol;
    @FXML
    private TableColumn<TablePerson,String> imCol;
    @FXML
    private TableColumn<TablePerson,String> otCol;
    @FXML
    private TableColumn<TablePerson,Date> drCol;
    @FXML
    private TableColumn<TablePerson,String> serdocCol;
    @FXML
    private TableColumn<TablePerson,String> numdocCol;



    private final String statusText = "Здесь будут показываться возможные ошибки";
    private final Tooltip statusTooltip = new Tooltip(statusText);

    private Thread checkFilesExistsThread = new Thread();


    @FXML
    public void initialize(){
        checkFilesExistsThread.interrupt();
        enpCol.setCellValueFactory(new PropertyValueFactory<>("enp"));
        snilsCol.setCellValueFactory(new PropertyValueFactory<>("snils"));
        famCol.setCellValueFactory(new PropertyValueFactory<>("personSurname"));
        imCol.setCellValueFactory(new PropertyValueFactory<>("personFirstname"));
        otCol.setCellValueFactory(new PropertyValueFactory<>("personLastname"));
        drCol.setCellValueFactory(new PropertyValueFactory<>("personBirthday"));
        enpCol.setPrefWidth(140);
        serdocCol.setCellValueFactory(new PropertyValueFactory<>("personSerdoc"));
        numdocCol.setCellValueFactory(new PropertyValueFactory<>("personNumdoc"));
        System.out.println("init column good");

/*      famColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        famColumn.setOnEditCommit( (TableColumn.CellEditEvent<FindSnils,String> event) ->{
            TablePosition<FindSnils, String> pos = event.getTablePosition();

            String newFam = event.getNewValue();
            if(newFam != null) {
                int row = pos.getRow();
                FindSnils person = event.getTableView().getItems().get(row);
                person.setFam(newFam);
                updateEntity(person);
            }
        });*/

        personTableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ContextMenu contextMenu = getContextMenu();
        contextMenu.setOnShowing(event -> contextMenu.getItems().get(0).setDisable(!isRowsSelected()));
        personTableview.setContextMenu(contextMenu);

        ContextMenu contextMenu1 = getContextMenuForStatusLabel();
        System.out.println(contextMenu1);
        System.out.println(contextMenu1.getItems().size());
        try {
            contextMenu1.setOnShowing(event -> {
                System.out.println("on showing");
                contextMenu1.getItems().get(0).setDisable(!checkFilesExistsThread.isAlive());
            });
        }catch (NullPointerException ex){
            System.out.println("npe on showing");
            contextMenu1.getItems().get(0).setDisable(true);
        }
        statusLabel.setContextMenu(contextMenu1);
        System.out.println("init good");
    }

    @FXML
    public void removeSelection(Event event){
        personTableview.getSelectionModel().clearSelection();
    }


    void updateEntity(FindSnils person){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            session.getTransaction().begin();

            session.update(person);

            session.getTransaction().commit();
        }catch(Exception e){

        }
    }


    @FXML
    public void sendQuery(){
        statusLabel.setTooltip(statusTooltip);
        statusLabel.setText("Посылаю запрос");
        progressBar.setProgress(-1);
        ObservableList<TablePerson> dataCust = personTableview.getItems();
        ArrayList<String> enps = new ArrayList<>(dataCust.size());
        ArrayList<String> enpsGood = new ArrayList<>(dataCust.size());



        Thread thread = new Thread(() -> {
            XmlParser parser = new XmlParser();
            int i = 0;
            for(TablePerson person : dataCust){
                try {
                   if(parser.createDocument(person)){
                        enps.add(person.getEnp());
                   }
                   i++;
                } catch (Exception e){
                    System.out.println("bad: " + person);
                    Platform.runLater(() -> {
                        statusLabel.setTooltip(new Tooltip("Ошибка при создании документа для:" + person.getEnp() + "\nОписание ошибки" + e.toString()));
                        statusLabel.setText("Ошибка при создании документа");
                        progressBar.setProgress(0);
                    });
                    return;
                }
            }
        });


        final String directorySnils = "\\\\Srv-term03\\542202_3s\\in\\snils";
        final String directoryError = "\\\\Srv-term03\\542202_3s\\in\\error";

        Thread isFilesExistsThread = new Thread(() -> {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            try {
                thread.join();
                int all = enps.size();

                TimerTask fileTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (enps.isEmpty()){
                            Platform.runLater(()->{
                                statusLabel.setText("Получено ответов:" + enpsGood.size() + "/" + all);
                                progressBar.setProgress(0);
                            });
                            executorService.shutdownNow();
                        }
                        Platform.runLater(() -> {
                            statusLabel.setText( "Ожидание ответа " + enpsGood.size() + "/" + all);
                        });

                        File oiFile;
                        Path pathSnils = Paths.get(directorySnils);
                        try(DirectoryStream<Path> stream = Files.newDirectoryStream(pathSnils)) {
                            for (Path file : stream) {
                                oiFile = file.toFile();
                                if (oiFile.isFile() && oiFile.canRead()) {
                                    String fileEnp = checkEnpsInsideFile(oiFile,enps,enpsGood);
                                    if(!fileEnp.equals("")){
                                        System.out.println("DELETING:" + file.toString());
                                        Files.delete(file);
                                    }
                                }
                            }
                        }catch (Exception e){
                            Platform.runLater(() -> {
                                statusLabel.setText("Ошибка во время сканирования ответов");
                                progressBar.setProgress(0);
                                statusLabel.setTooltip(new Tooltip(e.toString()));
                            });
                        }
                    }
                };
                long delay = 5000L;
                long period = 2000L;

                executorService.scheduleAtFixedRate(fileTask,delay,period, TimeUnit.MILLISECONDS);
                Thread.sleep(1800000);
                executorService.shutdown();
                Platform.runLater(()->{
                    statusLabel.setText("Ответов получено:" + enpsGood.size() + "/" + all);
                    progressBar.setProgress(0);
                });
            } catch (InterruptedException e){
                Platform.runLater(()->{
                    statusLabel.setText("Ошибка во время ожидания ответа.");
                    progressBar.setProgress(0);
                    statusLabel.setTooltip(new Tooltip(e.toString()));
                });
            }
        });
        thread.start();
        isFilesExistsThread.start();
        this.checkFilesExistsThread = isFilesExistsThread;
    }


    private String checkEnpsInsideFile(File file,ArrayList<String> enps,ArrayList<String> enpGood) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);//в переменной документ лежит результат парсинга

        NodeList clientsId = document.getElementsByTagName("clientId");
        String clientId = clientsId.item(0).getTextContent();
        for(String enp : enps){
            if(clientId.startsWith(enp)){
                enpGood.add(enp);
                enps.remove(enp);
                String snils = "";
                NodeList snilsNode = document.getElementsByTagName("ns2:Snils");
                if(snilsNode.getLength() == 1){
                    snils = snilsNode.item(0).getTextContent();
                } else {
                    snils = "нет данных";
                }
                updateTableRow(enp,snils);
                System.out.println("found enp-" + enp);
                return enp;
            }
        }
        return "";
    }


    @FXML
    public void findSnilsGood(){
        Thread findSnilsGoodThread = new Thread(() -> {
            try {
                List<TablePerson> data = SnilsDAO.findSnilsGood();
                synchronized (personData) {
                    personData.clear();
                    personData.addAll(data);
                }

                synchronized (personTableview) {
                    personTableview.setItems(personData);
                    personTableview.refresh();
                }

                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Готово");
                });
            }catch (Exception ex){
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Ошибка при поиске снилсов");
                    statusLabel.setTooltip(new Tooltip(ex.toString()));
                    menuExport.setDisable(false);
                });
            }
        });

        statusLabel.setTooltip(statusTooltip);
        statusLabel.setText("Поиск людей в базе");
        progressBar.setProgress(-1);
        findSnilsGoodThread.start();
    }

    @FXML
    public void importExcel(){
        statusLabel.setTooltip(statusTooltip);

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Импорт данных");
        File file = fileChooser.showOpenDialog(parent.getScene().getWindow());

        if(file == null) return;

        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {
                menuExport.setDisable(true);
                progressBar.setProgress(-1);
                statusLabel.setText("Считывание из экселя");
            });

            try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getPhysicalNumberOfRows();


                ArrayList<String> enps = new ArrayList<>(rows);
                List<TablePerson> data = new ArrayList<>();


                String mode = sheet.getRow(0).getCell(0).getStringCellValue();

                if(mode != null && (mode.equalsIgnoreCase("prizyv") || mode.contains("prizyv"))){
                    System.out.println("prizyw");
                    for (int i = 1; i < rows; i++) {
                        Row row = sheet.getRow(i);
                        try {
                            String surname = row.getCell(0).getStringCellValue();
                            System.out.println("surname + " + surname);
                            String firstname = row.getCell(1).getStringCellValue();
                            System.out.println("first:" + firstname);
                            String lastname = row.getCell(2).getStringCellValue();
                            System.out.println("last:" + lastname);
                            Date birthday = row.getCell(3).getDateCellValue();
                            System.out.println("dat:" + birthday);
                            Cell serCell = row.getCell(4);
                            serCell.setCellType(CellType.STRING);
                            String ser = serCell.getStringCellValue();
                            System.out.println("ser" + ser);
                            Cell numCell = row.getCell(5);
                            numCell.setCellType(CellType.STRING);
                            String num  = numCell.getStringCellValue();
                            System.out.println("num + " + num);
                            data.add(SnilsDAO.findPerson(surname.trim().toUpperCase(),firstname.trim().toUpperCase(),lastname.trim().toUpperCase(),birthday,ser.trim(),num.trim()));
                        } catch (Exception ex) {
                            throw new Exception("Строка " + i + "Ошибка:" + ex.toString());
                        }
                    }
                }else {
                    System.out.println("simple");
                    for (int i = 0; i < rows; i++) {
                        Row row = sheet.getRow(i);
                        try {
                            enps.add(row.getCell(0).getStringCellValue().trim());
                        } catch (Exception ex) {
                            break;
                        }
                    }
                    data = FindSnilsDAO.findPersonByEnp1(enps);
                }

                System.out.println("data[0]:" + data.get(0));

                synchronized (personData) {
                    personData.clear();
                    personData.addAll(data);
                }

                synchronized (personTableview) {
                    personTableview.setItems(personData);
                    personTableview.refresh();
                }

                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Готово");
                    menuExport.setDisable(false);
                });

            }catch (Exception ex){
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Ошибка при импорте");
                    statusLabel.setTooltip(new Tooltip(ex.toString()));
                    menuExport.setDisable(false);
                });
            }
        });
        thread.start();
    }

    @FXML
    public void exportExcel(){
        statusLabel.setTooltip(statusTooltip);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Экспорт в эксель");
        File file = fileChooser.showSaveDialog(parent.getScene().getWindow());

        if(file == null) return;



        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {
                menuImport.setDisable(true);
                progressBar.setProgress(-1);
                statusLabel.setText("Запись в эксель");
            });

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Данные");

                int rownum = 0;
                String[] headerRow={"snils","enp","fam","im","ot","dr","serdoc","numdoc","sex"};

                Row row = sheet.createRow(rownum++);


                for(int i =0; i < headerRow.length; i++) {
                    sheet.setColumnWidth(i, 3500);
                    row.createCell(i).setCellValue(headerRow[i]);
                }

                for(TablePerson x : personData){
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(x.getSnils());
                    row.createCell(1).setCellValue(x.getEnp());
                    row.createCell(2).setCellValue(x.getPersonSurname());
                    row.createCell(3).setCellValue(x.getPersonFirstname());
                    row.createCell(4).setCellValue(x.getPersonLastname());
                    row.createCell(5).setCellValue(x.getPersonBirthday().toString());
                    row.createCell(6).setCellValue(x.getPersonSerdoc());
                    row.createCell(7).setCellValue(x.getPersonNumdoc());
                    row.createCell(8).setCellValue(x.getSex());
                }
                workbook.write(new FileOutputStream(file));
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Экспорт готово");
                    menuImport.setDisable(false);
                });
            }catch (Exception ex){
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    statusLabel.setText("Ошибка при экспорте");
                    statusLabel.setTooltip(new Tooltip(ex.toString()));
                    menuImport.setDisable(false);
                });
            }
        });
        thread.start();
    }

    @FXML
    public void deleteRows(Event event){
        ObservableList<TablePerson> selectedRows = personTableview.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выделенные записи из таблицы?\nВыбрано записей:" + selectedRows.size());

        alert.setHeaderText("Удаление записей");
        alert.setTitle("Подтвердите действие");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ArrayList<TablePerson> rows = new ArrayList<>(selectedRows);
            rows.forEach(row ->{ personTableview.getItems().remove(row);personData.remove(row);});
        }
    }


    private ContextMenu getContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Удалить выделенное");
        item1.setOnAction(event -> deleteRows(event));
        contextMenu.getItems().add(item1);

        return contextMenu;
    }

    private ContextMenu getContextMenuForStatusLabel(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Перестать ожидать");
        item1.setOnAction(event -> {checkFilesExistsThread.interrupt(); statusLabel.setText("Ожидание прекращено");});
        contextMenu.getItems().add(item1);
        return contextMenu;
    }

    private boolean isRowsSelected(){
        return personTableview.getSelectionModel().getSelectedItems().size() > 0;
    }

    private void updateTableRow(String enp, String snils){
        synchronized (personTableview){
            ObservableList<TablePerson> data = personTableview.getItems();
            System.out.println("before set:" + data.get(0));
            for(TablePerson p : data){
                if(p.getEnp().equals(enp)){
                    p.setSnils(snils);
                    System.out.println("enp:" + enp + " snils:" + snils);
                    SnilsDAO.insertPerson(p);
                    break;
                }
            }
//            System.out.println("after set:" + data.get(0));
            personTableview.setItems(data);
            personTableview.refresh();
//            System.out.println("updateTableRow good");
        }
    }

}
