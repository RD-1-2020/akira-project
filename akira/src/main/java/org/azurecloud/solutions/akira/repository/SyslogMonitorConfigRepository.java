package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.SyslogMonitorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyslogMonitorConfigRepository extends JpaRepository<SyslogMonitorConfig, Long> {
    Optional<SyslogMonitorConfig> findBySourceUrl(String sourceAddress);
}
