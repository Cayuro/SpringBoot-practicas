package com.riwi.Librotech.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "libros")
@NoArgsConstructor
@AllArgsConstructor
@Entity

@SQLRestriction("disponible=true")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private LocalDate fechaPublicacion;

    private Double precio;

    // SOFT DELETE
    @Column(nullable = false)
    private Boolean disponible = true;

    // RELACION MANY-TO-ONE cada libro pertenece a una editorial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editorial_id", nullable = false)
    private Editorial editorial;

    // RELACION MANY-TO-MANY: un libro tiene muchos generos y un genero va a muchos libros

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "libros_generos", // nombre de la tabla
        joinColumns = @JoinColumn(name = "libro_id"), // FK hacia libro
        inverseJoinColumns = @JoinColumn(name = "genero_id") // FK hacia genero
    )

    private Set<Genero> generos = new HashSet<>();

    public void softDelete(){
        this.disponible = false;
    }
}