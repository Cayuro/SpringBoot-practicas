package com.riwi.intro.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description="Modelo que representa las tareas de los coders")
public class Task {
    @Schema(description = "ID unico de la tarea", example= "123", accessMode=Schema.AccessMode.READ_ONLY)
    private int id;
    @Schema(description= "Descripción de la tarea, aquí se explica lo que hace", example= "Lavar los platos")
    private String description;
}
