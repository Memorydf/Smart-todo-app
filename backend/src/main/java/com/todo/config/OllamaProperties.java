package com.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ollama")
public class OllamaProperties {

    /** Ollama 服务根地址，如 http://localhost:11434 */
    private String baseUrl = "http://localhost:11434";

    /** 模型名，需本地已拉取，如 deepseek-r1:7b */
    private String model = "deepseek-r1:7b";
}
