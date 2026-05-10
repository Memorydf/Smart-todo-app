package com.todo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.config.DeepSeekProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekUtil {

    private final RestTemplate restTemplate;
    private final DeepSeekProperties deepSeekProperties;
    private final ObjectMapper objectMapper;

    public String generate(String prompt) {
        String apiKey = deepSeekProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("DeepSeek API Key 未配置");
        }

        String base = deepSeekProperties.getBaseUrl().replaceAll("/+$", "");
        String url = base + "/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        Map<String, Object> body = new HashMap<>();
        body.put("model", deepSeekProperties.getModel());
        body.put("messages", messages);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(url, entity, Map.class);
            if (resp == null) {
                throw new IllegalStateException("DeepSeek API 返回为空");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IllegalStateException("DeepSeek API 返回格式异常");
            }

            Map<String, Object> choice = choices.get(0);
            @SuppressWarnings("unchecked")
            Map<String, String> message = (Map<String, String>) choice.get("message");
            if (message == null) {
                throw new IllegalStateException("DeepSeek API 返回消息为空");
            }

            String content = message.get("content");
            return content != null ? content.trim() : "";

        } catch (RestClientException e) {
            log.error("调用 DeepSeek API 失败: {}", e.getMessage(), e);
            throw new IllegalStateException("调用 DeepSeek API 失败: " + e.getMessage(), e);
        }
    }
}