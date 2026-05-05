package com.riwi.intro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.riwi.intro.models.Coder;
import com.riwi.intro.repositories.CoderRepository;

@Service
public class CoderService{
    private final CoderRepository repository;

    //constructor
    public CoderService(CoderRepository repository){
        this.repository = repository;
    }

    //getall
    public List<Coder> getAll(){
        return repository.findAll();
    }

    //create coder
    public Coder create(Coder coder){
        
        if(coder.getName() == null || coder.getName().isEmpty()){
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (getCoderById(coder.getId()) != null) {
            throw new RuntimeException("El ID ya existe");
        }
        repository.save(coder);
        return coder;
    }

    // getCoder
    public Coder getCoderById(Long id){
        return repository.findById(id);
    }

    // delete coder
    public Coder deleteCoderById(Long id){
        return repository.delete(id);
    }

    // put coder

    public Coder updateCoderById(Long id, Coder update){
        return repository.update(id, update);
    }
}