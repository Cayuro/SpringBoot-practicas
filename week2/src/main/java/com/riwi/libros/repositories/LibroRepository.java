package com.riwi.libros.repositories;

import com.riwi.libros.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Consulta derivada: buscar libros por autor
    List<Libro> findByAutor(String autor);
}