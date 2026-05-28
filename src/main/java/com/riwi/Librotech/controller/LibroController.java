package com.riwi.Librotech.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.riwi.Librotech.dto.BookRequest;
import com.riwi.Librotech.dto.BookResponse;
import com.riwi.Librotech.dto.LibroResumenDTO;
import com.riwi.Librotech.model.Libro;
import com.riwi.Librotech.service.LibroService;
import com.riwi.Librotech.validation.OnCreate;
import com.riwi.Librotech.validation.OnPatch;
import com.riwi.Librotech.validation.OnUpdate;

@RestController
@RequestMapping("/api/books")
@Validated
public class LibroController {
    private final LibroService bookServices;

    public LibroController(LibroService bookServices) {
        this.bookServices = bookServices;
    }

    // LABORATORIO SEMANA 4 #2
    // ya no es necesario el getmapping con paginación pues slice 
    // hace lo mismo pero más barato

    // @GetMapping
    // public ResponseEntity<Page<BookResponse>> getBooks(
    //         @RequestParam(required = false) String autor,
    //         @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    // ) {
    //     Page<BookResponse> bookResponses;

    //     if (autor != null && !autor.isBlank()) {
    //         bookResponses = bookServices.getBookByAutor(pageable, autor);
    //     } else {
    //         bookResponses = bookServices.getAllBooks(pageable);
    //     }

    //     return ResponseEntity.ok(bookResponses);
    // }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getCatalogo(@RequestParam(defaultValue = "0") int page){
        Slice<LibroResumenDTO> slice = bookServices.getCatalogo(page);
        
        // construccion de la respuesta con metadatos de navegacion
        Map<String, Object> response = new HashMap<>();
        response.put("libros",slice.getContent());
        response.put("currentPage", slice.getNumber());
        response.put("pageSize", slice.getSize());
        response.put("hasNext", slice.hasNext());
        response.put("hasPrevious", slice.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    // hasta aquí va  lab 4 -2

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Validated(OnCreate.class) BookRequest request) {
        BookResponse bookCreated = bookServices.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCreated);
    }

    // @PostMapping("/search-by-description")
    // public ResponseEntity<List<BookResponse>> getBookFromDescription(@RequestBody @Valid BookDescriptionRequest request) {
    //     List<BookResponse> books = bookServices.findBooksByDescription(request.getDescription());
    //     return ResponseEntity.ok(books);
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookServices.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody @Validated(OnUpdate.class) BookRequest bookRequest) {
        bookServices.updateBook(id, bookRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchBook(@PathVariable Long id, @RequestBody @Validated(OnPatch.class) BookRequest bookRequest) {
        bookServices.patchBook(id, bookRequest);
        return ResponseEntity.noContent().build();
    }

    // http://localhost:8080/api/books/1
    @GetMapping("/{id}")
    public Libro getById(@PathVariable Long id){
        return bookServices.getLibroWithRelations(id);
    }
}