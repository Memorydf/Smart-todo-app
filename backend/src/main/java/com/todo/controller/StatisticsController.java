package com.todo.controller;

import com.todo.dto.StatisticsOverviewDTO;
import com.todo.service.StatisticsService;
import com.todo.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<StatisticsOverviewDTO> overview() {
        return Result.ok(statisticsService.overview());
    }
}
