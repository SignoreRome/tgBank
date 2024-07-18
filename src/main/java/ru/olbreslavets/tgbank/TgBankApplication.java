package ru.olbreslavets.tgbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TgBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgBankApplication.class, args);
    }

}
