package com.todo.dto;

import lombok.Data;

@Data
public class AiRequestDTO {

    /** 对话：用户问题 */
    private String question;

    /** 生成待办：自然语言需求 */
    private String prompt;
}
