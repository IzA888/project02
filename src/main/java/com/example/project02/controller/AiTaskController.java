package com.example.project02.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.project02.service.AiTaskService;

@Controller
@RequestMapping("/agent")
public class AiTaskController {
    
    @Autowired
    private AiTaskService service;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping
    public String index(){
        return "agent-page";
    }

    @PostMapping("/ask")
    public String askAgent(@RequestParam String task) {
        service.sendTaskToAi(task);
    
        return "agent-page";
    }

    @GetMapping("/result")
    public ResponseEntity<String> getResult(){
        String resp = service.getRespostaIa();
        if (resp != null && !resp.trim().isEmpty()) {
            // 2. Prepara o contexto de dados para o Thymeleaf
            Context context = new Context();
            context.setVariable("response", resp);
            
            // 3. Renderiza manualmente APENAS o fragmento desejado
            // "agent_template" é o nome do arquivo .html, e "response-area" é o ID da div pai
            String htmlRenderizado = templateEngine.process(
                "agent_page", 
                Collections.singleton("#response-area"), 
                context
            );
            
            // 4. Retorna o HTML lindo gerado pelo Thymeleaf com o status 286
            return ResponseEntity.status(286)
                    .header("Content-Type", "text/html;charset=UTF-8") 
                    .body(htmlRenderizado);
        } else {
            //  Se a resposta ainda não estiver disponível, retorna um fragmento HTML indicando que estamos aguardando a resposta
            return ResponseEntity.ok()
                .body("<div id=\"response-area\" class=\"text-muted\">Aguardando processamento do agente...</div>");
        }

    }

    @PostMapping("/resposta")
    public ResponseEntity<String> receberResposta(@RequestBody String respostaIa) {
        service.listenAiResults(respostaIa);
        return ResponseEntity.ok("Resposta recebida com sucesso");
    }
}
