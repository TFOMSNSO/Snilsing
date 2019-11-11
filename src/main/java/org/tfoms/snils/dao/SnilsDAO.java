package org.tfoms.snils.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tfoms.snils.model.Person;
import org.tfoms.snils.model.Prizyvnik;
import org.tfoms.snils.model.SnilsSaveResponse;
import org.tfoms.snils.model.TablePerson;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SnilsDAO {
    static final Logger LOG = LoggerFactory.getLogger(SnilsDAO.class);

    private static EntityManager em = Persistence.createEntityManagerFactory("developerUnit").createEntityManager();

    /**
     * Возвращает все записи из таблицы SNILS_SAVE_RESPONSE_NEW (developer@dame)
     * */
    public static List<TablePerson> findSnilsGood(){
        EntityManager em = Persistence.createEntityManagerFactory("developerUnit").createEntityManager();

        List<SnilsSaveResponse> snilses = em.createNamedQuery("findSnilsGood",SnilsSaveResponse.class).getResultList();
        ArrayList<TablePerson> data = new ArrayList<>();

        for(SnilsSaveResponse s : snilses){
            data.add(new TablePerson(s));
        }
        em.close();
        return data;
    }

    /**
     * Поиск человека по ФИОД в таблице person (developer@dame)
     * */
    public static TablePerson findPerson(String surname,String firstname,String lastname,Date birthday,String trueSer,String trueNum){
        EntityManager em = Persistence.createEntityManagerFactory("developerUnit").createEntityManager();
        Person person = em.createNamedQuery("personByFIOD",Person.class)
                .setParameter("surname",surname)
                .setParameter("firstname",firstname)
                .setParameter("lastname",lastname)
                .setParameter("birthday",birthday).getSingleResult();

        TablePerson tablePerson = new TablePerson(person);
        tablePerson.setPersonSerdoc(trueSer);
        tablePerson.setPersonNumdoc(trueNum);
        tablePerson.setPersonadd(person.getPersonadd());
        em.close();
        return tablePerson;
    }

    /**
     * Вставка человека с полисом в SNILS_SAVE_RESPONSE_NEW (developer@dame)
     * */
    public static void insertPerson(TablePerson person){
        if(person == null) return;

        String personSnils = person.getSnils();

        if(personSnils == null || personSnils.length() != 11 ||
                personSnils.toLowerCase().contains("ошибка")) return;


        SnilsSaveResponse snilsPerson = new SnilsSaveResponse(person);
        snilsPerson.setDateInsert(new Date());


        em.getTransaction().begin();
        SnilsSaveResponse snilsSaveResponse = em.find(SnilsSaveResponse.class, person.getEnp());



        if(snilsSaveResponse == null){
            em.persist(snilsPerson);
        }else{
            em.remove(snilsSaveResponse);
            em.persist(snilsPerson);
        }
        em.getTransaction().commit();
    }

    public static SnilsSaveResponse findPerson(String enp){
        return em.find(SnilsSaveResponse.class,enp);
    }
}
