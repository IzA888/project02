package com.example.project02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.project02.controller.SseController;
import com.example.project02.model.AiTask;
import com.example.project02.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiTaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private SseController sseController;

    private AiTask aiTask = new AiTask();

    public void sendTaskToAi(String task) {
        //envia para o tópico que o flask está ouvindo
        try{
            kafkaTemplate.send("task", new ObjectMapper().writeValueAsString(task));
            aiTask.setPrompt(task);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    

    @KafkaListener(topics = "task-results", groupId = "spring-group")
    public void listenAiResults(String mensagem) {
        System.out.println("Recebido");
        try {
            if (mensagem.trim().startsWith("{")){
               String resposta = new ObjectMapper().readTree(mensagem).get("resposta").asText();
               aiTask.setResposta(resposta);
               System.out.println("Resposta extraída");
            } else {
                aiTask.setResposta(mensagem);
            }
            sseController.dispararTela(aiTask.getResposta());
            saveTask(aiTask);
            System.out.println("salvo");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar mensagem do Kafka: " + e.getMessage(), e);            
        }
    }

    public String getRespostaIa() {
        if (aiTask.getResposta() == null) {
            return "Aguardando resposta da IA...";
        } else {
            return aiTask.getResposta();
        }
    }
    
    public AiTask saveTask(AiTask task) {
        //salvar resposta no banco
        if (task != null) {
            return taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Task não pode ser nula");
        }
    }
}
