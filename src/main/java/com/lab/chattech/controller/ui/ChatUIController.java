package com.lab.chattech.controller.ui;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
// @RequiredArgsConstructor
@RequestMapping("/admin")
public class ChatUIController {


    private final MensajeService mensajeService;

    public ChatUIController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @GetMapping("/chat")
    public String mostrarSalaDeChat(Model model) {

        // Get all messages from MongoDB to display as the initial history
        List<Mensaje> historial = mensajeService.obtenerTodosLosMensajes();

        model.addAttribute("historial", historial);

         return "chat/sala";
    }

}