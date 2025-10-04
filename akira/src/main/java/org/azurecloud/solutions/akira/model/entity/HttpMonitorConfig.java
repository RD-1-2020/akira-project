package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("HTTP")
public class HttpMonitorConfig extends MonitorConfig {
    private String url;
    private int expectedStatus;
}
