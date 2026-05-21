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

@RestController
@RequestMapping("/api/mensajes")
public class MensajeRestController {

    
    private final MensajeService mensajeService;

    public final ResponseEntity<List<Mensaje>> obtenerTodosLosMensajes() {
        // Ask the service for all messages
        List<Mensaje> mensajes = mensajeService.obtenerTodosLosMensajes();

        // Wrap the list in a 200 OK response and return it
        return ResponseEntity.ok(mensajes);
    }

    //inyección de dependencias a través del constructor
    public MensajeRestController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

  
    @PostMapping
    public ResponseEntity<Mensaje> guardarMensaje(@RequestBody Mensaje mensaje) {
        // Save the message using the service
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensaje);

        // Return the saved message (which now includes the MongoDB-generated ID)
        return ResponseEntity.ok(mensajeGuardado);
    }

}