package com.resqnet.repository;

import com.resqnet.entity.Incident;
import com.resqnet.enums.IncidentStatus;
import com.resqnet.enums.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByStatusNotIn(List<IncidentStatus> statuses);
    List<Incident> findByStatusNotInOrderByPriorityScoreDescIdAsc(List<IncidentStatus> statuses);
    List<Incident> findByReportedById(Long userId);
    long countByPriorityLevel(PriorityLevel priorityLevel);
    long countByStatus(IncidentStatus status);
    long countByStatusNotIn(List<IncidentStatus> statuses);
}
