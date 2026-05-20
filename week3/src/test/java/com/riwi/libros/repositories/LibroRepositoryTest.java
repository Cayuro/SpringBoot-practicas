package com.riwi.libros.repositories;

import com.riwi.libros.models.Libro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class LibroRepositoryTest {

    @Autowired
    private LibroRepository libroRepository;

    @Test
    @DisplayName("Deberia guardar un libro y asignarle un ID autoincremental")
    void guardarLibroTest() {
        Libro libro = new Libro(null, "Prueba de Test", "Autor Test", "123-TEST", 2024);

        Libro guardado = libroRepository.save(libro);

        assertNotNull(guardado.getId());
        assertEquals("Prueba de Test", guardado.getTitulo());
    }

    @Test
    @DisplayName("Deberia encontrar libros por el autor usando la consulta derivada")
    void buscarPorAutorTest() {
        libroRepository.save(new Libro(null, "Libro 1", "Gabriel Garcia", "ISBN-1", 1980));
        libroRepository.save(new Libro(null, "Libro 2", "Miguel Cervantes", "ISBN-2", 1605));

        List<Libro> resultados = libroRepository.findByAutor("Gabriel Garcia");

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("Libro 1", resultados.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deberia eliminar un libro correctamente")
    void eliminarLibroTest() {
        Libro libro = libroRepository.save(new Libro(null, "A eliminar", "Autor", "ISBN-X", 2000));
        Long id = libro.getId();

        libroRepository.deleteById(id);
        Optional<Libro> resultado = libroRepository.findById(id);

        assertTrue(resultado.isEmpty());
    }
}
