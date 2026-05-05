package com.riwi.intro.repositories;

@Reppository
public class CoderRepository{
    private List<Coder> coders = new ArrayList<>();

    public List<Coder> findAll(){
        return coders;
    }
    public void save(Coder coder){
        coders.add(coder);
    }
}