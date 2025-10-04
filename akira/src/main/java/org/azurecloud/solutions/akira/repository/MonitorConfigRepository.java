package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.MonitorConfig;
import org.azurecloud.solutions.akira.model.entity.SyslogMonitorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonitorConfigRepository extends JpaRepository<MonitorConfig, Long> {

    @Query("SELECT s FROM SyslogMonitorConfig s WHERE s.sourceUrl = :sourceUrl")
    Optional<SyslogMonitorConfig> findBySourceUrl(String sourceUrl);
}
