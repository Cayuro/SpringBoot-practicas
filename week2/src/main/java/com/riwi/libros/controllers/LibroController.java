package com.riwi.libros.controllers;

import com.riwi.libros.models.Libro;
import com.riwi.libros.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private LibroService libroService;

    public LibroController(LibroService service) { this.libroService = service; }

    @GetMapping
    public List<Libro> listar() {
        return libroService.obtenerTodos();
    }

    @PostMapping
    public Libro crear(@RequestBody Libro libro) {
        return libroService.guardar(libro);
    }
}