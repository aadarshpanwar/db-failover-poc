package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1" + "/person")
public class PersonController {

    @Autowired
    private PersonRepo personRepo;

    @GetMapping
    public List<Person> getAll() {
        return personRepo.findAll();
    }

    @GetMapping("/firstName/{firstName}")
    List<Person> firstName(@PathVariable("firstName")String firstName) {
        return personRepo.findByFirstName(firstName);
    }

    @GetMapping("/age/{age}")
    List<Person> firstName(@PathVariable("age")int age) {
        return personRepo.findByAge(age);
    }
}