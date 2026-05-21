package com.lab.chattech.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.repository.MensajeRepository;

@Service
public class MensajeService {

    private final MensajeRepository messageRepository;

    public MensajeService(MensajeRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Mensaje guardarMensaje(Mensaje message) {
        return  messageRepository.save(message);
    }

    public List<Mensaje> obtenerHistorial() {
        return messageRepository.findAll();
    }

}