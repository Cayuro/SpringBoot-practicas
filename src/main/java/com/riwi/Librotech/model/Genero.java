package com.riwi.Librotech.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "generos")
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true)
    private String nombre;

    private String descripcion;

    @ManyToMany(mappedBy = "generos")
    @JsonIgnore
    private Set<Libro> libros = new HashSet<>();

    // contructor con argumentos
    public Genero(String nombre, String descripcion){
        this.nombre = nombre;
        this.descripcion= descripcion;
    }
}
