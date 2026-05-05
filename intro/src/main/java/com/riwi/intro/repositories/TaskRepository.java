package com.riwi.intro.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.riwi.intro.models.Task;

@Repository
public class TaskRepository {
    private List<Task> tasks = new ArrayList<>();

    public List<Task> findAll() {
        return tasks;
    }

    public void save(Task task){
        tasks.add(task);
    }

    public Task deleteById(int id){
        Task tarea = tasks.get(id);
        tasks.remove(tarea);
        return tarea;
    }

    public Task update(Task task, String description){
        int index = tasks.indexOf(task);
        if(index != -1){
            tasks.get(index).setDescription(description);
            return tasks.get(index);
        }
        return null;
    }

}
