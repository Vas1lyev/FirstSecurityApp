package ru.vasilyev.springcourse.FirstSecurityApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vasilyev.springcourse.FirstSecurityApp.models.Person;
import ru.vasilyev.springcourse.FirstSecurityApp.repositories.PeopleRepository;

@Service
public class RegistrationService {

    private final PeopleRepository personRepository;

    @Autowired
    public RegistrationService(PeopleRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public void register(Person person) {
        personRepository.save(person);
    }
}
