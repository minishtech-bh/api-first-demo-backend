package com.example.todo.controller;

import com.example.todo.api.TodoApi;
import com.example.todo.model.CreateTodoRequest;
import com.example.todo.model.Todo;
import com.example.todo.model.UpdateTodoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class TodoController implements TodoApi {

    private final List<Todo> todos = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public ResponseEntity<List<Todo>> getTodos() {
        return ResponseEntity.ok(todos);
    }

    @Override
    public ResponseEntity<Todo> createTodo(CreateTodoRequest createTodoRequest) {
        Todo todo = new Todo();
        todo.setId(idSequence.getAndIncrement());
        todo.setTitle(createTodoRequest.getTitle());
        todo.setCompleted(false);
        todos.add(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    @Override
    public ResponseEntity<Todo> getTodoById(Long id) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Todo> updateTodo(Long id, UpdateTodoRequest updateTodoRequest) {
        for (Todo todo : todos) {
            if (todo.getId().equals(id)) {
                todo.setTitle(updateTodoRequest.getTitle());
                todo.setCompleted(updateTodoRequest.getCompleted());
                return ResponseEntity.ok(todo);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteTodo(Long id) {
        boolean removed = todos.removeIf(t -> t.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
