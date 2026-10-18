package com.resqnet.service;

import com.resqnet.dto.IncidentRequest;
import com.resqnet.entity.Incident;
import com.resqnet.entity.Resource;
import com.resqnet.entity.User;
import com.resqnet.enums.IncidentStatus;
import com.resqnet.enums.ResourceStatus;
import com.resqnet.exception.ResourceNotFoundException;
import com.resqnet.repository.IncidentRepository;
import com.resqnet.repository.ResourceRepository;
import com.resqnet.repository.UserRepository;
import com.resqnet.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final PriorityEngine priorityEngine;
    private final IncidentTimelineService timelineService;
    private final NotificationService notificationService;

    @Transactional
    public Incident createIncident(IncidentRequest request, String reporterEmail) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));

        Incident incident = Incident.builder()
                .disasterType(request.getDisasterType())
                .title(request.getTitle())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .severity(request.getSeverity())
                .affectedPeople(request.getAffectedPeople())
                .injuredPeople(request.getInjuredPeople())
                .vulnerablePeople(request.getVulnerablePeople())
                .requiredResources(request.getRequiredResources())
                .imageUrl(request.getImageUrl())
                .reportedBy(reporter)
                .status(IncidentStatus.REPORTED)
                .build();

        recalculatePriority(incident);
        incident = incidentRepository.save(incident);
        timelineService.record(incident, "Incident reported");

        if (incident.getPriorityLevel() != null
                && incident.getPriorityLevel().name().equals("CRITICAL")) {
            notificationService.send(
                    "CRITICAL INCIDENT",
                    incident.getDisasterType() + " reported at " + incident.getLocationName()
                            + ". " + safe(incident.getAffectedPeople()) + " people affected.",
                    "CRITICAL",
                    incident);
        }

        return incident;
    }

    /** Recalculates the priority score using the nearest available resource distance. */
    public void recalculatePriority(Incident incident) {
        double nearestDistance = -1;
        List<Resource> available = resourceRepository.findByStatus(ResourceStatus.AVAILABLE);
        for (Resource r : available) {
            if (r.getLatitude() == null || r.getLongitude() == null) continue;
            double d = HaversineUtil.distanceKm(incident.getLatitude(), incident.getLongitude(),
                    r.getLatitude(), r.getLongitude());
            if (nearestDistance < 0 || d < nearestDistance) nearestDistance = d;
        }
        double score = priorityEngine.calculateScore(incident, nearestDistance);
        incident.setPriorityScore(score);
        incident.setPriorityLevel(priorityEngine.levelFromScore(score));
    }

    @Transactional
    public Incident verifyIncident(Long id, String verifierEmail) {
        Incident incident = getById(id);
        User verifier = userRepository.findByEmail(verifierEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Verifier not found"));

        incident.setStatus(IncidentStatus.VERIFIED);
        incident.setVerifiedBy(verifier);
        incident.setVerifiedAt(LocalDateTime.now());
        recalculatePriority(incident);
        incidentRepository.save(incident);
        timelineService.record(incident, "Incident verified");
        timelineService.record(incident, "Priority set to " + incident.getPriorityLevel());
        return incident;
    }

    @Transactional
    public Incident updateStatus(Long id, IncidentStatus status) {
        Incident incident = getById(id);
        incident.setStatus(status);
        if (status == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        incidentRepository.save(incident);
        timelineService.record(incident, "Status changed to " + status);
        return incident;
    }

    public Incident getById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + id));
    }

    public List<Incident> getAll() {
        return incidentRepository.findAll();
    }

    /** Returns active incidents ordered by priority in the database. */
    public List<Incident> getActiveIncidentsByPriority() {
        return incidentRepository.findByStatusNotInOrderByPriorityScoreDescIdAsc(
                List.of(IncidentStatus.RESOLVED, IncidentStatus.REJECTED));
    }

    public void delete(Long id) {
        incidentRepository.deleteById(id);
    }

    private int safe(Integer v) {
        return v == null ? 0 : v;
    }
}
