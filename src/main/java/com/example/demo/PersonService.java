package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepo personRepo;


    public Person save(Person person) {
        return personRepo.save(person);
    }

    public List<Person> list() {
        return personRepo.findAll();
    }
}
