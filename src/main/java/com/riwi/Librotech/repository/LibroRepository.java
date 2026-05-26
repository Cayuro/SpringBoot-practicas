package com.riwi.Librotech.repository;

import com.riwi.Librotech.dto.LibroResumenDTO;
import com.riwi.Librotech.model.Libro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Page<Libro> findByAutorIgnoreCase(Pageable pageable, String autor);

    Optional<Libro> findFirstByTituloIgnoreCase(String titulo);

    // laboraTORIO semana 4 #2
    @Query("""
            SELECT new com.riwi.Librotech.dto.LibroResumenDTO(
            l.id,
            l.titulo,
            l.fechaPublicacion,
            l.precio,
            l.editorial.nombre,
            l.editorial.pais
            ) FROM Libro l 
            JOIN l.editorial
            ORDER BY l.fechaPublicacion DESC
             """)
    Slice<LibroResumenDTO> findAllLibroResumenes(Pageable pageable);


    // LIBRO REPOSITORY ENTITY GRAPH

    /**
     * Carga un libro con sus relaciones (Editorial + Géneros) en UNA sola query.
     * Sin @EntityGraph, acceder a libro.getEditorial() o libro.getGeneros()
     * dispararía consultas adicionales (N+1).
     */

    @EntityGraph(attributePaths = {"editorial", "generos"})
    Optional<Libro> findById(Long id);

      /**
     * Lista completa con carga ansiosa de relaciones.
     * Útil para el panel de administración donde se necesitan todos los datos.
     */

    @EntityGraph(attributePaths = {"editorial","generos"})
    @Query("SELECT l FROM Libro l ORDER BY l.fechaPublicacion DESC")
    List<Libro> findAllWithRelations();
}