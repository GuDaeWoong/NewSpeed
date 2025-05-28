package com.example.newspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class NewSpeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewSpeedApplication.class, args);
    }

}
