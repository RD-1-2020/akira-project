package org.azurecloud.solutions.akira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main entry point for the Akira Spring Boot application.
 * Enables scheduling for background tasks like monitoring.
 */
@EnableScheduling
@SpringBootApplication
public class AkiraApplication {

    /**
     * The main method that starts the Spring Boot application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(AkiraApplication.class, args);
    }

}
