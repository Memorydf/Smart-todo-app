package com.todo.service;

import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;

import java.util.List;

public interface TodoService {

    Todo add(TodoDTO dto);

    Todo update(TodoDTO dto);

    void delete(Long id);

    List<Todo> list();

    Todo finish(Long id);

    /** 清空全部待办（设置页等） */
    void clearAll();
}
