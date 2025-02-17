package com.davidparry.cover.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ExampleCodeTest {

    @Test
    public void constructor_works() {
        ExampleCode exampleCode = new ExampleCode();
        assertNotNull(exampleCode);
    }

    @Test
    public void test_remainder_numbers() {
        ExampleCode exampleCode = new ExampleCode();
        int result = exampleCode.remainder(10, 3);
        assertEquals(1, result);
    }


    @Test
    public void test_multiply_numbers() {
        ExampleCode exampleCode = new ExampleCode();
        int result = exampleCode.multiply(3, 4);
        assertEquals(12, result);
    }


    @Test
    public void test_divide_numbers() {
        ExampleCode exampleCode = new ExampleCode();
        int result = exampleCode.divide(10, 2);
        assertEquals(5, result);
    }


    @Test
    public void test_subtract_numbers() {
        ExampleCode exampleCode = new ExampleCode();
        int result = exampleCode.subtract(10, 5);
        assertEquals(5, result);
    }


    @Test
    public void test_add_positive_numbers() {
        ExampleCode exampleCode = new ExampleCode();
        int result = exampleCode.add(3, 5);
        assertEquals(8, result);
    }


}
