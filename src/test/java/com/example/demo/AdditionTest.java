package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Addition class test")
public class AdditionTest {

    public AdditionTest() {
        System.out.println("constuctor called");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }

    @Test
    void testAdd() {
        int res = Addition.add(10, 10);
        Assertions.assertEquals(20, res);
    }

    @Test
    void testAdd2() {
        int res = Addition.add(20 , 20);
        assertEquals(40, res);
    }

    @RepeatedTest(3)
    void testRepeatd() {
        int res = Addition.add(10, 20);
        System.out.println("test ");
        assertEquals(30,res );
    }

    @TestFactory                                                       
    Iterator<DynamicTest> positiveNumberPredicateTestCases() {         
        return Arrays.asList(
                dynamicTest("negative number",                         
                             () -> assertFalse(Predicate.check(-1))),  
                dynamicTest("zero",                                    
                             () -> assertFalse(Predicate.check(0))),   
                dynamicTest("positive number", 
                             () -> assertTrue(Predicate.check(1)))     
        ).iterator();
    }
}
