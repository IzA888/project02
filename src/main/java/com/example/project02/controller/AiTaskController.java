package com.example.project02.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.project02.service.AiTaskService;

@Controller
@RequestMapping("/agent")
public class AiTaskController {
    
    @Autowired
    private AiTaskService service;

    @GetMapping
    public String index(){
        return "agent-page";
    }

    @PostMapping("/ask")
    public String askAgent(@RequestParam String task) {
        service.sendTaskToAi(task);
    
        return "agent-page";
    }

    @PostMapping("/resposta")
    public ResponseEntity<String> receberResposta(@RequestBody String respostaIa) {
        service.listenAiResults(respostaIa);
        return ResponseEntity.ok("Resposta recebida com sucesso");
    }
}
