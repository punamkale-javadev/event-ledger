package com.eventledger.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    private final DataSource dataSource;

    public HealthController(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public Map<String, Object> health(){
        Map<String, Object> response = new HashMap<>();

        response.put("service", "event-gateway");
        response.put("time", Instant.now());
        try{
            response.put("status","UP");
            response.put("database","CONNECTED");
        }
        catch(Exception ex){
            response.put("database","DISCONNECTED");
            response.put("error",ex.getMessage());
        }
        return response;
    }
}
