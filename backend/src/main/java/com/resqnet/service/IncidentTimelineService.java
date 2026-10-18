package com.resqnet.service;

import com.resqnet.entity.Incident;
import com.resqnet.entity.IncidentTimeline;
import com.resqnet.repository.IncidentTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentTimelineService {

    private final IncidentTimelineRepository timelineRepository;

    public void record(Incident incident, String event) {
        timelineRepository.save(IncidentTimeline.builder()
                .incident(incident)
                .event(event)
                .build());
    }

    public List<IncidentTimeline> getTimeline(Long incidentId) {
        return timelineRepository.findByIncidentIdOrderByTimestampAsc(incidentId);
    }
}
