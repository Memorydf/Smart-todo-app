package com.todo.controller;

import com.todo.dto.AiRequestDTO;
import com.todo.entity.AiChat;
import com.todo.entity.Todo;
import com.todo.service.AiService;
import com.todo.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<AiChat> chat(@RequestBody AiRequestDTO dto) {
        return Result.ok(aiService.chat(dto));
    }

    @PostMapping("/generateTodo")
    public Result<Map<String, String>> generateTodo(@RequestBody AiRequestDTO dto) {
        String text = aiService.generateTodo(dto);
        return Result.ok(Map.of("suggestions", text));
    }

    /** AI 生成待办并写入 MySQL，前端拉取 /todo/list 刷新 */
    @PostMapping("/generateTodosAndSave")
    public Result<List<Todo>> generateTodosAndSave(@RequestBody AiRequestDTO dto) {
        return Result.ok(aiService.generateTodosAndSave(dto));
    }
}
