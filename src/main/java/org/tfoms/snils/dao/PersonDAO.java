package org.tfoms.snils.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.tfoms.snils.model.Person;
import org.tfoms.snils.model.SnilsSaveResponse;
import org.tfoms.snils.model.TablePerson;
import org.tfoms.snils.repo.PersonsRepository;
import org.tfoms.snils.repo.SnilsPersonRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class PersonDAO {

    @Autowired
    private PersonsRepository repository;

    /**
     * Поиск по ФИОДу в таблице person (developer@dame)
     * */
    public TablePerson findByFIOD(String personSurname, String personFirstname, String personLastname, Date personBirthday, String trueSer, String trueNum){
        Person p = repository.findByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(personSurname,personFirstname,personLastname,personBirthday);

        TablePerson tablePerson = new TablePerson(p);
        tablePerson.setPersonNumdoc(trueNum);
        tablePerson.setPersonSerdoc(trueSer);

        return tablePerson;
    }

    /**
     * Поиск по енп людей из таблицы person (developer@dame)
     * @param enps список енп, по которому ищем людей
     * */
    public List<TablePerson> findAllByEnp(List<String> enps){
        List<Person> personList = repository.findByEnpInOrderByPersonSurname(enps);
        List<TablePerson> tablePersonList = new ArrayList<>(personList.size());

        for(Person person : personList){
            tablePersonList.add(new TablePerson(person));
        }

        return tablePersonList;
    }


}
