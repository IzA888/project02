package com.example.project02.controller;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    // Lista thread-safe para armazenar as sessões ativas dos navegadores
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        // Define o timeout da conexão (ex: 5 minutos / 300.000ms)
        SseEmitter emitter = new SseEmitter(300_000L);
        this.emitters.add(emitter);

        // Remove da lista caso o usuário feche a aba ou expire
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError((e) -> this.emitters.remove(emitter));

        return emitter;
    }

    public void dispararTela(String msg) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("agent-response").data(msg));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}