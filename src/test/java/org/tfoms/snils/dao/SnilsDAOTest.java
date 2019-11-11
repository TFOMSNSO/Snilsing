package org.tfoms.snils.dao;

import org.junit.Before;
import org.junit.Test;
import org.tfoms.snils.model.SnilsSaveResponse;
import org.tfoms.snils.model.TablePerson;

import java.util.Date;

import static org.junit.Assert.*;

public class SnilsDAOTest {
    TablePerson person;



    @Before
    public void setUp() throws Exception {
        person = new TablePerson();
        person.setSnils("");//04613960351
        person.setEnp("5447330891000565");
        person.setPersonSurname("БОЧКАРЕВА");
        person.setPersonFirstname("ИРИНА");
        person.setPersonLastname("МИХАЙЛОВНА");
        person.setPersonBirthday(new Date());//08.12.1966
        person.setPersonSerdoc("32 11");
        person.setPersonNumdoc("073558");
        person.setSex("Female");
    }

    @Test
    public void insertPerson() {
        SnilsSaveResponse mock = SnilsDAO.findPerson("5447330891000565");
        assertNotNull(mock);

        //тут не должен вставить
        SnilsDAO.insertPerson(person);

        SnilsSaveResponse mock1 = SnilsDAO.findPerson("5447330891000565");

        assertNotNull(mock1);

        assertEquals(mock,mock1);

        person.setSnils(mock1.getSnils());
        person.setPersonBirthday(mock1.getPersonBirthday());

        //тут должен вставить
        SnilsDAO.insertPerson(person);

        SnilsSaveResponse mock2 = SnilsDAO.findPerson("5447330891000565");

        assertNotEquals(mock2,mock1);
    }



}