package com.lab.chattech.repository;

 
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.lab.chattech.model.Mensaje;

import java.util.List;

@Repository
public interface MensajeRepository extends MongoRepository<Mensaje, String> {
    List<Mensaje> findTop10ByOrderByFechaEnvioDesc();
}
 