package com.riwi.intro.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gestión de Coders",
        version = "1.0",
        description = "Documentación detallada de los endpoints para la gestión de coders y tareas.",
        contact = @Contact(name = "Juan Esteban Gomez", email = "juan_gomez2000@hotmail.com")
    )
)
public class SwaggerConfig {
}
