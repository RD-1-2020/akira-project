package org.azurecloud.solutions.akira.repository;

import org.azurecloud.solutions.akira.model.entity.SyslogMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyslogMessageRepository extends JpaRepository<SyslogMessage, Long> {
    List<SyslogMessage> findBySourceIdOrderByReceivedAtDesc(Long sourceId);

    @Query(value = "SELECT * FROM syslog_message s " +
           "WHERE s.source_id = :sourceId " +
           "AND s.received_at >= :startDate " +
           "ORDER BY s.received_at DESC", nativeQuery = true)
    List<SyslogMessage> findBySourceIdAndStartDateOrderByReceivedAtDesc(
            @Param("sourceId") Long sourceId,
            @Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT * FROM syslog_message s " +
           "WHERE s.source_id = :sourceId " +
           "AND s.received_at <= :endDate " +
           "ORDER BY s.received_at DESC", nativeQuery = true)
    List<SyslogMessage> findBySourceIdAndEndDateOrderByReceivedAtDesc(
            @Param("sourceId") Long sourceId,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT * FROM syslog_message s " +
           "WHERE s.source_id = :sourceId " +
           "AND s.received_at >= :startDate " +
           "AND s.received_at <= :endDate " +
           "ORDER BY s.received_at DESC", nativeQuery = true)
    List<SyslogMessage> findBySourceIdAndDateRangeOrderByReceivedAtDesc(
            @Param("sourceId") Long sourceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT EXTRACT(HOUR FROM s.receivedAt) as hour, COUNT(s) as count " +
           "FROM SyslogMessage s " +
           "WHERE s.sourceId = :sourceId " +
           "AND s.receivedAt >= :startDate " +
           "AND s.receivedAt <= :endDate " +
           "GROUP BY EXTRACT(HOUR FROM s.receivedAt) " +
           "ORDER BY hour")
    List<Object[]> countByHourAndSourceIdInRange(
            @Param("sourceId") Long sourceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
