package com.todo.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.config.DeepSeekProperties;
import com.todo.dto.AiGeneratedTodoItem;
import com.todo.dto.AiRequestDTO;
import com.todo.dto.TodoDTO;
import com.todo.entity.AiChat;
import com.todo.entity.Todo;
import com.todo.mapper.AiChatMapper;
import com.todo.service.AiService;
import com.todo.service.TodoService;
import com.todo.utils.DeepSeekUtil;
import com.todo.utils.OllamaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final OllamaUtil ollamaUtil;
    private final DeepSeekUtil deepSeekUtil;
    private final DeepSeekProperties deepSeekProperties;
    private final AiChatMapper aiChatMapper;
    private final TodoService todoService;
    private final ObjectMapper objectMapper;

    private boolean useDeepSeek() {
        return StringUtils.hasText(deepSeekProperties.getApiKey());
    }

    private String callAI(String prompt) {
        if (useDeepSeek()) {
            log.debug("使用 DeepSeek API");
            return deepSeekUtil.generate(prompt);
        } else {
            log.debug("使用 Ollama");
            return ollamaUtil.generate(prompt);
        }
    }

    @Override
    public AiChat chat(AiRequestDTO dto) {
        String q = dto.getQuestion();
        if (q == null || q.isBlank()) {
            throw new IllegalArgumentException("question 不能为空");
        }
        q = q.trim();
        String prompt = buildChatPrompt(q);
        String answer = callAI(prompt);
        AiChat row = new AiChat();
        row.setQuestion(q);
        row.setAnswer(answer);
        aiChatMapper.insert(row);
        return aiChatMapper.selectById(row.getId());
    }

    @Override
    public String generateTodo(AiRequestDTO dto) {
        String p = dto.getPrompt();
        if (p == null || p.isBlank()) {
            throw new IllegalArgumentException("prompt 不能为空");
        }
        p = p.trim();
        String prompt = buildGenerateTodoPrompt(p);
        return callAI(prompt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Todo> generateTodosAndSave(AiRequestDTO dto) {
        String p = dto.getPrompt();
        if (p == null || p.isBlank()) {
            throw new IllegalArgumentException("prompt 不能为空");
        }
        p = p.trim();
        String raw = callAI(buildStructuredJsonPrompt(p));
        List<TodoDTO> items = parseAiTodoItems(raw);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("未能从 AI 输出解析出有效待办，请换种描述重试");
        }
        List<Todo> saved = new ArrayList<>();
        for (TodoDTO item : items) {
            saved.add(todoService.add(item));
        }
        return saved;
    }

    private List<TodoDTO> parseAiTodoItems(String raw) {
        String trimmed = stripMarkdownFence(raw == null ? "" : raw.trim());
        try {
            ObjectMapper lenient = objectMapper.copy();
            lenient.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            int start = trimmed.indexOf('[');
            int end = trimmed.lastIndexOf(']');
            if (start >= 0 && end > start) {
                String json = trimmed.substring(start, end + 1);
                List<AiGeneratedTodoItem> list =
                        lenient.readValue(json, new TypeReference<>() {});
                List<TodoDTO> out = new ArrayList<>();
                for (AiGeneratedTodoItem it : list) {
                    if (it == null || !StringUtils.hasText(it.getTitle())) {
                        continue;
                    }
                    TodoDTO d = new TodoDTO();
                    d.setTitle(it.getTitle().trim());
                    d.setContent(StringUtils.hasText(it.getContent()) ? it.getContent().trim() : null);
                    d.setCategory(normalizeCategory(it.getCategory()));
                    d.setPriority(normalizePriority(it.getPriority()));
                    d.setStatus(0);
                    out.add(d);
                    if (out.size() >= 20) {
                        break;
                    }
                }
                if (!out.isEmpty()) {
                    return out;
                }
            }
        } catch (Exception ignored) {
        }
        return parseLineFallback(trimmed);
    }

    private static List<TodoDTO> parseLineFallback(String raw) {
        List<TodoDTO> out = new ArrayList<>();
        for (String line : raw.split("\\r?\\n")) {
            String t = line.trim();
            if (t.isEmpty()
                    || t.startsWith("{")
                    || t.startsWith("}")
                    || t.startsWith("[")) {
                continue;
            }
            t = t.replaceFirst("^[-*\\d.、）)]+\\s*", "");
            if (t.length() > 200) {
                t = t.substring(0, 200);
            }
            if (!t.isEmpty()) {
                TodoDTO d = new TodoDTO();
                d.setTitle(t);
                d.setCategory("other");
                d.setPriority("medium");
                d.setStatus(0);
                out.add(d);
            }
            if (out.size() >= 15) {
                break;
            }
        }
        return out;
    }

    private static String stripMarkdownFence(String s) {
        String x = s;
        if (x.startsWith("```")) {
            int i = x.indexOf('\n');
            if (i > 0) {
                x = x.substring(i + 1);
            } else {
                x = x.substring(3);
            }
        }
        if (x.endsWith("```")) {
            x = x.substring(0, x.length() - 3);
        }
        return x.trim();
    }

    private static String normalizeCategory(String c) {
        if (!StringUtils.hasText(c)) {
            return "other";
        }
        String x = c.trim().toLowerCase();
        return switch (x) {
            case "work", "study", "life", "other" -> x;
            default -> "other";
        };
    }

    private static String normalizePriority(String p) {
        if (!StringUtils.hasText(p)) {
            return "medium";
        }
        String x = p.trim().toLowerCase();
        return switch (x) {
            case "low", "medium", "high" -> x;
            default -> "medium";
        };
    }

    private static String buildStructuredJsonPrompt(String userGoal) {
        return "你是待办结构化助手。根据用户目标，生成 3～10 条待办。\n"
                + "只输出一个 JSON 数组，不要 markdown 代码块，不要解释文字。\n"
                + "每个元素字段：title（必填），category（work|study|life|other），priority（low|medium|high），content（可选）。\n"
                + "示例：[{\"title\":\"复习第三章\",\"category\":\"study\",\"priority\":\"high\"}]\n\n"
                + "用户目标：\n"
                + userGoal
                + "\n\nJSON：\n";
    }

    private static String buildChatPrompt(String userQuestion) {
        return "你是「智能待办」AI 助手，由 DeepSeek 驱动，擅长：待办拆解、学习计划、时间规划、执行与拖延建议。\n"
                + "要求：使用简体中文；先理解用户意图再回答；条理清晰、尽量精炼；需要时用短列表（以「•」开头）；不要编造用户未提及的事实；不要输出内部推理过程。\n\n"
                + "用户："
                + userQuestion
                + "\n\n助手：";
    }

    private static String buildGenerateTodoPrompt(String userGoal) {
        return "你是待办生成助手。用户会描述目标或场景，请把目标拆成 3～8 条「明天就能动手」的具体待办标题。\n"
                + "输出要求：\n"
                + "1) 每条单独一行；不要编号；不要空行。\n"
                + "2) 若类别明显，行尾用括号标注之一：(work) (study) (life) (other)。\n"
                + "3) 需要时可加优先级提示：[高] [中] [低]。\n"
                + "4) 不要寒暄，不要解释规则。\n\n"
                + "用户描述：\n"
                + userGoal
                + "\n\n待办列表：\n";
    }
}