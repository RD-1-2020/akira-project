package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "syslog_monitor_config")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class SyslogMonitorConfig extends MonitorConfig {
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\\\.|$)){4}$", message = "Invalid IP address")
    @Column(nullable = false)
    private String sourceIp;
    
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration noMessageInterval;
    
    private LocalDateTime lastMessageAt;
}
