package org.tfoms.snils.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.tfoms.snils.model.Person;

import java.util.Date;
import java.util.List;

public interface PersonsRepository extends JpaRepository<Person, String> {

    Person findByPersonSurnameAndPersonFirstnameAndPersonLastnameAndPersonBirthday(String personSurname, String personFirstname, String personLastname, Date personBirthday);

    List<Person> findByEnpInOrderByPersonSurname(List<String> enp);
}
