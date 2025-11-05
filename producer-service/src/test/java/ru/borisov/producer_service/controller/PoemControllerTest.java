package ru.borisov.producer_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.borisov.producer_service.kafka.KafkaProducer;
import ru.borisov.producer_service.service.LLMService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PoemControllerTest {

    @Mock
    private LLMService llmService;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private PoemController poemController;

    private MockMvc mockMvc;

    private final String REF_POEM = "TEST_POEM";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(poemController).build();
    }

    @Test
    public void test() throws Exception {
        when(llmService.sendMessage(any())).thenReturn(REF_POEM);

        mockMvc.perform(post("/send-poem"))
                .andExpect(status().isOk())
                .andExpect(content().string(REF_POEM));

        verify(kafkaProducer, times(1)).sendMessageAsync(anyString());
    }

}
