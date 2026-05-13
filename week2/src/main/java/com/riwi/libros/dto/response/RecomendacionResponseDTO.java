package com.riwi.libros.dto.response;

import java.util.List;

public record RecomendacionResponseDTO(

        String consulta,
        String consultaNormalizada,
        int total,
        LibroRecomendadoDTO recomendado,
        List<LibroRecomendadoDTO> resultados

) {}
