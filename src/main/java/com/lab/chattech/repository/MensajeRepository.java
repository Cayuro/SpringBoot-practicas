package com.lab.chattech.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.lab.chattech.model.Mensaje;

@Repository
public interface MensajeRepository extends MongoRepository<Mensaje, String> {

    List<Mensaje> findAllByOrderByFechaEnvioAsc();

    List<Mensaje> findTop10ByOrderByFechaEnvioDesc();
}
