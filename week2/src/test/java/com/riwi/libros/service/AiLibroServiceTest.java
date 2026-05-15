package com.riwi.libros.service;

import com.riwi.libros.assistant.LibroAssistant;
import com.riwi.libros.dto.response.RecomendacionResponseDTO;
import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiLibroServiceTest {

    @Test
    void recomiendaLibroPorConsultaAproximadaCuandoLaIaNoEncuentraResultado() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroAssistant assistant = mock(LibroAssistant.class);
        AiLibroService service = new AiLibroService(repository, assistant);

        Libro cienAnios = libro(1L, "Cien años de soledad", "Gabriel Garcia Marquez", "9780307474728", 1967);
        Libro cleanCode = libro(2L, "Clean Code", "Robert C. Martin", "9780132350884", 2008);

        when(repository.findAll()).thenReturn(List.of(cleanCode, cienAnios));
        when(assistant.recomendar(anyString())).thenReturn("NINGUNO");

        RecomendacionResponseDTO response = service.recomendarLibros("solitico");

        assertNotNull(response.recomendado());
        assertEquals(cienAnios.getId(), response.recomendado().id());
        assertEquals("Cien años de soledad", response.recomendado().titulo());
    }

    private Libro libro(Long id, String titulo, String autor, String isbn, int anioPublicacion) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setIsbn(isbn);
        libro.setAnioPublicacion(anioPublicacion);
        return libro;
    }
}
