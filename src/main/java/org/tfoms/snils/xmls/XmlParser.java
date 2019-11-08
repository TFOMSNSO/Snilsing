package org.tfoms.snils.xmls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tfoms.snils.dao.PersonDAO;
import org.tfoms.snils.model.FindSnils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.tfoms.snils.model.Personadd;
import org.tfoms.snils.model.TablePerson;
import org.tfoms.snils.model.ui.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class XmlParser {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Settings settings;

    // директория, в которой будем создавать файлы
    private String directoryRequest;

    protected int i = 0;

    protected int j = 0;

    @Autowired
    public XmlParser(Settings settings){
        this.settings = settings;
        directoryRequest = settings.getRequestFolder();
    }

    /**
     * Создаем xml документ в directoryRequest
     * */
    public boolean createDocument(TablePerson person) throws ParserConfigurationException
            , TransformerException{

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element realRoot = document.createElement("tns1:ClientMessage");
            realRoot.setAttribute("xmlns:tns1","urn://x-artefacts-smev-gov-ru/services/service-adapter/types");
            realRoot.setAttribute("xmlns:ns2","urn://x-artefacts-smev-gov-ru/services/service-adapter/types/faults");
            document.appendChild(realRoot);

            Element system = document.createElement("tns1:itSystem");
            system.appendChild(document.createTextNode("542202_3S"));
            realRoot.appendChild(system);


            Element requestMessage = document.createElement("tns1:RequestMessage");
            Element messageType = document.createElement("tns1:messageType");
            messageType.appendChild(document.createTextNode("RequestMessageType"));

            requestMessage.appendChild(messageType);


            Element requestMetaData = document.createElement("tns1:RequestMetadata");
            Element clientId = document.createElement("tns1:clientId");
            clientId.appendChild(document.createTextNode(person.getEnp() + "_" + UUID.randomUUID().toString().substring(0,23)));
            Element testMessage = document.createElement("testMessage");
            testMessage.appendChild(document.createTextNode("false"));
            requestMetaData.appendChild(clientId);
            requestMetaData.appendChild(testMessage);

            requestMessage.appendChild(requestMetaData);
            Element requestContent = document.createElement("tns1:RequestContent");
            Element content = document.createElement("tns1:content");
            Element messagePrimaryContent = document.createElement("tns1:MessagePrimaryContent");

            // root element
            Element root = document.createElement("tns:SnilsByAdditionalDataRequest");
            root.setAttribute("xmlns:smev","urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1");
            root.setAttribute("xmlns:pfr","http://common.kvs.pfr.com/1.0.0");
            root.setAttribute("xmlns:tns","http://kvs.pfr.com/snils-by-additionalData/1.0.1");

            // фамилия
            Element familyName = document.createElement("smev:FamilyName");
            familyName.appendChild(document.createTextNode(person.getPersonSurname()));
            root.appendChild(familyName);

            // фамилия
            Element firstName = document.createElement("smev:FirstName");
            firstName.appendChild(document.createTextNode(person.getPersonFirstname()));
            root.appendChild(firstName);

            // фамилия
            Element patronymic = document.createElement("smev:Patronymic");
            patronymic.appendChild(document.createTextNode(person.getPersonLastname()));
            root.appendChild(patronymic);

            Element birthDate = document.createElement("tns:BirthDate");
            birthDate.appendChild(document.createTextNode(dateFormat.format(person.getPersonBirthday())));
            root.appendChild(birthDate);

            Element gender = document.createElement("tns:Gender");
            gender.appendChild(document.createTextNode(person.getSex() == null ? "" : person.getSex()));
            root.appendChild(gender);


            String[] addr = tryParse(person.getPersonadd());
            if(addr.length == 5){
                Element birthPlace = document.createElement("tns:BirthPlace");

                Element placeType = document.createElement("pfr:PlaceType");
                placeType.appendChild(document.createTextNode(addr[0]));
                birthPlace.appendChild(placeType);

                if(addr[1].length() != 0) {
                    Element settlement = document.createElement("pfr:Settlement");
                    settlement.appendChild(document.createTextNode(addr[1]));
                    birthPlace.appendChild(settlement);
                }

                if(addr[2].length() != 0) {
                    Element disctrict = document.createElement("pfr:District");
                    disctrict.appendChild(document.createTextNode(addr[2]));
                    birthPlace.appendChild(disctrict);
                }
                if(addr[3].length() != 0) {
                    Element reg = document.createElement("pfr:Region");
                    reg.appendChild(document.createTextNode(addr[3]));
                    birthPlace.appendChild(reg);
                }
                if(addr[4].length() != 0) {
                    Element contr = document.createElement("pfr:Country");
                    contr.appendChild(document.createTextNode(addr[4]));
                    birthPlace.appendChild(contr);
                }
                root.appendChild(birthPlace);
            }


            Element passport = document.createElement("smev:PassportRF");

            Element series = document.createElement("smev:Series");
            series.appendChild(document.createTextNode(person.getPersonSerdoc() == null ? "" : person.getPersonSerdoc().replaceAll(" ","")));
            passport.appendChild(series);

            Element number = document.createElement("smev:Number");
            number.appendChild(document.createTextNode(person.getPersonNumdoc() == null ? "" : person.getPersonNumdoc()));
            passport.appendChild(number);

            if(person.getPersonadd() != null) {
                if (person.getPersonadd().getDatepassport() != null) {
                    Element issueDate = document.createElement("smev:IssueDate");
                    issueDate.appendChild(document.createTextNode(person.getPersonadd().getDatepassport() != null ? dateFormat.format(person.getPersonadd().getDatepassport()) : "-"));
                    passport.appendChild(issueDate);
                }
                if (person.getPersonadd().getDok_vi() != null) {
                    Element issuer = document.createElement("smev:Issuer");
                    issuer.appendChild(document.createTextNode(person.getPersonadd().getDok_vi() != null ? person.getPersonadd().getDok_vi() : "-"));
                    passport.appendChild(issuer);
                }
            }
            root.appendChild(passport);

            messagePrimaryContent.appendChild(root);
            content.appendChild(messagePrimaryContent);
            requestContent.appendChild(content);
            requestMessage.appendChild(requestContent);

            realRoot.appendChild(requestMessage);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File( directoryRequest + person.getEnp() + ".xml"));
            transformer.transform(domSource, streamResult);
            return true;
    }

    /**
     * Метод парсит место рождения
     * @see 'https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html'
     * @param p - Экземпляр Personadd в котором парсим место рождения(поле - 'born'
     * @return Массив вида: [placeType,Settlement,District,Region,Country]
     *         или пустой массив, если не получилось
     * */
    public String[] tryParse(Personadd p){
        final String simplePlaceType = "СТАНДАРТНОЕ";
        final String specificPlaceType = "ОСОБОЕ";
        final String regionType1 = "ОБЛАСТЬ";
        final String regionType2 = "КРАЙ";


        if(p == null){
            j++;
            return new String[]{};
        }

        String bornString = p.getBorn();

        if(bornString == null || bornString.length() == 0){
            j++;
            return new String[]{};
        }
        // out values
        String outPlaceType = "";
        String outSettlement = "";
        String outDistrict = "";
        String outRegion = "";
        String outCountry = "";

        String regionType = "";

        bornString = bornString.toUpperCase();


        /*
            https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html - документация по регулярным выражениям
         \\s - пробельный символ
         | - логическое или
         + - 1 или больше
         * - 0 или больше
         ? - 1 или 0
         [abc] - любой из символов abc
         {2,} - 2 и больше повторения

         */

        bornString = bornString.replaceAll("\\s?\\.",". ");

        bornString = bornString.replace(","," ");
        //удаляем лишние пробелы
        bornString = bornString.trim().replaceAll("[ ]{2,}"," ");

        //на этом моменте у нас строка без двойных пробелов.
        // После каждой точки гарантированно стоит пробел.
        // Запятых нет. Начальных и конечных пробелов нет.

        if(bornString.matches(".*(РФ|РОССИЯ|РОССИЙСКАЯ ФЕДЕРАЦИЯ).*")){
            bornString = bornString.replaceAll("(РФ|РОССИЯ|РОССИЙСКАЯ ФЕДЕРАЦИЯ)","");
            outCountry = "РОССИЙСКАЯ ФЕДЕРАЦИЯ";
        }


        bornString = bornString.replaceAll("(РЕСП\\. |РЕС\\. |РЕСП )" ,"РЕСПУБЛИКА ");


        //заменяем всякие названия на "ГОРОД"

        bornString = bornString.replaceAll("(Р\\. П\\. |Р-П|ПОС\\. Г\\. Т\\. |П\\. Г\\. Т\\. |Р\\. ПОС\\. |Д\\. П\\. |" +
                "Г\\. П\\. |РАБ\\. ПОС\\. |Х\\. КР\\. |ПГТ\\. |А\\. |РП\\. |Г\\. |С/З|С\\. |Д\\. |ДЕР\\. |СТ\\. |ГОР\\. |" +
                "ПОС\\. |П\\. |СТАНЦИЯ|СОВХОЗ|ПОСЕЛОК|ПОСЁЛОК|Р-Д|С-З|ДЕРЕВНЯ|С/Х|С\\\\Х|К/С|Ш-ТА|З/С|РАБОЧИЙ ГОРОД|Р\\. П|" +
                "СТАН\\.|С\\\\С|С\\\\З|Б\\\\П|К\\. |Х\\. |С/С Ф\\. |СЕЛ\\. |С-С |УЧ\\. |ГО\\. |ПР\\. )","ГОРОД ");

        bornString = bornString.replaceAll("НСО"," НОВОСИБИРСКАЯ ОБЛ. ");

        bornString = bornString.replaceAll("РАБОЧИЙ ","");

        // еще раз удаляем пробелы, так как в предыдущем выражении они могут добавиться
        bornString = bornString.trim().replaceAll("[ ]{2,}"," ");


        // e.g. "НОВОСИБИРСК" / "КАЗАХСТАН"
        if(!bornString.contains(" ") && !bornString.contains(".")){
            if(!bornString.equals("КАЗАХСТАН"))
                return new String[]{specificPlaceType, bornString,"","",""};
            else
                return new String[]{specificPlaceType, "","","",bornString};
        }
        // класс символов, которые обознача=ют город
        final String towns = "(ГОР|ГОРОД|Г|СЕЛО|С|П|ПОС|ДЕР|ПГТ|РП|Д|СТ|АУЛ|ХУТОР|РАЗЬЕЗД|СЕЛ|ФЕРМА|А|С/СОВЕТ)";
        // класс символов, которые обозначают район
        final String districts = "(РАЙОН|РАЙОНА|Р-ОН|Р-ОНА|Р-НА|Р-Н|Р|Р\\.)";
        // класс символов, которые обозначают область/край
        final String regions = "(ОБЛАСТЬ|ОБЛ\\.|ОБЛАСТИ|ОБЛ|О\\.|КРАЙ|КРАЯ|КР\\.|КР)";
        // класс символов, которые обозначают любое слово
        final String name = "[А-Я\\-Ё0-9]+";
        // класс символов, которые обозначают республику
        final String countries = "(РЕСПУБЛИКА|РЕСП.|РЕСП|РЕСУБЛИКА|СТРАНА)";


        // Перебираем форматы адресов
        // В базе все адреса записаны по-разному
        // Начинаем, примерно, с самых популярных форматов
        if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s%s\\s%s.*", towns,name,name,districts,name,regions),bornString)){
            //e.g. "С. ФЕДОРОВКА СЕВЕРНОГО РАЙОНА НОВОСИБИРСКОЙ ОБЛАСТИ"
            String addr[] = bornString.split("\\s");
            outPlaceType = simplePlaceType;// СТАНДАРТНОЕ
            outSettlement = addr[1];
            outDistrict = addr[2];                                              //1
            outRegion = addr[4];
            regionType = addr[5];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s%s.*",name,name,districts,name,regions),bornString)){
            // e.g. "ВЕРХ-ТАРКА КЫШТОВСКОГО РАЙОНА НОВОСИБИРСКОЙ ОБЛАСТИ"
            String addr[] = bornString.split("\\s");
            outPlaceType = simplePlaceType;// СТАНДАРТНОЕ
            outSettlement = addr[0];                                            //2
            outDistrict = addr[1];
            outRegion = addr[3];
            regionType = addr[4];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s%s\\s%s.*",name,towns,name,districts,name,regions),bornString)){
            // e.g. "ПРОЛЕТАРСКИЙ ГОРОД ОРДЫНСКОГО Р-НА НОВОСИБИРСКОЙ ОБЛ"
            String addr[] = bornString.split("\\s");
            outPlaceType = simplePlaceType;// СТАНДАРТНОЕ
            outSettlement = addr[0];                                            //3
            outDistrict = addr[2];
            outRegion = addr[4];
            regionType = addr[5];
        }else if(Pattern.matches(String.format("%s\\s%s.*",towns,name),bornString)){
            // e.g. "ГОР. НОВОСИБИРСК"
            // or "Г. ТЕЛЬ-АВИВ-ЯФФО ИЗРАИЛЬ"
            String addr[] = bornString.split("\\s");                    //4
            outPlaceType = specificPlaceType; // ОСОБОЕ
            outSettlement = addr[1];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s%s\\s%s.*",regions,name,districts,name,towns,name),bornString)){
            //e.g. "ОБЛ НОВОСИБИРСКАЯ Р-Н МАСЛЯНИНСКИЙ Д ЖЕРНОВКА"
            outPlaceType = simplePlaceType;
            String addr[] = bornString.split("\\s");
            regionType = addr[0];                                             //5
            outRegion = addr[1];
            outDistrict = addr[3];
            outSettlement = addr[5];
        }else if(Pattern.matches(String.format("s\\s%s\\s%s\\s%s\\s%s\\s%s.*",districts,name,regions,name,towns,name),bornString)){
            //e.g. "ОБЛ НОВОСИБИРСКАЯ Р-Н МАСЛЯНИНСКИЙ Д ЖЕРНОВКА"
            outPlaceType = simplePlaceType;
            String addr[] = bornString.split("\\s");
            regionType = addr[0];                                             //5
            outRegion = addr[1];
            outDistrict = addr[3];
            outSettlement = addr[5];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s.*",towns,name,name,regions),bornString)){
            // e.g. "... ПОС. ОРЛОВКА ТОМСКОЙ ОБЛ ..."
            String addr[] = bornString.split("\\s");                    //6
            outPlaceType = specificPlaceType;
            outSettlement = addr[1];
            outRegion = addr[2];
            regionType = addr[3];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s.*",towns,name,name,districts),bornString)){
            //e.g. "... С. РОЖДЕСТВЕНКА КУПИНСКОГО РАЙОНА ...."
            outPlaceType = specificPlaceType;                                 //7
            String[] addr = bornString.split("\\s");
            outSettlement = addr[1];
            outDistrict = addr[2];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s.*",name,towns,name,districts),bornString)){
            //e.g. "... С. РОЖДЕСТВЕНКА КУПИНСКОГО РАЙОНА ...."
            outPlaceType = specificPlaceType;                                 //7
            String[] addr = bornString.split("\\s");
            outSettlement = addr[0];
            outDistrict = addr[2];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s.*",name,towns,name,regions),bornString)){
            //e.g. "... С. РОЖДЕСТВЕНКА КУПИНСКОГО РАЙОНА ...."
            outPlaceType = specificPlaceType;                                 //7
            String[] addr = bornString.split("\\s");
            outSettlement = addr[0];
            outRegion = addr[2];
            regionType = addr[3];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s%s\\s%s.*",name,regions,name,districts,towns,name),bornString)){
            //e.g. "ДОНЕЦКОЙ ОБЛ МАКЕЕВСКОГО Р-НА С. МАКЕЕВКА"
            String addr[] = bornString.split("\\s");                    //8
            outPlaceType = simplePlaceType;
            outSettlement = addr[5];
            outRegion = addr[0];
            regionType = addr[1];
            outDistrict = addr[2];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s.*",regions,name,towns,name),bornString)){
            //e.g. "ОБЛ НОВОСИБИРСКАЯ Г БАРАБИНСК"
            String addr[] = bornString.split("\\s");                    //10
            outPlaceType = specificPlaceType;
            outSettlement = addr[3];
            outRegion = addr[1];
            regionType = addr[0]; // область или край
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s.*",name,name,regions),bornString)){
            //e.g. "БЕЛОВО КЕМЕРОВСКАЯ ОБЛ."
            String addr[] = bornString.split("\\s");                    //11
            outPlaceType = specificPlaceType;
            outSettlement = addr[0];
            outRegion = addr[1];
            regionType = addr[2];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s.*",name,name,districts),bornString)){
            //e.g. "БЕЛОВО КЕМЕРОВСКАЯ ОБЛ."
            String addr[] = bornString.split("\\s");                    //12
            outPlaceType = specificPlaceType;
            outSettlement = addr[0];
            outDistrict = addr[1];
        }else if(Pattern.matches(String.format("%s\\s%s\\s?.*",name,regions),bornString)){
            //e.g. "ЧИТИНСКАЯ ОБЛ"
            String addr[] = bornString.split("\\s");                //13
            outPlaceType = specificPlaceType;
            outRegion = addr[0];
            regionType = addr[1];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s?.*",name,districts,name,regions),bornString)){
            //e.g. "СПИРОВСКИЙ Р-Н КАЛИНИНСКОЙ ОБЛ."
            String addr[] = bornString.split("\\s");                //14
            outPlaceType = specificPlaceType;
            outDistrict = addr[0];
            outRegion = addr[2];
            regionType = addr[3];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s\\s%s\\s?.*",name,name,name,regions),bornString)){
            //e.g. "НОВОКЛЮЧИ КУПИНСКОГО НОВОСИБИРСКАЯ ОБЛ."
            String addr[] = bornString.split("\\s");                //15
            outPlaceType = specificPlaceType;
            outSettlement = addr[0];
            outDistrict = addr[1];
            outRegion = addr[2];
            regionType = addr[3];
        }else if(Pattern.matches(String.format("%s\\s%s.*",countries,name),bornString)){
            //e.g. "РЕСПУБЛИКА МОЛДОВА"
            String addr[] = bornString.split("\\s");                //16
            outPlaceType = specificPlaceType;
            outCountry = addr[1];
        }else if(Pattern.matches(String.format("%s\\s%s.*",name,countries),bornString)){
            //e.g. "МОЛДОВА РЕСПУБЛИКА"
            String addr[] = bornString.split("\\s");                //17
            outPlaceType = specificPlaceType;
            outCountry = addr[0];
        }else if(Pattern.matches(String.format("%s\\s%s\\s%s",name,name,name),bornString)){
            //e.g. "МОЛДОВА РЕСПУБЛИКА"
            String addr[] = bornString.split("\\s");                //17
            outPlaceType = simplePlaceType;
            outSettlement = addr[0];
            outDistrict = addr[1];
            outRegion = addr[2];
            regionType = regionType1;
        }else if(Pattern.matches(String.format(".*%s\\s%s",towns,name),bornString)){
            //e.g. "МОЛДОВА РЕСПУБЛИКА"
            String addr[] = bornString.split("\\s");                //17
            outPlaceType = specificPlaceType;
            outSettlement = addr[addr.length - 1];
        }











        // Меняем падеж на именительный
        if(regionType.startsWith("О")){
            if(outRegion.endsWith("КОЙ")){
                outRegion = outRegion.replaceAll("КОЙ$", "КАЯ ОБЛАСТЬ");
            }else{
                outRegion = outRegion + " " + regionType1;// + область
            }
        }else if(regionType.startsWith("К")){
            if(outRegion.endsWith("КОГО")){
                outRegion = outRegion.replaceAll("КОГО$","КИЙ КРАЙ");
            }else{
                outRegion = outRegion + " " + regionType2;// + край
            }
        }

        //Меняем падеж на именительный
        if(outDistrict.endsWith("КОГО")){
            outDistrict = outDistrict.replaceAll("КОГО$","КИЙ");
        }else if(outDistrict.endsWith("НОГО")){
            outDistrict.replaceAll("НОГО$","НЫЙ");
        }

        if(outPlaceType.length() == 0){
            i++;
            System.out.println("|" + bornString + "|");
        }


        return new String[]{outPlaceType, outSettlement, outDistrict, outRegion, outCountry};
    }
}
