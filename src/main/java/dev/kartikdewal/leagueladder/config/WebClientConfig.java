package dev.kartikdewal.leagueladder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${api.baseUrl:}")
    private String baseUrl;

    @Value("${api.key:}")
    private String apiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        if (baseUrl.isEmpty()) {
            logger.error("API baseUrl is not set. Please configure 'api.baseUrl' in application.properties.");
            throw new IllegalStateException("API baseUrl is not set.");
        }
        if (apiKey.isEmpty()) {
            logger.error("API key is not set. Please configure 'api.key' in application.properties.");
            throw new IllegalStateException("API key is not set.");
        } else if (!apiKey.matches("^[a-zA-Z0-9]{64}$")) {
            logger.error("API key is invalid.");
            throw new IllegalStateException("API key is invalid.");
        }
        return builder.baseUrl(baseUrl).build();
    }
}