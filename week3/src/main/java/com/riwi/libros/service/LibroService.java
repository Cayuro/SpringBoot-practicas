package com.riwi.libros.service;

import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository repository;

    public LibroService(LibroRepository repository) {
        this.repository = repository;
    }

    public List<Libro> obtenerTodos() {
        return repository.findAll();
    }

    // De esta forma ya está trayendo paginado y se puede pedir en la consulta
    // el limit y offset basicamente para saber desde donde comenzar
    public Page<Libro> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Libro> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public Libro guardar(Libro libro) {
        return repository.save(libro);
    }

    public boolean eliminarById(Long id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    public Libro actualizar(Long id, Libro libro) {
        if (!repository.existsById(id)) {
            return null;
        }

        libro.setId(id);
        return repository.save(libro);
    }
}
