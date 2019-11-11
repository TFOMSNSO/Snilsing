package org.tfoms.snils.xmls;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.tfoms.snils.model.Person;
import org.tfoms.snils.model.Personadd;
import org.tfoms.snils.model.ui.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;



public class XmlParserTest {
    private List<Person> people = new ArrayList<>();

    private XmlParser parser;

    private final int peopleToTestCount = 10000;

    @Before
    public void setUp() throws Exception {
        EntityManager em = Persistence.createEntityManagerFactory("developerUnit").createEntityManager();
        people = em.createNamedQuery("personAll",Person.class).setMaxResults(peopleToTestCount).getResultList();
        System.out.println("people array length:" + people.size());
        em.close();

        parser = new XmlParser(new Settings());
    }

    @Ignore
    @Test
    public void parseBirthPlace() {
        for(Person p : people){
            Personadd pa = p.getPersonadd();
            String[] el = parser.tryParse(pa);

            if(el.length == 5)
                assertTrue("|" + pa.getBorn() + "-> " + Arrays.toString(el), el[0].equals("ОСОБОЕ") || el[0].equals("СТАНДАРТНОЕ"));
        }
        System.out.println(parser.j);
        System.out.println(parser.i);
    }
}