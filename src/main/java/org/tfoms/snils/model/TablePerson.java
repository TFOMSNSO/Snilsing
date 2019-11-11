package org.tfoms.snils.model;

import javafx.scene.control.Tab;

import java.util.Date;




/**
 * Экземпляры этого класса отображаюся в Tableview
 * */
public class TablePerson {
    private String snils;
    private String enp;
    private String personSurname;
    private String personFirstname;
    private String personLastname;
    private Date personBirthday;
    private String personSerdoc;
    private String personNumdoc;
    private Personadd personadd;
    private String sex;

    public TablePerson() {
    }

    public TablePerson(SnilsSaveResponse s){
        this.snils = s.getSnils();
        this.enp = s.getEnp();
        this.personSurname = s.getPersonSurname();
        this.personFirstname = s.getPersonFirstname();
        this.personLastname = s.getPersonLastname();
        this.personBirthday = s.getPersonBirthday();
        this.personSerdoc = s.getPersonSerdoc();
        this.personNumdoc = s.getPersonNumdoc();
        this.personSerdoc = s.getPersonSerdoc();
        this.personNumdoc = s.getPersonNumdoc();
        this.sex = s.getSex();
    }

    public TablePerson(Prizyvnik person,Personadd personadd){
        this.snils = "-";
        this.enp = person.getEnpVnutr();
        this.personSurname = person.getFam();
        this.personFirstname = person.getIm();
        this.personLastname = person.getOt();
        this.personBirthday = person.getDr();
        this.personSerdoc = person.getSer();
        this.personNumdoc = person.getNum();
        this.personadd = personadd;
        this.sex = person.getSex();
    }

    public TablePerson(Person person){
        this.snils = "-";
        this.enp = person.getEnp();
        this.personSurname = person.getPersonSurname();
        this.personFirstname = person.getPersonFirstname();
        this.personLastname = person.getPersonLastname();
        this.personBirthday = person.getPersonBirthday();
        this.personSerdoc = person.getPersonSerdoc();
        this.personNumdoc = person.getPersonNumdoc();
        this.personadd = person.getPersonadd();
        this.sex = person.getSex();
    }



    public Personadd getPersonadd() {
        return personadd;
    }

    public void setPersonadd(Personadd personadd) {
        this.personadd = personadd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSnils(){
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getEnp() {
        return enp;
    }

    public void setEnp(String enp) {
        this.enp = enp;
    }

    public String getPersonSurname() {
        return personSurname;
    }

    public void setPersonSurname(String personSurname) {
        this.personSurname = personSurname;
    }

    public String getPersonFirstname() {
        return personFirstname;
    }

    public void setPersonFirstname(String personFirstname) {
        this.personFirstname = personFirstname;
    }

    public String getPersonLastname() {
        return personLastname;
    }

    public void setPersonLastname(String personLastname) {
        this.personLastname = personLastname;
    }

    public Date getPersonBirthday() {
        return personBirthday;
    }

    public void setPersonBirthday(Date personBirthday) {
        this.personBirthday = personBirthday;
    }

    public String getPersonSerdoc() {
        return personSerdoc;
    }

    public void setPersonSerdoc(String personSerdoc) {
        this.personSerdoc = personSerdoc;
    }

    public String getPersonNumdoc() {
        return personNumdoc;
    }

    public void setPersonNumdoc(String personNumdoc) {
        this.personNumdoc = personNumdoc;
    }

    @Override
    public String toString() {
        return "TablePerson{" +
                "snils='" + snils + '\'' +
                ", enp='" + enp + '\'' +
                ", personSurname='" + personSurname + '\'' +
                ", personFirstname='" + personFirstname + '\'' +
                ", personLastname='" + personLastname + '\'' +
                ", personBirthday=" + personBirthday +
                ", personSerdoc='" + personSerdoc + '\'' +
                ", personNumdoc='" + personNumdoc + '\'' +
                ", personadd=" + personadd +
                ", sex='" + sex + '\'' +
                '}';
    }
}
