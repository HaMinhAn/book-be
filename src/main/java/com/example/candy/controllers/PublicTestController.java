package com.example.candy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicTestController.class);
    
    @GetMapping("/test")
    public Map<String, String> publicTest() {
        logger.info("Public test endpoint accessed");
        return Map.of("message", "This is a public endpoint and works fine!");
    }
}
