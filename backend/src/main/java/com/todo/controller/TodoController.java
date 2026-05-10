package com.todo.controller;

import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;
import com.todo.service.TodoService;
import com.todo.utils.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/add")
    public Result<Todo> add(@Valid @RequestBody TodoDTO dto) {
        return Result.ok(todoService.add(dto));
    }

    @PutMapping("/update")
    public Result<Todo> update(@Valid @RequestBody TodoDTO dto) {
        return Result.ok(todoService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<List<Todo>> list() {
        return Result.ok(todoService.list());
    }

    @PutMapping("/finish/{id}")
    public Result<Todo> finish(@PathVariable Long id) {
        return Result.ok(todoService.finish(id));
    }

    @DeleteMapping("/clear")
    public Result<Void> clearAll() {
        todoService.clearAll();
        return Result.ok();
    }
}
