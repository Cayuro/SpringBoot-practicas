package com.riwi.libros.config;

import com.riwi.libros.assistant.LibroAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnBean(ChatModel.class)
    public LibroAssistant libroAssistant(ChatModel model) {

        return AiServices.create(
                LibroAssistant.class,
                model
        );
    }

    @Bean
    @ConditionalOnMissingBean(LibroAssistant.class)
    public LibroAssistant libroAssistantFallback() {
        return mensaje -> {
            if (mensaje != null && mensaje.toLowerCase().contains("explica")) {
                return "La IA no esta configurada. Se uso la recomendacion local por coincidencia de titulo, autor o ISBN.";
            }

            return "NINGUNO";
        };
    }
}
