package com.todo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.todo.dto.StatisticsOverviewDTO;
import com.todo.entity.Todo;
import com.todo.mapper.TodoMapper;
import com.todo.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TodoMapper todoMapper;

    @Override
    public StatisticsOverviewDTO overview() {
        List<Todo> all = todoMapper.selectList(Wrappers.<Todo>lambdaQuery());
        LocalDate today = LocalDate.now();

        long total = all.size();
        long finished = all.stream().filter(this::isFinished).count();
        long pending = total - finished;

        long overdue =
                all.stream()
                        .filter(t -> !isFinished(t))
                        .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(today))
                        .count();

        long todayPending =
                all.stream()
                        .filter(t -> !isFinished(t))
                        .filter(t -> today.equals(t.getDeadline()))
                        .count();

        Map<String, Long> categoryPending = new LinkedHashMap<>();
        for (String c : List.of("work", "study", "life", "other")) {
            long n =
                    all.stream()
                            .filter(t -> !isFinished(t))
                            .filter(t -> c.equals(t.getCategory()))
                            .count();
            categoryPending.put(c, n);
        }

        return StatisticsOverviewDTO.builder()
                .total(total)
                .finished(finished)
                .pending(pending)
                .overdue(overdue)
                .todayPending(todayPending)
                .categoryPending(categoryPending)
                .build();
    }

    private boolean isFinished(Todo t) {
        return t.getStatus() != null && t.getStatus() == 1;
    }
}
