package ru.borisov.producer_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.borisov.producer_service.kafka.KafkaProducer;
import ru.borisov.producer_service.service.LLMService;

import java.io.IOException;

@RestController
public class PoemController {

    private final LLMService llmService;
    private final KafkaProducer kafkaProducer;
    private final String defaultPrompt;

    @Autowired
    public PoemController(
            LLMService llmService,
            KafkaProducer kafkaProducer,
            @Value("${llm.default.prompt}") String defaultPrompt
    ) {
        this.llmService = llmService;
        this.kafkaProducer = kafkaProducer;
        this.defaultPrompt = defaultPrompt;
    }

    @PostMapping("/send-poem")
    public String sendPoem() throws IOException, InterruptedException {
        String message = llmService.sendMessage(defaultPrompt);
        kafkaProducer.sendMessageAsync(message);
        return message;
    }
}
