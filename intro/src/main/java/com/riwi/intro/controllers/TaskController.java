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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/task")
@Tag(name = "Eventos", description = "Operaciones relacionadas con la gestión de eventos")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Obtener tarea por ID", description = "Retorna la tarea registrada con el ID especificado.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode="404", description="Pagina no encontrada")
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable int id){
        return service.getTaskById(id);
    }

    @Operation(summary = "Obtener todas las tareas", description = "Retorna una lista paginada de todas las tareas registradas.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode="404", description="Pagina no encontrada")
    @GetMapping
    public List<Task> getAllTasks(){
        return service.getAll();
    }
    
    @Operation(summary="Editar la tarea", description="edita los campos de la tarea que coinciden con el id especificado")
    @ApiResponse(responseCode="200", description="Tarea editada correctamente")
    @PutMapping("/{id}")
    public Task updateById(@PathVariable int id, @RequestBody String task){
        return service.updateTaskById(id, task);
    }

    @Operation(summary="Eliminar tarea", description="Elimina la tarea con el ID especificado")
    @ApiResponse(responseCode="200", description="Tarea Eliminada correctamente")
    @DeleteMapping("/{id}")
    public Task deleteById(@PathVariable int id){
        return service.deleteById(id);
    }

    @Operation(summary = "Crear una nueva tarea", description = "Registra una tarea en el sistema y retorna el objeto creado.")
    @ApiResponse(responseCode = "201", description = "Tarea creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    public Task postTask(@RequestBody Task task){
        return service.save(task);
    }
}
