package org.tfoms.snils.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.tfoms.snils.model.SnilsSaveResponse;
import org.tfoms.snils.model.TablePerson;
import org.tfoms.snils.repo.SnilsPersonRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SnilsSaveDAO {
    @Autowired
    private SnilsPersonRepository snilsRepository;

    /**
     * Вставляем человека со снилсом в таблицу
     * SNILS_SAVE_RESPONSE_NEW (developer@dame)
     * */
    public boolean insertPerson(TablePerson person){
        System.out.println("insert this:" + this);
        // снилс - 11 цифр, если вместо снилса ошибка или что-нибудь еще, выходим
        if(!person.getSnils().trim().matches("\\d{11}")) return false;

        if(snilsRepository.countByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(person.getPersonSurname(),
                person.getPersonFirstname(),person.getPersonLastname(),person.getPersonBirthday()) > 0) {

            // удаляем старые записи
            snilsRepository.deleteAllByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(person.getPersonSurname(),
                    person.getPersonFirstname(), person.getPersonLastname(), person.getPersonBirthday());
        }

        SnilsSaveResponse snilsPerson = new SnilsSaveResponse(person);
        snilsPerson.setDateInsert(new Date());

        snilsRepository.save(snilsPerson);

        return true;
    }

    /**
     * Люди со снилсами из таблицы SNILS_SAVE_RESPONSE_NEW
     * developer@dame
     * */
    public List<TablePerson> findAllGood(){
        List<TablePerson> tablePersonList = new ArrayList<>();

        for(SnilsSaveResponse s : snilsRepository.findAll()){
            tablePersonList.add(new TablePerson(s));
        }

        return tablePersonList;
    }

}
