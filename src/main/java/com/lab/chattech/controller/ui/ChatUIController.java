package com.lab.chattech.controller.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.MensajeService;

@Controller
@RequestMapping("/admin")
public class ChatUIController {

    private final MensajeService mensajeService;

    public ChatUIController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @GetMapping("/chat")
    public String mostrarSalaDeChat(Model model) {
        // Cargamos el historial para que la sala muestre mensajes apenas abre.
        List<Mensaje> historial = mensajeService.obtenerTodosLosMensajes();
        model.addAttribute("historial", historial);
        return "chat/sala";
    }
}
