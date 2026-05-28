package com.riwi.Librotech.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="editoriales")
public class Editorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    // Nuevo campo: país de la editorial
    @Column(nullable = false)
    private String pais;

    private Integer fundadoEn;

    // Lado inverso de la relación: una Editorial tiene muchos Libros
    @OneToMany(mappedBy = "editorial")
    @JsonIgnore
    private List<Libro> libros = new ArrayList<>();

    // === Constructores ===
    public Editorial() {}

    public Editorial(String nombre, String direccion, String pais, Integer fundadoEn) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.pais = pais;
        this.fundadoEn = fundadoEn;
    }

}
