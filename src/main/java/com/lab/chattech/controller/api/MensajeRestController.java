package com.lab.chattech.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.MensajeService;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeRestController {

    private final MensajeService mensajeService;

    public MensajeRestController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @GetMapping
    public ResponseEntity<List<Mensaje>> obtenerTodosLosMensajes() {
        // Este endpoint ayuda a revisar el historial completo desde HTTP.
        List<Mensaje> mensajes = mensajeService.obtenerTodosLosMensajes();
        return ResponseEntity.ok(mensajes);
    }

    @PostMapping
    public ResponseEntity<Mensaje> guardarMensaje(@RequestBody Mensaje mensaje) {
        // Guardamos el mensaje para que también quede disponible en el historial.
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensaje);
        return ResponseEntity.ok(mensajeGuardado);
    }
}
