package com.todo.utils;

import com.todo.config.OllamaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 调用本地 Ollama {@code /api/generate}（与方案一致：model + prompt + stream:false）。
 * 部分模型可能在回复中包含推理标签块（如 think），展示前会剥离。
 */
@Component
@RequiredArgsConstructor
public class OllamaUtil {

    /** R1 常见推理包裹标签（拼接避免模板误解析） */
    private static final Pattern REASONING_BLOCK_THINK = Pattern.compile(
            String.join("", "<", "think", ">", "[\\s\\S]*?", "<", "/think", ">"),
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern REASONING_BLOCK_REASONING = Pattern.compile(
            String.join("", "<", "reasoning", ">", "[\\s\\S]*?", "<", "/reasoning", ">"),
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final RestTemplate restTemplate;
    private final OllamaProperties ollamaProperties;

    public String generate(String prompt) {
        String base = ollamaProperties.getBaseUrl().replaceAll("/+$", "");
        String url = base + "/api/generate";

        Map<String, Object> body = new HashMap<>();
        body.put("model", ollamaProperties.getModel());
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(url, entity, Map.class);
            if (resp == null) {
                throw new IllegalStateException("Ollama 返回为空");
            }
            Object responseText = resp.get("response");
            String raw = responseText != null ? responseText.toString() : "";
            return stripReasoning(raw);
        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "调用 Ollama 失败，请确认已启动 Ollama 服务且配置正确: "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * 去掉模型的推理标签块，避免用户看到长推理过程。
     */
    public static String stripReasoning(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw == null ? "" : raw.trim();
        }
        String stripped =
                REASONING_BLOCK_REASONING.matcher(
                                REASONING_BLOCK_THINK.matcher(raw).replaceAll(""))
                        .replaceAll("")
                        .trim();
        return stripped.isEmpty() ? raw.trim() : stripped;
    }
}
