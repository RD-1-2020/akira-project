package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.SyslogMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyslogMessageRepository extends JpaRepository<SyslogMessage, Long> {

    List<SyslogMessage> findByMessageContainingIgnoreCase(String term);
}
