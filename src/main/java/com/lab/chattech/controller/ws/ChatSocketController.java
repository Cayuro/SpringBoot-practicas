package com.lab.chattech.controller.ws;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.BotIAService;
import com.lab.chattech.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatSocketController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private BotIAService botIAService;

    @Autowired
    private SimpMessagingTemplate mensajeria;

 
    @MessageMapping("/enviar")
    @SendTo("/tema/mensajes")
    public Mensaje procesarMensajeDeUsuario(Mensaje mensajeRecibido) {

          Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensajeRecibido);

        new Thread(() -> {
          Mensaje respuestaIA = botIAService.generarRespuestaIA(mensajeGuardado.getContenido());

           mensajeria.convertAndSend("/tema/mensajes", respuestaIA);

        }).start(); // .start() launches the new thread immediately

       return mensajeGuardado;
    }

}