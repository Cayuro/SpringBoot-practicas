package com.lab.chattech.controller.ws;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.BotIAService;
import com.lab.chattech.service.MensajeService;

@Controller
public class ChatSocketController {

    private final MensajeService mensajeService;
    private final BotIAService botIAService;
    private final SimpMessagingTemplate mensajeria;
    private final ObjectProvider<TaskExecutor> taskExecutorProvider;

    public ChatSocketController(MensajeService mensajeService,
            BotIAService botIAService,
            SimpMessagingTemplate mensajeria,
            ObjectProvider<TaskExecutor> taskExecutorProvider) {
        this.mensajeService = mensajeService;
        this.botIAService = botIAService;
        this.mensajeria = mensajeria;
        this.taskExecutorProvider = taskExecutorProvider;
    }

    @MessageMapping("/enviar")
    @SendTo("/topic/mensajes")
    public Mensaje procesarMensajeDeUsuario(@Payload Mensaje mensajeRecibido) {
        Mensaje mensajeGuardado = mensajeService.guardarMensaje(mensajeRecibido);

        // La respuesta de IA se genera en segundo plano para no bloquear la conexión WebSocket.
        TaskExecutor taskExecutor = taskExecutorProvider.getIfAvailable();
        Runnable tareaIA = () -> {
            Mensaje respuestaIA = botIAService.generarRespuestaIA(mensajeGuardado.getContenido());
            mensajeria.convertAndSend("/topic/mensajes", respuestaIA);
        };

        if (taskExecutor != null) {
            taskExecutor.execute(tareaIA);
        } else {
            new Thread(tareaIA).start();
        }

        return mensajeGuardado;
    }
}
