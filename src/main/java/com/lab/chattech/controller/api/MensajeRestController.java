package com.lab.chattech.controller.api;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
    This controller exposes a REST API for messages.

    A REST API lets other systems (or our frontend JavaScript) interact with
    our data using standard HTTP methods (GET, POST, PUT, DELETE).
    The responses are usually in JSON format, not HTML pages.

    WHY @RestController AND NOT @Controller?
    - @Controller: Spring expects the method to return the name of a view (HTML template)
    - @RestController: Spring automatically converts the return value to JSON
      and sends it as the response body. It is @Controller + @ResponseBody combined.

    @RequestMapping("/api/mensajes") means:
    All endpoints in this class will start with "/api/mensajes".
    This keeps our API organized and separated from the UI routes.

    Example URL for this controller: http://localhost:8080/api/mensajes
*/
@RestController
@RequestMapping("/api/mensajes")
public class MensajeRestController {

    /*
        We inject the service to handle database operations.
        The controller should never directly talk to the repository.
        All logic goes through the service layer.
    */
    @Autowired
    private MensajeService mensajeService;

    /*
        GET /api/mensajes
        Returns all messages stored in MongoDB as a JSON array.

        This endpoint is useful for:
        - Testing that MongoDB is working
        - External applications that need the message history
        - Debugging the chat history

        ResponseEntity<List<Mensaje>> means:
        - We return a full HTTP response object
        - The body contains a List of Mensaje objects
        - Spring converts the list to a JSON array automatically

        ResponseEntity.ok() sends the response with HTTP status 200 (OK).
    */
    @GetMapping
    public ResponseEntity<List<Mensaje>> obtenerTodosLosMensajes() {
        // Ask the service for all messages
        List<Mensaje> mensajes = mensajeService.obtenerTodosLosMensajes();

        // Wrap the list in a 200 OK response and return it
        return ResponseEntity.ok(mensajes);
    }

    /*
        POST /api/mensajes
        Accepts a JSON message in the request body and saves it to MongoDB.

        @RequestBody tells Spring to read the JSON from the request body
        and convert it into a Mensaje object automatically.

        This endpoint is mainly useful for:
        - Testing the save functionality without going through the WebSocket
        - External systems that want to post messages programmatically

        Example request body (JSON):
        {
            "remitente": "Juan",
            "contenido": "Hola desde la API!"
        }
    */
    @PostMapping
    public ResponseEntity<Mensaje> guardarMensaje(@RequestBody Mensaje mensaje) {
        // Save the message using the service
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensaje);

        // Return the saved message (which now includes the MongoDB-generated ID)
        return ResponseEntity.ok(mensajeGuardado);
    }

}