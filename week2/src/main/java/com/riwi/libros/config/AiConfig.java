package com.riwi.libros.config;

import com.riwi.libros.assistant.LibroAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public LibroAssistant libroAssistant(ChatModel model) {

        return AiServices.create(
                LibroAssistant.class,
                model
        );
    }
}
