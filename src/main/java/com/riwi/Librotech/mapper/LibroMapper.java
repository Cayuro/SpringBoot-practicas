package com.riwi.Librotech.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.riwi.Librotech.model.Genero;
import com.riwi.Librotech.model.Libro;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

import com.riwi.Librotech.dto.LibroCreateDTO;
import com.riwi.Librotech.dto.LibroResponseDTO;

@Mapper(componentModel = "spring")
public interface LibroMapper {
    // entidad a responseDTO
    @Mapping(source = "editorial.nombre", target = "nombreEditorial")
    @Mapping(source = "generos", target = "generos", qualifiedByName = "generoToString")
    LibroResponseDTO toLibroResponseDTO(Libro libro);

    // metodo auxiliar para extraer solo los nombres de generos
    // se marca con @Named para usarlo en el @Mapping
    @Named("mapGeneroToNombres")
    default Set<String> mapGenerosToNombres(Set<Genero> generos) {
        if (generos == null) return null; // manejo de null
        return generos.stream() // stream de generos
                .map(Genero::getNombre) // extrae el nombre de cada genero
                .collect(Collectors.toSet()); // recolecta en un Set<String>
    }

    // createDTO a entidad (para crear un nuevo libro)
    // ignoramos el ID, y relaciones complejas las manejamos en el service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponible", constant = "true") // nuevo libro siempre disponible
    @Mapping(target = "editorial", ignore = true) // se asigna en el
    @Mapping(target = "generos", ignore = true)
    Libro toEntity(LibroCreateDTO dto);

    
}
