package com.riwi.libros.service;

import com.riwi.libros.assistant.LibroAssistant;
import com.riwi.libros.dto.response.LibroRecomendadoDTO;
import com.riwi.libros.dto.response.RecomendacionResponseDTO;
import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;

@Service
public class AiLibroService {

    private static final double MIN_SCORE = 0.35;
    private static final int MAX_RESULTADOS = 5;

    private final LibroRepository repository;
    private final LibroAssistant assistant;

    public AiLibroService(
            LibroRepository repository,
            LibroAssistant assistant
    ) {
        this.repository = repository;
        this.assistant = assistant;
    }

    public RecomendacionResponseDTO recomendarLibros(String consulta) {

        String consultaNormalizada = normalizar(consulta);

        if (consultaNormalizada.isBlank()) {
            return new RecomendacionResponseDTO(
                    consulta,
                    consultaNormalizada,
                    0,
                    null,
                    List.of()
            );
        }

        List<Libro> libros = repository.findAll();

        if (libros.isEmpty()) {
            return new RecomendacionResponseDTO(
                    consulta,
                    consultaNormalizada,
                    0,
                    null,
                    List.of()
            );
        }

        List<LibroRecomendadoDTO> resultados = libros.stream()
                .map(libro -> recomendar(libro, consultaNormalizada))
                .filter(resultado -> resultado.score() >= MIN_SCORE)
                .sorted(Comparator.comparing(LibroRecomendadoDTO::score).reversed())
                .limit(MAX_RESULTADOS)
                .toList();

        LibroRecomendadoDTO recomendado = resultados.isEmpty() ? null : resultados.get(0);

        return new RecomendacionResponseDTO(
                consulta,
                consultaNormalizada,
                resultados.size(),
                recomendado,
                resultados
        );
    }

    public String explicarRecomendacionConIa(String consulta) {
        RecomendacionResponseDTO recomendacion = recomendarLibros(consulta);

        if (recomendacion.recomendado() == null) {
            return "No se encontraron libros relacionados con la consulta.";
        }

        String catalogo = recomendacion.resultados().stream()
                .map(libro -> """
                        ID: %s
                        Titulo: %s
                        Autor: %s
                        ISBN: %s
                        Anio de publicacion: %s
                        Score: %s
                        """.formatted(
                        libro.id(),
                        libro.titulo(),
                        libro.autor(),
                        libro.isbn(),
                        libro.anioPublicacion(),
                        libro.score()
                ))
                .reduce("", String::concat);

        String prompt = """
                Consulta del usuario:
                %s

                Candidatos encontrados por la aplicacion:
                %s

                Explica brevemente por que el primer candidato es la mejor recomendacion.
                No inventes libros fuera de esta lista.
                """.formatted(consulta, catalogo);

        return assistant.recomendar(prompt);
    }

    private LibroRecomendadoDTO recomendar(Libro libro, String consultaNormalizada) {
        String titulo = normalizar(libro.getTitulo());
        String autor = normalizar(libro.getAutor());
        String isbn = normalizar(libro.getIsbn());

        double scoreTitulo = calcularScore(consultaNormalizada, titulo);
        double scoreAutor = calcularScore(consultaNormalizada, autor);
        double scoreIsbn = calcularScore(consultaNormalizada, isbn);
        double score = redondear((scoreTitulo * 0.70) + (scoreAutor * 0.20) + (scoreIsbn * 0.10));

        return new LibroRecomendadoDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getAnioPublicacion(),
                score,
                construirRazon(scoreTitulo, scoreAutor, scoreIsbn)
        );
    }

    private double calcularScore(String consulta, String valor) {
        if (valor.isBlank()) {
            return 0;
        }

        if (valor.equals(consulta)) {
            return 1;
        }

        if (valor.contains(consulta) || consulta.contains(valor)) {
            return 0.90;
        }

        return similitudLevenshtein(consulta, valor);
    }

    private String construirRazon(double scoreTitulo, double scoreAutor, double scoreIsbn) {
        if (scoreTitulo >= scoreAutor && scoreTitulo >= scoreIsbn) {
            return "Coincide principalmente con el titulo.";
        }

        if (scoreAutor >= scoreTitulo && scoreAutor >= scoreIsbn) {
            return "Coincide principalmente con el autor.";
        }

        return "Coincide principalmente con el ISBN.";
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinTildes
                .toLowerCase()
                .replace('ñ', 'n')
                .replaceAll("[^a-z0-9 ]", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private double similitudLevenshtein(String izquierda, String derecha) {
        int distancia = distanciaLevenshtein(izquierda, derecha);
        int longitudMaxima = Math.max(izquierda.length(), derecha.length());

        if (longitudMaxima == 0) {
            return 1;
        }

        return 1.0 - ((double) distancia / longitudMaxima);
    }

    private int distanciaLevenshtein(String izquierda, String derecha) {
        int[][] matriz = new int[izquierda.length() + 1][derecha.length() + 1];

        for (int i = 0; i <= izquierda.length(); i++) {
            matriz[i][0] = i;
        }

        for (int j = 0; j <= derecha.length(); j++) {
            matriz[0][j] = j;
        }

        for (int i = 1; i <= izquierda.length(); i++) {
            for (int j = 1; j <= derecha.length(); j++) {
                int costo = izquierda.charAt(i - 1) == derecha.charAt(j - 1) ? 0 : 1;

                matriz[i][j] = Math.min(
                        Math.min(matriz[i - 1][j] + 1, matriz[i][j - 1] + 1),
                        matriz[i - 1][j - 1] + costo
                );
            }
        }

        return matriz[izquierda.length()][derecha.length()];
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
