package com.riwi.intro.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.intro.models.Task;
import com.riwi.intro.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable int id){
        return service.getTaskById(id);
    }

    @GetMapping
    public List<Task> getAllTasks(){
        return service.getAll();
    }
    
    @PutMapping("/{id}")
    public Task updateById(@PathVariable int id, @RequestBody String task){
        return service.updateTaskById(id, task);
    }

    @DeleteMapping("/{id}")
    public Task deleteById(@PathVariable int id){
        return service.deleteById(id);
    }

    @PostMapping
    public Task postTask(@RequestBody Task task){
        return service.save(task);
    }
}
