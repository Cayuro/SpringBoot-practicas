package com.lab.chattech.model;
 
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
import java.time.LocalDateTime;
 
@Document(collection = "mensajes")
public class Mensaje {
 
   
    @Id
    private String id;
 
    private String remitente;

    private String contenido;
 
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    public Mensaje() {
    }
    public Mensaje(String remitente, String contenido) {
        this.remitente = remitente;
        this.contenido = contenido;
    }
 
    // ---- Getters and Setters ----
    // Spring and MongoDB need these to read and write the field values.
    // Without them, the fields would be inaccessible from outside this class.
 
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public String getRemitente() {
        return remitente;
    }
 
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }
 
    public String getContenido() {
        return contenido;
    }
 
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
 
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
 
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
 
    /*
        toString is useful for debugging. When we print a Mensaje object,
        Java calls this method and shows us all the field values as text.
    */
    @Override
    public String toString() {
        return "Mensaje{" +
                "id='" + id + '\'' +
                ", remitente='" + remitente + '\'' +
                ", contenido='" + contenido + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                '}';
    }
 
}
 