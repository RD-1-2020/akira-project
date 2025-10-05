package org.azurecloud.solutions.akira.model.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "monitor_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HttpMonitorConfig.class, name = "HTTP"),
        @JsonSubTypes.Type(value = SyslogMonitorConfig.class, name = "SYSLOG")
})
public abstract class MonitorConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private MonitorStatus status = MonitorStatus.ACTIVE;

    @ManyToOne
    private NotifierConfig notifierConfig;
}
