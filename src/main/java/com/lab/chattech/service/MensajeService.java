package com.lab.chattech.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.model.Mensaje;
import com.lab.chattech.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MensajeService {
    
       public Mensaje guardarMensaje(Mensaje mensaje) {
        // Save the message using the repository and return the saved version
        Mensaje mensajeGuardado = mensajeRepository.save(mensaje);
        return mensajeGuardado;
    }


    public List<Mensaje> obtenerHistorial() {
        public List<Mensaje> obtenerTodosLosMensajes() {
        // Ask the repository for all messages and return them
        List<Mensaje> todos = mensajeRepository.findAll();
        return todos;
    }    
    
    
    // Get the 10 most recent messages (sorted newest first by the repository)
        List<Mensaje> mensajesRecientes = mensajeRepository.findTop10ByOrderByFechaEnvioDesc();
 
        // Reverse so the conversation flows from oldest to newest (top to bottom)
        // This makes it easier for the AI to understand the conversation flow
        List<Mensaje> mensajesOrdenados = new ArrayList<>(mensajesRecientes);
        Collections.reverse(mensajesOrdenados);
 
        return mensajesOrdenados;
    }
 
}