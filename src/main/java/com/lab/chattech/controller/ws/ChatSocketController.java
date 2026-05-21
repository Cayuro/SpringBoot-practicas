package com.lab.chattech.controller.ws;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.BotIAService;
import com.lab.chattech.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/*
    This controller handles messages that arrive through the WebSocket.

    It is different from a regular @RestController because:
    - A @RestController handles HTTP requests (GET, POST, etc.)
    - This @Controller handles STOMP messages that arrive via WebSocket

    HOW THE WEBSOCKET MESSAGE FLOW WORKS:

    1. Browser: user types a message and clicks "Send"
    2. Browser: JavaScript calls stompClient.send("/app/enviar", {}, messageJson)
       - "/app" is the application destination prefix from WebSocketConfig
       - "/enviar" matches the @MessageMapping below
    3. Spring: finds the method annotated with @MessageMapping("/enviar")
       and calls it with the message data
    4. Controller: saves the user's message to MongoDB
    5. Controller: the @SendTo annotation makes Spring send the user's message
       to ALL clients subscribed to "/tema/mensajes"
    6. Controller: a new background thread calls the AI service
    7. AI Service: builds a prompt, calls OpenAI, gets a response
    8. Controller: uses SimpMessagingTemplate to send the AI response
       to ALL clients subscribed to "/tema/mensajes"

    @Controller (without @Rest) is used here because we are not handling HTTP.
    We are handling WebSocket/STOMP messages, which have a different lifecycle.
*/
@Controller
public class ChatSocketController {

    /*
        MensajeService is used to save the user's message to MongoDB.
        We always save messages before broadcasting them so we have a record.
    */
    @Autowired
    private MensajeService mensajeService;

    /*
        BotIAService is used to generate the AI response.
        It will call OpenAI using the conversation history as context.
    */
    @Autowired
    private BotIAService botIAService;

    /*
        SimpMessagingTemplate is a Spring utility class that lets us send
        WebSocket messages from anywhere in our code, not just from controller methods.

        Normally, @SendTo handles broadcasting the return value of a method.
        But for the AI response, we need to send a SECOND message AFTER the
        first one is already returned. For that, we need SimpMessagingTemplate.

        Think of it as: @SendTo is automatic, SimpMessagingTemplate is manual.

        "Simp" stands for "Simple Messaging Protocol" and is Spring's internal
        WebSocket messaging layer.
    */
    @Autowired
    private SimpMessagingTemplate mensajeria;

    /*
        @MessageMapping("/enviar") means:
        "When a STOMP message arrives at /app/enviar, call this method."

        The "/app" prefix is added by the browser automatically.
        In WebSocketConfig, we set "/app" as the applicationDestinationPrefix.
        So the browser sends to "/app/enviar" and Spring routes to "/enviar".

        @SendTo("/tema/mensajes") means:
        "After this method returns an object, send that object to ALL clients
        subscribed to the '/tema/mensajes' channel."

        This is how we broadcast the user's message to everyone in the chat room.
        One person sends a message, and ALL connected browsers receive it instantly.
    */
    @MessageMapping("/enviar")
    @SendTo("/tema/mensajes")
    public Mensaje procesarMensajeDeUsuario(Mensaje mensajeRecibido) {

        // Step 1: Save the user's message to MongoDB so it is persisted
        // We save first so the AI can include this message in its context
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensajeRecibido);

        // Step 2: Start a new background thread to call the AI
        // WHY A SEPARATE THREAD?
        // The @SendTo annotation sends the user's message to everyone RIGHT NOW.
        // If we called the AI inside this method (synchronously), EVERYONE would
        // have to wait 2-5 seconds before seeing the user's own message appear.
        // That would make the chat feel broken and slow.
        //
        // By using a separate thread:
        // - The user's message is broadcast IMMEDIATELY (return value of this method)
        // - The AI thinking happens in the background
        // - When the AI finishes, its response is sent separately
        // This way, the user sees their message right away and waits for the AI separately.
        //
        // This is a simple threading approach. In a production app, you would use
        // Spring's @Async or a proper thread pool, but for a learning project
        // new Thread() is perfectly fine and easy to understand.
        new Thread(() -> {
            // This block runs in a separate thread, not blocking the main flow

            // Ask the AI service to generate a response to the user's message
            // This may take a few seconds because it calls the OpenAI API
            Mensaje respuestaIA = botIAService.generarRespuestaIA(mensajeGuardado.getContenido());

            // After the AI responds, use SimpMessagingTemplate to send it
            // to ALL clients subscribed to "/tema/mensajes".
            // We cannot use @SendTo here because this runs in a background thread,
            // not in the original message handler method.
            mensajeria.convertAndSend("/tema/mensajes", respuestaIA);

        }).start(); // .start() launches the new thread immediately

        // Step 3: Return the user's saved message immediately.
        // @SendTo picks up this return value and broadcasts it to "/tema/mensajes".
        return mensajeGuardado;
    }

}