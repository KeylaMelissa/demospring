package com.example.demospring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelayController {

    @GetMapping("/api/delay")
    public ResponseEntity<String> delay() throws InterruptedException {

        Thread.sleep(70000); // 70 segundos

        return ResponseEntity.ok("Respuesta después de 70 segundos");
    }
}

