package com.lab.chattech.service;

import com.lab.chattech.model.Mensaje;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    This service is responsible for talking to the OpenAI API using Spring AI.

    Spring AI is a library that makes it easy to connect to AI language models
    like ChatGPT (OpenAI). It handles the HTTP calls to OpenAI automatically.
    We just need to build a prompt (a text instruction) and ask for a response.

    What is RAG? (Retrieval-Augmented Generation)
    RAG is a technique where we retrieve data from a database first,
    then pass it as context to the AI model before asking a question.
    This is exactly what we are doing: we retrieve chat history from MongoDB
    and give it to OpenAI so it can generate a relevant, contextual response.
    This is a simple version of RAG.
*/
@Service
public class BotIAService {

    /*
        ChatClient is the main Spring AI object we use to talk to OpenAI.
        It is similar to how we use RestTemplate to call REST APIs.
        We build it using the ChatClient.Builder, which Spring provides automatically
        when we include the Spring AI OpenAI Starter in our pom.xml.
    */
    private final ChatClient chatClient;

    /*
        We need the MensajeService to:
        1. Get the recent conversation history (to give context to the AI)
        2. Save the AI's response as a new message in MongoDB
    */
    @Autowired
    private MensajeService mensajeService;

    /*
        This constructor receives a ChatClient.Builder object.
        Spring AI automatically creates and provides this builder.
        We use it to build our ChatClient instance.

        Why do we use constructor injection here instead of @Autowired on the field?
        Because ChatClient requires a builder pattern to set it up,
        not just a simple injection of an existing object.
    */
    public BotIAService(ChatClient.Builder chatClientBuilder) {
        // Build the ChatClient from the builder that Spring AI provides
        this.chatClient = chatClientBuilder.build();
    }

    /*
        This is the main method of this service.
        It receives the question the user asked, builds a smart prompt with context,
        asks OpenAI for a response, and returns the response as a saved Mensaje.

        The method name means "generate AI response" in Spanish.
    */
    public Mensaje generarRespuestaIA(String preguntaDelUsuario) {

        // Step 1: Get the recent conversation history from MongoDB
        // We use this to give the AI context about what has been discussed
        List<Mensaje> historial = mensajeService.obtenerHistorial();

        // Step 2: Convert the list of messages into a single text block
        // We format each message as "remitente: contenido" on its own line
        // This makes the conversation easy for the AI to read and understand
        StringBuilder contexto = new StringBuilder();
        for (Mensaje msg : historial) {
            contexto.append(msg.getRemitente());
            contexto.append(": ");
            contexto.append(msg.getContenido());
            contexto.append("\n");
        }

        // Step 3: Build the full prompt text that we will send to OpenAI
        // - A system instruction (who the AI is and what it should do)
        // - The conversation history (so it understands the context)
        // - The new question from the user
        String promptCompleto = "Eres LibroBot, el asistente virtual de LibroTech. " +
                "Tu mision es ayudar a los bibliotecarios con sus preguntas. " +
                "Responde siempre en espanol de forma amable, clara y concisa. " +
                "Aqui tienes el historial reciente de la conversacion para que entiendas el contexto:\n\n" +
                contexto.toString() +
                "\nAhora responde a esta nueva pregunta: " +
                preguntaDelUsuario;

        // Step 4: Send the prompt to OpenAI using Spring AI and get the response text
        // .prompt() starts a new request
        // .user(promptCompleto) sets the message we are sending
        // .call() sends the request to OpenAI and waits for the response
        // .content() extracts just the text from the response object
        String textoRespuesta = chatClient
                .prompt()
                .user(promptCompleto)
                .call()
                .content();

        // Step 5: Create a new Mensaje object to represent the AI's response
        Mensaje mensajeDelBot = new Mensaje();
        mensajeDelBot.setRemitente("LibroBot IA");
        mensajeDelBot.setContenido(textoRespuesta);

        // Step 6: Save the AI's response to MongoDB so it becomes part of the history
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensajeDelBot);
        return mensajeGuardado;
    }

}