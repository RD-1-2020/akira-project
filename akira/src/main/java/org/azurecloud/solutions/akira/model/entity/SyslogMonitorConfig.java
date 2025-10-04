package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("SYSLOG")
public class SyslogMonitorConfig extends MonitorConfig {
    private String sourceUrl;
    private Duration noMessageInterval;
    private LocalDateTime lastMessageAt;
}
