package org.azurecloud.solutions.akira.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientEventDto {
    private String clientHostname;
    private String message;
    private LocalDateTime date;
}

