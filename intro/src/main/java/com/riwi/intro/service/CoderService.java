package com.riwi.intro.service;

@Service
@Scope("prototype")
public class CoderService{
    private final CoderRepository repository;

    public CoderService(CoderRepository repository){
        this.repository = repository;
    }

    public List<Coder> getAll(){
        return repository.findAll();
    }

    public Coder create(Coder coder){

        if(coder.getName() == null || coder.getName().isEmpty()){
            throw new RuntimeException("El nombre es obligatorio");
        }
        repository.save(coder);
        return coder;
    }
}