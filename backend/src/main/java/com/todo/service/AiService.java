package com.todo.service;

import com.todo.dto.AiRequestDTO;
import com.todo.entity.AiChat;
import com.todo.entity.Todo;

import java.util.List;

public interface AiService {

    AiChat chat(AiRequestDTO dto);

    String generateTodo(AiRequestDTO dto);

    /**
     * AI 生成结构化待办并批量写入 MySQL，返回已保存记录（方案：自动生成 + 持久化 + 前端刷新列表）。
     */
    List<Todo> generateTodosAndSave(AiRequestDTO dto);
}
