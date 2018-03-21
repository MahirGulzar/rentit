package com.example.demo.common.application.exceptions;

public class PlantNotFoundException extends Exception{
    public PlantNotFoundException(Long id) {
        super(String.format("Plant not found! (Plant id: %d)", id));
    }
}
