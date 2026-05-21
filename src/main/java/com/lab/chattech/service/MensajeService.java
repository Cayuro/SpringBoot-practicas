package com.lab.chattech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.repository.MensajeRepository;

@Service
public class MensajeService {

    private final MensajeRepository messageRepository;
    private final List<Mensaje> mensajesEnMemoria = new CopyOnWriteArrayList<>();
    private volatile boolean usarBaseDeDatos = true;

    public MensajeService(MensajeRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Mensaje guardarMensaje(Mensaje message) {
        normalizarMensaje(message);

        if (usarBaseDeDatos) {
            try {
                return messageRepository.save(message);
            } catch (Exception exception) {
                usarBaseDeDatos = false;
            }
        }

        if (message.getId() == null || message.getId().isBlank()) {
            message.setId(UUID.randomUUID().toString());
        }

        mensajesEnMemoria.add(message);
        return message;
    }

    public List<Mensaje> obtenerTodosLosMensajes() {
        if (usarBaseDeDatos) {
            try {
                return messageRepository.findAllByOrderByFechaEnvioAsc();
            } catch (Exception exception) {
                usarBaseDeDatos = false;
            }
        }

        return ordenarPorFecha(mensajesEnMemoria);
    }

    public List<Mensaje> obtenerHistorial() {
        List<Mensaje> mensajes;

        if (usarBaseDeDatos) {
            try {
                mensajes = messageRepository.findTop10ByOrderByFechaEnvioDesc();
            } catch (Exception exception) {
                usarBaseDeDatos = false;
                mensajes = mensajesEnMemoria;
            }
        } else {
            mensajes = mensajesEnMemoria;
        }

        List<Mensaje> historial = ordenarPorFecha(mensajes);
        if (historial.size() <= 10) {
            return historial;
        }

        return new ArrayList<>(historial.subList(historial.size() - 10, historial.size()));
    }

    private void normalizarMensaje(Mensaje message) {
        if (message.getFechaEnvio() == null) {
            message.setFechaEnvio(LocalDateTime.now());
        }

        if (message.getRemitente() == null || message.getRemitente().isBlank()) {
            message.setRemitente("Usuario");
        }

        if (message.getContenido() == null) {
            message.setContenido("");
        }
    }

    private List<Mensaje> ordenarPorFecha(List<Mensaje> mensajes) {
        return mensajes.stream()
                .sorted(Comparator.comparing(Mensaje::getFechaEnvio, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}