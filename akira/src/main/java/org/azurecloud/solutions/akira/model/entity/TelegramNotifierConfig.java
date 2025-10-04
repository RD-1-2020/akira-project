package org.azurecloud.solutions.akira.model.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("TELEGRAM")
@Data
@JsonTypeName("TELEGRAM")
public class TelegramNotifierConfig extends NotifierConfig {
    private String botToken;
    private String chatId;
}
