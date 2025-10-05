package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;

@Entity
@Table(name = "http_monitor_config")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class HttpMonitorConfig extends MonitorConfig {
    @Column(nullable = false)
    private String url;
    
    @Column(nullable = false)
    private int expectedStatus;
    
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration timeout;
}
