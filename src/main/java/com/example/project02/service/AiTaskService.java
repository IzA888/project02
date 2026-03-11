package com.example.project02.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.project02.model.AiTask;
import com.example.project02.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiTaskService {

    private TaskRepository taskRepository;
    private final Map<Long, String> resultado = new ConcurrentHashMap<>();
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private AiTask aiTask;

    public void sendTaskToAi(AiTask task) {
        //envia para o tópico que o flask está ouvindo
        try{
            kafkaTemplate.send("task", new ObjectMapper().writeValueAsString(task));
            aiTask.setPrompt(task.getPrompt());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    

    @KafkaListener(topics = "response", groupId = "spring-group")
    public void ListenAiResults(String mensage){
        try {
            aiTask = new ObjectMapper().readValue(mensage, AiTask.class);
            resultado.put(aiTask.getId(), aiTask.getResposta());
            saveTask(aiTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLatestResult(){
        return resultado.getOrDefault(aiTask.getId(), "Aguardando resposta...");
    }
    
    public String saveTask(AiTask task) {
        //salvar resposta no banco
        return "Task salva com sucesso" + taskRepository.save(task);
    }
}
