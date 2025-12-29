package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "clients")
public class Client {

    @Id
    @Column(nullable = false, unique = true)
    private String hostname;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private ClientStatus status;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ClientStatus.OK;
        }
    }
}

