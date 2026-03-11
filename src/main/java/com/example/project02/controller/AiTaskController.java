package com.example.project02.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.project02.model.AiTask;
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
    public void askAgent(@RequestParam AiTask task, RedirectAttributes ra) {
        service.sendTaskToAi(task);
        
        ra.addFlashAttribute("Processando...");
    }

    @GetMapping("/result")
    public String getResult(Model model){
        model.addAttribute("response-area", service.getLatestResult());
        return "agent-page";
    }
}
