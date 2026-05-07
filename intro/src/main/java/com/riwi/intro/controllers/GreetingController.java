package com.riwi.intro.controllers;

import com.riwi.intro.service.GreetingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Lugares", description = "Operaciones del módulo de lugares")
public class GreetingController {

    private final GreetingService service;

    public GreetingController(GreetingService service) {
        this.service = service;
    }

    
    @GetMapping("/greet")
    public String greet(@RequestParam(defaultValue = "Coder") String name) {
        return service.getPersonalizedGreeting(name);
    }
}
