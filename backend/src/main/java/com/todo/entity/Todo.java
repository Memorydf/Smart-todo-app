package com.todo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("todo")
public class Todo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /** low / medium / high */
    private String priority;

    /** 0 未完成 1 已完成 */
    private Integer status;

    private LocalDate deadline;

    /** work / study / life / other */
    private String category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
