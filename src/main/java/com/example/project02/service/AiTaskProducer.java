package com.example.project02.service;

import com.example.project02.model.AiTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class AiTaskProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTaskToAi(AiTask task) {
        //envia para o tópico que o flask está ouvindo
        kafkaTemplate.send("ai_task", new ObjectMapper().writeValueAsString(task));
    }
}
