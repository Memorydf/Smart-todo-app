package com.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StatisticsOverviewDTO {

    private long total;
    private long finished;
    private long pending;
    private long overdue;
    private long todayPending;
    private Map<String, Long> categoryPending;
}
