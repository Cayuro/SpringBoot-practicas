package com.riwi.libros.service;

import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibroServiceTest {

    @Test
    void guardaLibroUsandoRepositorio() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = new LibroService(repository);
        Libro libro = new Libro(null, "Clean Code", "Robert C. Martin", "9780132350884", 2008);
        Libro guardado = new Libro(1L, "Clean Code", "Robert C. Martin", "9780132350884", 2008);

        when(repository.save(libro)).thenReturn(guardado);

        Libro resultado = service.guardar(libro);

        assertEquals(1L, resultado.getId());
        assertEquals("Clean Code", resultado.getTitulo());
    }

    @Test
    void eliminaLibroCuandoExiste() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = new LibroService(repository);

        when(repository.existsById(1L)).thenReturn(true);

        assertTrue(service.eliminarById(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void noEliminaLibroCuandoNoExiste() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = new LibroService(repository);

        when(repository.existsById(99L)).thenReturn(false);

        assertFalse(service.eliminarById(99L));
    }

    @Test
    void obtieneLibroPorId() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = new LibroService(repository);
        Libro libro = new Libro(1L, "El Principito", "Antoine de Saint-Exupery", "9780156012195", 1943);

        when(repository.findById(1L)).thenReturn(Optional.of(libro));

        assertEquals("El Principito", service.obtenerPorId(1L).orElseThrow().getTitulo());
    }
}
