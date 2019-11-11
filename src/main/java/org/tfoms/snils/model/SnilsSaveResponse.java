package org.tfoms.snils.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SNILS_SAVE_RESPONSE_NEW")
@NamedQueries({
        @NamedQuery(name="findSnilsBySnils",query = "from SnilsSaveResponse s where s.snils = :snilsnum"),
        @NamedQuery(name="findSnilsById",query = "from SnilsSaveResponse  s where s.personBirthday = :birthday " +
                "                                                            and s.personFirstname = :firstname " +
                                                                            "and s.personSurname=:surname " +
                                                                            "and s.personLastname = :lastname "),
        @NamedQuery(name = "findSnilsGood",query = "from SnilsSaveResponse s order by s.dateInsert, s.personSurname, s.personFirstname, s.personLastname "),
        @NamedQuery(name = "findSnilsGoodByEnp", query = "from SnilsSaveResponse s where s.enp = :enp")
})
public class SnilsSaveResponse {
    @Id
    private String enp;

    @Column(name = "SNILSNUM")
    private String snils;

    @Column(name = "GENDER")
    private String sex;

    @Column(name = "SERDOC")
    private String personSerdoc;

    @Column(name = "NUMDOC")
    private String personNumdoc;

    @Column(name = "FAMILYNAME")
    private String personSurname;

    @Column(name = "FIRSTNAME")
    private String personFirstname;

    @Column(name = "PATRONYMIC")
    private String personLastname;

    @Column(name = "BIRTHDATE")
    @Temporal(TemporalType.DATE)
    private Date personBirthday;

    @Column(name = "D_INSERT")
    private Date dateInsert;

    public SnilsSaveResponse() { }

    public SnilsSaveResponse(TablePerson person) {
        this.personSurname = person.getPersonSurname();
        this.personFirstname = person.getPersonFirstname();
        this.personLastname = person.getPersonLastname();
        this.personBirthday = person.getPersonBirthday();
        this.personSerdoc = person.getPersonSerdoc();
        this.personNumdoc = person.getPersonNumdoc();
        this.snils = person.getSnils();
        this.sex = person.getSex();
        this.enp = person.getEnp();
    }

    public Date getPersonBirthday() {
        return personBirthday;
    }

    @Override
    public String toString() {
        return "SnilsSaveResponse{" +
                "enp='" + enp + '\'' +
                ", snils='" + snils + '\'' +
                ", sex='" + sex + '\'' +
                ", personSerdoc='" + personSerdoc + '\'' +
                ", personNumdoc='" + personNumdoc + '\'' +
                ", personSurname='" + personSurname + '\'' +
                ", personFirstname='" + personFirstname + '\'' +
                ", personLastname='" + personLastname + '\'' +
                ", personBirthday=" + personBirthday +
                ", dateInsert=" + dateInsert +
                '}';
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

    public String getSnilsnum() {
        return snils;
    }

    public void setSnilsnum(String snilsnum) {
        this.snils = snilsnum;
    }

    public String getEnp() {
        return enp;
    }

    public void setEnp(String enp) {
        this.enp = enp;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public void setPersonBirthday(Date personBirthday) {
        this.personBirthday = personBirthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public Date getDateInsert() {
        return dateInsert;
    }

    public void setDateInsert(Date dateInsert) {
        this.dateInsert = dateInsert;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(!(obj instanceof SnilsSaveResponse)) return false;

        SnilsSaveResponse other = (SnilsSaveResponse) obj;

        if(!enp.equals(other.enp)) return false;
        if(!snils.equals(other.snils)) return false;
        if(!sex.equals(other.sex)) return false;
        if(!personSurname.equals(other.personSurname)) return false;
        if(!personFirstname.equals(other.personFirstname)) return false;
        if(!personLastname.equals(other.personLastname)) return false;
        if(!personBirthday.equals(other.personBirthday)) return false;
        if(!personNumdoc.equals(other.personNumdoc)) return false;
        if(!personSerdoc.equals(other.personSerdoc)) return false;
        if(!dateInsert.equals(other.dateInsert)) return false;

        return true;
    }
}
