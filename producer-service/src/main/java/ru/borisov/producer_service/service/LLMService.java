package ru.borisov.producer_service.service;

import java.io.IOException;

public interface LLMService {
    String sendMessage(String message) throws IOException, InterruptedException;
}
