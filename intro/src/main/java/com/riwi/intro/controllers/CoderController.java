package com.riwi.intro.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.intro.models.Coder;
import com.riwi.intro.service.CoderService;

@RestController
@RequestMapping("/api/coders") // prefijo para endpoints de controller
public class CoderController {
    private final CoderService service;

    public CoderController(CoderService coder){
        this.service = coder;
    }
    @GetMapping
    public List<Coder> getCoders(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Coder getCoderById(@PathVariable Long id){
        return service.getCoderById(id);
    }
    
    @PutMapping("/{id}")
    public Coder update(@PathVariable Long id, @RequestBody Coder update){
        return service.updateCoderById(id, update);
    }
    
    @PostMapping // endpoint para agregar un nuevo coder
    public Coder create(@RequestBody Coder coder){
        return service.create(coder); // retorna el coder creado
    }

    @DeleteMapping("/{id}") // endpoint para eliminar un coder por id
    public Coder deleteCoderById(@PathVariable Long id){
        return service.deleteCoderById(id);
    }
/*
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

    @DeleteMapping("/{id}") // endpoint para eliminar un coder por id
    public Coder deleteCoderById(@PathVariable Long id){

*/ 

        /*
        // solo usar cuando se retorna string, para evitar editar mientras
        // se recorre la lista
         boolean elimin = coders.removeIf(c -> c.getId().equals(id));
         return removed ? "Coder eliminado" : "Coder no encontrado";
      */

/*        for(Coder coder : coders){
            if(coder.getId().equals(id)){
                coders.remove(coder);
                return coder;
            }
        }
        return null;
    }
    
    @PutMapping("/{id}") // endpoint para delete
    public Coder update(@PathVariable Long id, @RequestBody Coder update){
        for(Coder coder : coders){
            if(coder.getId().equals(id)){
                coder.setName(update.getName());
                coder.setClan(update.getClan());
                return "Coder id: " + coder.getId() + " actualizado";
            }
        }
        return "Coder id: " + id + " no encontrado";
    }
    //  ==== Adicional puede ser eliminado si genera problemas === 
    @GetMapping // endpoint para obtener todos los coders
    public List<Coder> getAllCoders() {
        return coders;
    }
*/
}
