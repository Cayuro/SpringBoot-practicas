package com.riwi.intro.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.intro.models.Coder;

@RestController
@RequestMapping("/api/coders") // prefijo para endpoints de controller
public class CoderController {
    private List<Coder> coders = new ArrayList<>();
   
    @GetMapping("/{id}") // endpoint para obtener un coder por id
    public Coder getCoderById(@PathVariable Long id) {
        return coders.stream()
        .filter(coder -> coder.getId().equals(id))
        .findFirst()
        .orElse(null); // retorna null si no se encuentra el coder
}
    @GetMapping("/search") // endpoint para buscar coders por clan
    public List<Coder> SearchCodersByClan(@RequestParam String clan){
        return coders.stream()
        .filter(coder -> coder.getClan().equalsIgnoreCase(clan))
        .toList();
    }
    @PostMapping // endpoint para agregar un nuevo coder
    public Coder create(@RequestBody Coder coder){
        coders.add(coder);
        return coder; // retorna el coder creado
    }


    //  ==== Adicional puede ser eliminado si genera problemas === 
    @GetMapping // endpoint para obtener todos los coders
    public List<Coder> getAllCoders() {
        return coders;
    }
}
