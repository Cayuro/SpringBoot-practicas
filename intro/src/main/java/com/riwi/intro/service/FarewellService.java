package com.riwi.intro.service;

import org.springframework.stereotype.Service;

@Service
public class FarewellService {
    public String getPersonalizedFarewell(String name){
        return "!Adios, " + name + "! Esperamos verte pronto en el entrenamiento de Spring Boot.";
    }
}
