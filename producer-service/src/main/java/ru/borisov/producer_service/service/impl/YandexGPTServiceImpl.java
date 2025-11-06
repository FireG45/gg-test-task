package ru.borisov.producer_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.borisov.producer_service.service.LLMService;
import ru.borisov.producer_service.service.handler.LLMResponseHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class YandexGPTServiceImpl implements LLMService {

    private final HttpClient http = HttpClient.newHttpClient();

    private final String apiUrl;
    private final String apiKey;
    private final String apiSecret;
    private final ObjectMapper mapper;
    private final LLMResponseHandler responseHandler;

    @Autowired
    public YandexGPTServiceImpl(
            @Value("${llm.api.url}") String apiUrl,
            @Value("${llm.api.key}") String apiKey,
            @Value("${llm.api.secret}") String apiSecret,
            ObjectMapper mapper,
            LLMResponseHandler responseHandler
    ) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.mapper = mapper;
        this.responseHandler = responseHandler;
    }

    @Override
    public String sendMessage(String message) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Api-Key " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(
                        """
                                {
                                    "modelUri": "gpt://%s/yandexgpt-lite",
                                    "completionOptions": {
                                        "stream": false,
                                        "temperature": 0.6,
                                        "maxTokens": "2000"
                                    },
                                    "messages": [
                                        {
                                            "role": "user",
                                            "text": "%s"
                                        }
                                    ]
                                }
                                """.formatted(apiSecret, message)
                ))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = responseHandler.handleResponse(response.statusCode(), response.body(), mapper);

        JsonNode root = mapper.readTree(responseBody);

        return root.path("result")
            .path("alternatives")
            .get(0)
            .path("message")
            .path("text")
            .asText();
    }
}
