package org.azurecloud.solutions.akira.model.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "monitor_type", discriminatorType = DiscriminatorType.STRING)
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
    private String name;

    @ManyToOne
    private NotifierConfig notifierConfig;
}
