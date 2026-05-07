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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/coders") // prefijo para endpoints de controller
@Tag(name = "Coders", description = "Operaciones relacionadas con la gestión de Coders")
public class CoderController {
    private final CoderService service;

    public CoderController(CoderService coder){
        this.service = coder;
    }

    @Operation(summary = "Obtener todos los coders", description = "Retorna una lista paginada de todos los coders registrados.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode="404", description="Pagina no encontrada")
    @GetMapping
    public List<Coder> getCoders(){
        return service.getAll();
    }

    @Operation(summary = "Obtener coder por ID", description = "Retorna el coder registrado con el ID especificado.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode="404", description="Pagina no encontrada")
    @GetMapping("/{id}")
    public Coder getCoderById(@Parameter(description = "ID único del usuario a consultar", example = "1") @PathVariable Long id){
        return service.getCoderById(id);
    }
    
    @Operation(summary="Editar un coder", description="edita los campos de la entidad coder con el id especificado")
    @ApiResponse(responseCode="200", description="Coder editado correctamente")
    @PutMapping("/{id}")
    public Coder update(@PathVariable Long id, @RequestBody Coder update){
        return service.updateCoderById(id, update);
    }
    
    @Operation(summary = "Crear un nuevo Coder", description = "Registra un coder en el sistema y retorna el objeto creado.")
    @ApiResponse(responseCode = "201", description = "Coder creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping // endpoint para agregar un nuevo coder
    public Coder create(@RequestBody Coder coder){
        return service.create(coder); // retorna el coder creado
    }

    @Operation(summary="Eliminar coder", description="Elimina el coder con el ID especificado")
    @ApiResponse(responseCode="200", description="Coder eliminado correctamente")
    @DeleteMapping("/{id}") // endpoint para eliminar un coder por id
    public Coder deleteCoderById(@PathVariable Long id){
        return service.deleteCoderById(id);
    }
/*
    // CONTROLLER DE ACUERDO CON LO PEDIDO EN EL DÍA 2.
    
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
