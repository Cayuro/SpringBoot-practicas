package com.riwi.intro.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.riwi.intro.models.Coder;

@Repository
public class CoderRepository{
    private List<Coder> coders = new ArrayList<>();

    public Coder findById(Long id){
        for(Coder coder : coders){
            if(coder.getId().equals(id)){
                return coder;
            }
        }
        return null;
    }
    
    public List<Coder> findAll(){
        return coders;
    }
    public void save(Coder coder){
        coders.add(coder);
    }

    
    public Coder delete(Long id){
     Coder found = findById(id);
    if(found != null){
        coders.remove(found);
    }
    return found;
}

    public Coder update(Long id, Coder update){
        for(Coder coder : coders){
            if(coder.getId().equals(id)){
                coder.setName(update.getName());
                coder.setClan(update.getClan());
                return coder;
            }
        }
        return null;
    }
}