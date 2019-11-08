package org.tfoms.snils.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.tfoms.snils.model.SnilsSaveResponse;

import java.util.Date;

@Qualifier("SnilsSaveResponseNewRepository")
public interface SnilsPersonRepository extends JpaRepository<SnilsSaveResponse,String> {

    int countByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(String personSurname,String personFirstname,String personLastname, Date personBirthday);

    void deleteAllByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(String personSurname,String personFirstname,String personLastname, Date personBirthday);
}
