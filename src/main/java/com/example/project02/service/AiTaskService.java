package com.example.project02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.project02.model.AiTask;
import com.example.project02.repository.TaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiTaskService {

    private TaskRepository taskRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    

    @KafkaListener(topics = "response", groupId = "spring-group")
    public void listenAiResults(String mensagem) {
        System.out.println("Recebido: " + mensagem);
        try {
            JsonNode respostaIa = new ObjectMapper().readTree(mensagem);
    
            if (respostaIa.isArray() && respostaIa.has(0) && respostaIa.get(0).has(0)) {
                    String resposta = respostaIa.get(0).get(0).asText();
                    aiTask.setResposta(resposta);
                    saveTask(aiTask);
                    System.out.println(aiTask);
            } else {
                throw new IllegalArgumentException("Resposta recebida em formato inesperado: " + mensagem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
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
