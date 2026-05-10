package com.todo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * AI 返回的 JSON 数组元素（用于解析后写入 todo 表）。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiGeneratedTodoItem {

    private String title;
    private String category;
    private String priority;
    private String content;
}
