package org.tfoms.snils.model;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name = "personByFIOD",query = "from Person p " +
                "where p.personSurname=:surname " +
                "and p.personFirstname=:firstname " +
                "and p.personLastname=:lastname " +
                "and p.personBirthday=:birthday " ),
        @NamedQuery(name = "personAll", query = "from Person p")
})
public class Person {
    @Id
    private String person_addressid;

    @Column(name = "person_sex")
    private String sex;

    private String enp;

    @Column(name = "person_surname")
    private String personSurname;

    @Column(name = "person_kindfirstname")
    private String personFirstname;

    @Column(name = "person_kindlastname")
    private String personLastname;

    @Column(name = "person_birthday")
    @Temporal(TemporalType.DATE)
    private Date personBirthday;

    @Column(name = "person_serdoc")
    private String personSerdoc;

    @Column(name = "person_numdoc")
    private String personNumdoc;

    @Column(name = "person_linksmoestablishmentid")
    private String smo;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name="person_addressid",referencedColumnName = "personadd_addressid")
    private Personadd personadd;



    public String getPerson_addressid() {
        return person_addressid;
    }

    public void setPerson_addressid(String person_addressid) {
        this.person_addressid = person_addressid;
    }

    public String getSex() {
        return sex.equals("0") ? "Male" : "Female";
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getSmo() {
        return smo;
    }

    public void setSmo(String smo) {
        this.smo = smo;
    }


    public Personadd getPersonadd() {
        return personadd;
    }

    public void setPersonadd(Personadd personadd) {
        this.personadd = personadd;
    }

    @Override
    public String toString() {
        return "Person{" +
                "person_addressid='" + person_addressid + '\'' +
                ", sex='" + sex + '\'' +
                ", enp='" + enp + '\'' +
                ", personSurname='" + personSurname + '\'' +
                ", personFirstname='" + personFirstname + '\'' +
                ", personLastname='" + personLastname + '\'' +
                ", personBirthday=" + personBirthday +
                ", personSerdoc='" + personSerdoc + '\'' +
                ", personNumdoc='" + personNumdoc + '\'' +
                ", smo='" + smo + '\'' +
                ", personadd=" + personadd +
                '}';
    }
}
