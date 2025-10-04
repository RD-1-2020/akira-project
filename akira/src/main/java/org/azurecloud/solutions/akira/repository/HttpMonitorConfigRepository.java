package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.HttpMonitorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpMonitorConfigRepository extends JpaRepository<HttpMonitorConfig, Long> {
}
