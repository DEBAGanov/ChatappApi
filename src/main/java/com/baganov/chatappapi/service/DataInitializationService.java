package com.baganov.chatappapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

@Service
@Slf4j
public class DataInitializationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeData() {
        try {
            log.info("Starting data initialization from CSV");
            Resource resource = new ClassPathResource("db/data/questions.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            // Пропускаем заголовок
            String header = reader.readLine();
            log.info("CSV header: {}", header);

            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",\\s*");
                    jdbcTemplate.update(
                            "INSERT INTO posts (title, content) VALUES (?, ?)",
                            data[0], data[1]);
                    count++;
                } catch (Exception e) {
                    log.error("Error processing line: {}", line, e);
                }
            }
            log.info("Successfully loaded {} records from CSV", count);
        } catch (Exception e) {
            log.error("Error initializing data from CSV", e);
        }
    }
}