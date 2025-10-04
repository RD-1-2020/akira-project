package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {

    List<NotificationMessage> findAllByStatus(NotificationMessage.Status status);
}
