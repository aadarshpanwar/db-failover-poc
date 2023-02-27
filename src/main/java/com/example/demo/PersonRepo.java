package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long>{
    @Query(value = "select * from person where first_name = ?1", nativeQuery = true)
    @Transactional(readOnly = true)
    List<Person> findByFirstName(String firstName);

    @Query(value = "select * from person where age = ?1", nativeQuery = true)
    @Transactional(readOnly = true)
    List<Person> findByAge(int age);
}
