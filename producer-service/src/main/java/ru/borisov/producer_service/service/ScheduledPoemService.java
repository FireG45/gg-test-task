package ru.borisov.producer_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.borisov.producer_service.kafka.KafkaProducer;

import java.io.IOException;

@Service
public class ScheduledPoemService {

    private final LLMService llmService;
    private final KafkaProducer kafkaProducer;
    private final String defaultPrompt;

    @Autowired
    public ScheduledPoemService(
            LLMService llmService,
            KafkaProducer kafkaProducer,
            @Value("${llm.default.prompt:Напиши четверостишие в стиле Есенина о природе}") String defaultPrompt
    ) {
        this.llmService = llmService;
        this.kafkaProducer = kafkaProducer;
        this.defaultPrompt = defaultPrompt;
    }

    @Async("jobExecutor")
    @Scheduled(cron = "${poem.cron:0 * * * * *}")
    public void sendPoem() throws IOException, InterruptedException {
        kafkaProducer.sendMessageAsync(llmService.sendMessage(defaultPrompt));
    }
}
