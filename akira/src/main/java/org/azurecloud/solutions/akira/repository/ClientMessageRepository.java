package org.azurecloud.solutions.akira.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.azurecloud.solutions.akira.model.entity.ClientMessage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientMessageRepository extends JpaRepository<ClientMessage, Long> {

    @Transactional(readOnly = true)
    Page<ClientMessage> findByClientHostnameAndReceivedAtBetween(String hostname, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ClientMessage> findByClientHostname(String hostname, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("""
            SELECT extract(hour from m.receivedAt) as h, count(m) as cnt FROM ClientMessage m
                        WHERE m.receivedAt >= :startOfDay
                        GROUP BY h ORDER BY h
            """)
    List<Object[]> countMessagesByHourSince(@Param("startOfDay") LocalDateTime startOfDay);

    @Transactional(readOnly = true)
    @Query("""
            SELECT extract(hour from m.receivedAt) as h, count(m) as cnt FROM ClientMessage m
                    WHERE m.client.hostname = :hostname AND m.receivedAt >= :startOfDay
                    GROUP BY h ORDER BY h
            """)
    List<Object[]> countMessagesByHourAndClientSince(@Param("hostname") String hostname, @Param("startOfDay") LocalDateTime startOfDay);

}

