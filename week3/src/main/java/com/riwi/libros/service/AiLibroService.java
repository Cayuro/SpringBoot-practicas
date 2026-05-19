package com.riwi.libros.service;

import com.riwi.libros.assistant.LibroAssistant;
import com.riwi.libros.dto.response.LibroRecomendadoDTO;
import com.riwi.libros.dto.response.RecomendacionResponseDTO;
import com.riwi.libros.models.Libro;
import com.riwi.libros.repositories.LibroRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiLibroService {

    private static final int MAX_RESULTADOS_LOCALES = 5;
    private static final int MAX_RESULTADOS_IA = 3;
    private static final int MAX_RESULTADOS_APROXIMADOS = 3;
    private static final Pattern NUMEROS = Pattern.compile("\\d+");

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
            return respuestaVacia(consulta, consultaNormalizada);
        }

        List<Libro> catalogo = repository.findAll();

        if (catalogo.isEmpty()) {
            return respuestaVacia(consulta, consultaNormalizada);
        }

        List<LibroRecomendadoDTO> resultados = buscarLocal(catalogo, consultaNormalizada);

        if (resultados.isEmpty()) {
            resultados = buscarConIa(catalogo, consulta);
        }

        if (resultados.isEmpty()) {
            resultados = buscarPorSimilitud(catalogo, consultaNormalizada);
        }

        LibroRecomendadoDTO recomendado = resultados.isEmpty() ? null : resultados.get(0);

        return new RecomendacionResponseDTO(
                consulta,
                consultaNormalizada,
                resultados.size(),
                recomendado,
                resultados
        );
    }

    private List<LibroRecomendadoDTO> buscarLocal(List<Libro> catalogo, String consultaNormalizada) {
        List<LibroRecomendadoDTO> resultados = new ArrayList<>();

        for (Libro libro : catalogo) {
            String titulo = normalizar(libro.getTitulo());
            String autor = normalizar(libro.getAutor());
            String isbn = normalizar(libro.getIsbn());

            boolean coincide = titulo.contains(consultaNormalizada)
                    || autor.contains(consultaNormalizada)
                    || isbn.contains(consultaNormalizada);

            if (coincide) {
                resultados.add(new LibroRecomendadoDTO(
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getAnioPublicacion(),
                        0.90,
                        "Coincidencia directa con titulo, autor o ISBN."
                ));
            }

            if (resultados.size() == MAX_RESULTADOS_LOCALES) {
                break;
            }
        }

        return resultados;
    }

    private List<LibroRecomendadoDTO> buscarPorSimilitud(List<Libro> catalogo, String consultaNormalizada) {
        return catalogo.stream()
                .map(libro -> new Candidato(libro, calcularScore(libro, consultaNormalizada)))
                .sorted(Comparator.comparingDouble(Candidato::score).reversed())
                .limit(MAX_RESULTADOS_APROXIMADOS)
                .map(candidato -> {
                    Libro libro = candidato.libro();
                    return new LibroRecomendadoDTO(
                            libro.getId(),
                            libro.getTitulo(),
                            libro.getAutor(),
                            libro.getIsbn(),
                            libro.getAnioPublicacion(),
                            redondear(candidato.score()),
                            "Coincidencia aproximada con la consulta."
                    );
                })
                .toList();
    }

    private double calcularScore(Libro libro, String consultaNormalizada) {
        String titulo = normalizar(libro.getTitulo());
        String autor = normalizar(libro.getAutor());
        String isbn = normalizar(libro.getIsbn());
        String textoLibro = normalizar(titulo + " " + autor + " " + isbn);

        double score = 0.0;

        if (titulo.contains(consultaNormalizada) || autor.contains(consultaNormalizada) || isbn.contains(consultaNormalizada)) {
            score = Math.max(score, 0.90);
        }

        score = Math.max(score, similitud(consultaNormalizada, titulo) * 0.85);
        score = Math.max(score, similitud(consultaNormalizada, autor) * 0.75);
        score = Math.max(score, similitud(consultaNormalizada, isbn) * 0.70);
        score = Math.max(score, mejorSimilitudEntrePalabras(consultaNormalizada, textoLibro));

        return score;
    }

    private double mejorSimilitudEntrePalabras(String consulta, String textoLibro) {
        String[] palabrasConsulta = consulta.split(" ");
        String[] palabrasLibro = textoLibro.split(" ");
        double mejor = 0.0;

        for (String palabraConsulta : palabrasConsulta) {
            if (palabraConsulta.length() < 3) {
                continue;
            }

            for (String palabraLibro : palabrasLibro) {
                if (palabraLibro.length() < 3) {
                    continue;
                }

                double similitud = similitud(palabraConsulta, palabraLibro);
                if (palabraLibro.contains(palabraConsulta) || palabraConsulta.contains(palabraLibro)) {
                    similitud = Math.max(similitud, 0.82);
                }

                mejor = Math.max(mejor, similitud);
            }
        }

        return mejor;
    }

    private double similitud(String izquierda, String derecha) {
        if (izquierda.isBlank() || derecha.isBlank()) {
            return 0.0;
        }

        int distancia = distanciaLevenshtein(izquierda, derecha);
        int longitudMayor = Math.max(izquierda.length(), derecha.length());

        return 1.0 - ((double) distancia / longitudMayor);
    }

    private int distanciaLevenshtein(String izquierda, String derecha) {
        int[] anterior = new int[derecha.length() + 1];
        int[] actual = new int[derecha.length() + 1];

        for (int j = 0; j <= derecha.length(); j++) {
            anterior[j] = j;
        }

        for (int i = 1; i <= izquierda.length(); i++) {
            actual[0] = i;

            for (int j = 1; j <= derecha.length(); j++) {
                int costo = izquierda.charAt(i - 1) == derecha.charAt(j - 1) ? 0 : 1;
                actual[j] = Math.min(
                        Math.min(actual[j - 1] + 1, anterior[j] + 1),
                        anterior[j - 1] + costo
                );
            }

            int[] temporal = anterior;
            anterior = actual;
            actual = temporal;
        }

        return anterior[derecha.length()];
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private List<LibroRecomendadoDTO> buscarConIa(List<Libro> catalogo, String consulta) {
        StringBuilder textoCatalogo = new StringBuilder();

        for (Libro libro : catalogo) {
            textoCatalogo.append("ID: ")
                    .append(libro.getId())
                    .append(" | Titulo: ")
                    .append(libro.getTitulo())
                    .append(" | Autor: ")
                    .append(libro.getAutor())
                    .append(" | ISBN: ")
                    .append(libro.getIsbn())
                    .append("\n");
        }

        String prompt = """
                Consulta del usuario:
                %s

                Catalogo disponible:
                %s

                Responde SOLO con IDs (separados por comas) de libros que puedan servir para la consulta.
                Maximo %s IDs.
                Si no hay coincidencias, responde SOLO con: NINGUNO
                """.formatted(consulta, textoCatalogo, MAX_RESULTADOS_IA);

        String respuestaIa;
        try {
            respuestaIa = assistant.recomendar(prompt);
        } catch (Exception ex) {
            return List.of();
        }

        if (respuestaIa == null || normalizar(respuestaIa).contains("ninguno")) {
            return List.of();
        }

        Set<Long> ids = extraerIds(respuestaIa);
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<Long, Libro> librosPorId = new HashMap<>();
        for (Libro libro : catalogo) {
            librosPorId.put(libro.getId(), libro);
        }

        List<LibroRecomendadoDTO> resultados = new ArrayList<>();
        for (Long id : ids) {
            Libro libro = librosPorId.get(id);
            if (libro == null) {
                continue;
            }

            resultados.add(new LibroRecomendadoDTO(
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getAnioPublicacion(),
                    0.60,
                    "Sugerido por IA como opcion aproximada."
            ));

            if (resultados.size() == MAX_RESULTADOS_IA) {
                break;
            }
        }

        return resultados;
    }

    private Set<Long> extraerIds(String texto) {
        Matcher matcher = NUMEROS.matcher(texto);
        Set<Long> ids = new LinkedHashSet<>();

        while (matcher.find()) {
            ids.add(Long.parseLong(matcher.group()));
        }

        return ids;
    }

    public String explicarRecomendacionConIa(String consulta) {
        RecomendacionResponseDTO recomendacion = recomendarLibros(consulta);

        if (recomendacion.recomendado() == null) {
            return "No se encontraron libros relacionados con la consulta.";
        }

        LibroRecomendadoDTO libro = recomendacion.recomendado();
        return "Recomendacion: " + libro.titulo() + " de " + libro.autor()
                + ". Motivo: " + libro.razon();
    }

    private RecomendacionResponseDTO respuestaVacia(String consulta, String consultaNormalizada) {
        return new RecomendacionResponseDTO(
                consulta,
                consultaNormalizada,
                0,
                null,
                List.of()
        );
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

    private record Candidato(Libro libro, double score) {
    }

}
