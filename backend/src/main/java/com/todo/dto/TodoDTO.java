package com.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String priority;

    private LocalDate deadline;

    private String category;

    /** 0 未完成 1 已完成 */
    private Integer status;
}
