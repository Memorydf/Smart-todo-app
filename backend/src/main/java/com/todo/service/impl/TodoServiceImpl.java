package com.todo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;
import com.todo.mapper.TodoMapper;
import com.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    @Override
    public Todo add(TodoDTO dto) {
        Todo t = new Todo();
        t.setTitle(dto.getTitle().trim());
        t.setContent(StringUtils.hasText(dto.getContent()) ? dto.getContent() : null);
        t.setPriority(StringUtils.hasText(dto.getPriority()) ? dto.getPriority() : "medium");
        t.setCategory(StringUtils.hasText(dto.getCategory()) ? dto.getCategory() : "other");
        t.setDeadline(dto.getDeadline());
        t.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        todoMapper.insert(t);
        return todoMapper.selectById(t.getId());
    }

    @Override
    public Todo update(TodoDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        Todo existing = todoMapper.selectById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("待办不存在");
        }
        existing.setTitle(dto.getTitle().trim());
        if (dto.getContent() != null) {
            existing.setContent(dto.getContent());
        }
        if (dto.getPriority() != null) {
            existing.setPriority(dto.getPriority());
        }
        if (dto.getCategory() != null) {
            existing.setCategory(dto.getCategory());
        }
        existing.setDeadline(dto.getDeadline());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        todoMapper.updateById(existing);
        return todoMapper.selectById(existing.getId());
    }

    @Override
    public void delete(Long id) {
        todoMapper.deleteById(id);
    }

    @Override
    public List<Todo> list() {
        return todoMapper.selectList(Wrappers.<Todo>lambdaQuery().orderByDesc(Todo::getUpdateTime));
    }

    @Override
    public Todo finish(Long id) {
        Todo existing = todoMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("待办不存在");
        }
        existing.setStatus(1);
        todoMapper.updateById(existing);
        return todoMapper.selectById(id);
    }

    @Override
    public void clearAll() {
        todoMapper.delete(Wrappers.<Todo>lambdaQuery().apply("1=1"));
    }
}
