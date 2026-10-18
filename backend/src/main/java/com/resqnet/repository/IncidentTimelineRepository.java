package com.resqnet.repository;

import com.resqnet.entity.IncidentTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentTimelineRepository extends JpaRepository<IncidentTimeline, Long> {
    List<IncidentTimeline> findByIncidentIdOrderByTimestampAsc(Long incidentId);
}
