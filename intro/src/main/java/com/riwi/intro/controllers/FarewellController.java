package com.riwi.intro.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.intro.service.FarewellService;

@RestController
public class FarewellController {
    private final FarewellService service;

    public FarewellController(FarewellService service) {
        this.service = service;
    }
    
    @GetMapping("/farewell")
    public String farewell(@RequestParam(defaultValue = "Coder") String name) {
        return service.getPersonalizedFarewell(name); 
    }
}
