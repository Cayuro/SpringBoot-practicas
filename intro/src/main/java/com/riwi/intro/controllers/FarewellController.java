package com.riwi.intro.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.intro.service.FarewellService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Lugares", description = "Operaciones del módulo de lugares")
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
