package com.riwi.Librotech.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;

    private String titulo;

    private String autor;

    private String isbn;

    private LocalDate fechaPublicacion;
}
