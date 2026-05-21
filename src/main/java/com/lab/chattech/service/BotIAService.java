package com.lab.chattech.service;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lab.chattech.model.Mensaje;

@Service
public class BotIAService {

    private final MensajeService mensajeService;
    private final ChatClient chatClient;

    public BotIAService(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider, MensajeService mensajeService) {
        this.mensajeService = mensajeService;

        ChatClient.Builder chatClientBuilder = chatClientBuilderProvider.getIfAvailable();
        this.chatClient = chatClientBuilder != null ? chatClientBuilder.build() : null;
    }

    public Mensaje generarRespuestaIA(String preguntaUsuario) {
        Mensaje mensajeBot = new Mensaje();
        mensajeBot.setRemitente("LibroBot IA");

        String respuestaTexto = generarTextoRespuesta(preguntaUsuario);
        mensajeBot.setContenido(respuestaTexto);
        return mensajeService.guardarMensaje(mensajeBot);
    }

    private String generarTextoRespuesta(String preguntaUsuario) {
        if (!StringUtils.hasText(preguntaUsuario)) {
            return "No recibí un mensaje válido para responder.";
        }

        if (chatClient == null) {
            return "La IA todavía no está disponible, pero tu mensaje fue recibido correctamente.";
        }

        String historialMongo = mensajeService.obtenerHistorial().stream()
                .map(mensaje -> mensaje.getRemitente() + ": " + mensaje.getContenido())
                .collect(Collectors.joining("\n"));

        String prompt = "Eres el asistente de LibroTech. Usa este historial de chat como contexto:\n"
                + historialMongo + "\n\nResponde de forma breve y clara a: " + preguntaUsuario;

        try {
            String respuestaTexto = chatClient.prompt().user(prompt).call().content();
            if (StringUtils.hasText(respuestaTexto)) {
                return respuestaTexto;
            }
        } catch (Exception exception) {
            // Si la llamada a Spring AI falla, devolvemos una respuesta simple para no romper el chat.
        }

        return "La IA no pudo generar una respuesta en este momento.";
    }
}
