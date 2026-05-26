package com.riwi.Librotech.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.riwi.Librotech.dto.BookRequest;
import com.riwi.Librotech.dto.BookResponse;
import com.riwi.Librotech.dto.LibroResumenDTO;
import com.riwi.Librotech.model.Libro;
import com.riwi.Librotech.repository.LibroRepository;

import java.util.List;

@Service
public class LibroService {
    private final LibroRepository bookRepository;

    public LibroService(LibroRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ===== LABORATORIO SEMANA 4 # 2
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Obtiene un "slice" (fragmento) del catálogo de libros.
     * No ejecuta COUNT → más rápido que Page para catálogos masivos.
     *
     * @param page número de página (0-indexed)
     * @return Slice con los DTOs de resumen y metadatos de navegación
     */
    public Slice<LibroResumenDTO> getCatalogo(int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        return bookRepository.findAllLibroResumenes(pageable);
    }

    /**
     * Obtiene un libro con TODAS sus relaciones cargadas (para edición/detalle).
     */
    public Libro getLibroWithRelations(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Libro no encontrado: " + id));
    }

    // finlab 4 -2


    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::toBookResponse);
    }

    public Page<BookResponse> getBookByAutor(Pageable pageable, String author) {
        return bookRepository.findByAutorIgnoreCase(pageable, author)
                .map(this::toBookResponse);
    }

    public BookResponse addBook(BookRequest request) {
        Libro book = new Libro();
        book.setTitulo(request.getTitulo());
        book.setAutor(request.getAutor());
        book.setIsbn(request.getIsbn());
        book.setFechaPublicacion(request.getFechaPublicacion());

        Libro savedBook = bookRepository.save(book);

        return toBookResponse(savedBook);
    }

    // public List<BookResponse> findBooksByDescription(String description) {
    //     List<Libro> books = bookRepository.findAll();
    //     List<Long> matchingIds = bookTitleAiService.findBookIdsFromDescription(description, books);

    //     return matchingIds.stream()
    //             .map(id -> books.stream()
    //                     .filter(book -> book.getId().equals(id))
    //                     .findFirst()
    //                     .orElse(null))
    //             .filter(book -> book != null)
    //             .map(this::toBookResponse)
    //             .toList();
    // }

    public void deleteBook(Long id) {
        Libro book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
    }

    public void updateBook(Long id, BookRequest bookRequest) {
        bookRepository.findById(id)
                .map(book -> {
                            book.setTitulo(bookRequest.getTitulo());
                            book.setAutor(bookRequest.getAutor());
                            book.setIsbn(bookRequest.getIsbn());
                            book.setFechaPublicacion(bookRequest.getFechaPublicacion());
                            return bookRepository.save(book);
                        }
                )
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void patchBook(Long id, BookRequest bookRequest) {
        bookRepository.findById(id)
                .map(book -> {
                    if (bookRequest.getTitulo() != null) book.setTitulo(bookRequest.getTitulo());
                    if (bookRequest.getAutor() != null) book.setAutor(bookRequest.getAutor());
                    if (bookRequest.getIsbn() != null) book.setIsbn(bookRequest.getIsbn());
                    if (bookRequest.getFechaPublicacion() != null)
                        book.setFechaPublicacion(bookRequest.getFechaPublicacion());
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }


    private BookResponse toBookResponse(Libro book) {
        return new BookResponse(
                book.getId(),
                book.getTitulo(),
                book.getAutor(),
                book.getIsbn(),
                book.getFechaPublicacion()
        );
    }
}