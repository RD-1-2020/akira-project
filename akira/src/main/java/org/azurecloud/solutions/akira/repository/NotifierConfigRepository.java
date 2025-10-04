package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifierConfigRepository extends JpaRepository<NotifierConfig, Long> {
}
