package com.sparta.village;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VillageApplication {

    public static void main(String[] args) {
        SpringApplication.run(VillageApplication.class, args);
    }

}
