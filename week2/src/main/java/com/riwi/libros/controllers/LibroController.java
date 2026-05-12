package com.riwi.libros.controllers;

import com.riwi.libros.models.Libro;
import com.riwi.libros.service.LibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService service;

    public LibroController(LibroService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long id) {

        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Libro> listar() {
        return service.obtenerTodos();
    }

    @PostMapping
    public ResponseEntity<Libro> crear(@RequestBody Libro libro) {

        Libro nuevo = service.guardar(libro);

        return ResponseEntity.status(201).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibroById(@PathVariable Long id) {

        boolean eliminado = service.eliminarById(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}