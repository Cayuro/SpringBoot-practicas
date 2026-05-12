package com.riwi.libros.service;

import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private LibroRepository libroRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository) {this.libroRepository = libroRepository;}
    public List<Libro> obtenerTodos() {
        return libroRepository.findAll();
    }

    public Libro guardar(Libro libro) {
        // Aquí podríamos añadir lógica de negocio (ej. validar el ISBN)
        return libroRepository.save(libro);
    }
}