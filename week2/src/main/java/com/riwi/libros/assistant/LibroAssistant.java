package com.riwi.libros.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface LibroAssistant {

    @SystemMessage("""
        Eres un experto recomendando libros.
        Recomienda únicamente libros que existan en el catálogo recibido.
        Responde en español, de forma breve y clara.
        """)
    String recomendar(@UserMessage String mensaje);
}
