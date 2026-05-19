package com.riwi.libros.controllers;

import com.riwi.libros.dto.response.RecomendacionResponseDTO;
import com.riwi.libros.models.Libro;
import com.riwi.libros.service.AiLibroService;
import com.riwi.libros.service.LibroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService service;
    private final AiLibroService aiLibroService;

    public LibroController(LibroService service, AiLibroService aiLibroService) {
        this.service = service;
        this.aiLibroService = aiLibroService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long id) {

        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Libro>> listar(
            @PageableDefault(size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(pageable));
    }

    @GetMapping({"/recomendaciones", "/ia/recomendaciones"})
    public ResponseEntity<RecomendacionResponseDTO> recomendar(@RequestParam String consulta) {
        return ResponseEntity.ok(aiLibroService.recomendarLibros(consulta));
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
