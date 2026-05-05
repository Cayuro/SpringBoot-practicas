package com.riwi.intro.service;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.riwi.intro.models.Task;
import com.riwi.intro.repositories.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository repository;

    //Constructor repository
    public TaskService(TaskRepository repository){
        this.repository = repository;
    }
    
    public List<Task> getAll(){
        return repository.findAll();
    }

    public Task getTaskById(int id){
        return repository.finById(id);
    }

    public Task save(Task task){
        if (task.getDescription() == null) {
            throw new RuntimeException("Debe tener una descripción de la tarea");
        } else if(getTaskById(task.getId()) != null){
            throw new RuntimeException("Ese ID ya está asignado a una task");
        }
        repository.save(task);
        return task;
    }

    public Task updateTaskById(int id, String description){
        return repository.updateById(id, description);
    }

    public Task deleteById(int id){
        return repository.deleteById(id);
    }
}
