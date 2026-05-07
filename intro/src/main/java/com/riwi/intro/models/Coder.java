package com.riwi.intro.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description="Modelo que representa a cada coder de la base de datos")
public class Coder {
    @Schema(description="ID unico del coder", example="123", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Nombre completo del coder", example = "Jainer Pulgarin")
    private String name;
    @Schema(description= "Clan al que pertenece el coder", example = "Hamilton")
    private String clan;
}
