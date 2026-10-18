package com.resqnet.service;

import com.resqnet.entity.*;
import com.resqnet.enums.IncidentStatus;
import com.resqnet.enums.ResourceStatus;
import com.resqnet.exception.BadRequestException;
import com.resqnet.repository.ResourceAllocationRepository;
import com.resqnet.repository.ResourceRepository;
import com.resqnet.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final ResourceRepository resourceRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final AllocationEngine allocationEngine;
    private final IncidentService incidentService;
    private final IncidentTimelineService timelineService;
    private final NotificationService notificationService;

    /**
     * Module 6: Intelligent Resource Allocation.
     * For a verified incident, rank all available resources by weighted score and
     * assign the best-fit ones (up to `count` per resource type request is left to caller).
     */
    @Transactional
    public List<ResourceAllocation> allocateBestResources(Long incidentId, int maxResources) {
        if (maxResources < 1) {
            throw new BadRequestException("maxResources must be at least 1");
        }

        Incident incident = incidentService.getById(incidentId);
        List<Resource> available = resourceRepository.findByStatusForUpdate(ResourceStatus.AVAILABLE);

        if (available.isEmpty()) {
            throw new BadRequestException("No available resources to allocate");
        }

        List<Resource> ranked = allocationEngine.rankCandidates(incident, available);
        List<ResourceAllocation> allocations = new java.util.ArrayList<>();

        int assigned = 0;
        for (Resource resource : ranked) {
            if (assigned >= maxResources) break;
            if (resource.getAvailableQuantity() == null || resource.getAvailableQuantity() <= 0) continue;

            double score = allocationEngine.score(incident, resource);
            double distance = HaversineUtil.distanceKm(incident.getLatitude(), incident.getLongitude(),
                    resource.getLatitude(), resource.getLongitude());
            double etaMin = (distance / 40.0) * 60.0; // assume avg 40km/h

            resource.setAvailableQuantity(resource.getAvailableQuantity() - 1);
            if (resource.getAvailableQuantity() <= 0) {
                resource.setStatus(ResourceStatus.ASSIGNED);
            }
            resource.setAssignedIncident(incident);
            resourceRepository.save(resource);

            ResourceAllocation allocation = ResourceAllocation.builder()
                    .incident(incident)
                    .resource(resource)
                    .allocationScore(score)
                    .distanceKm(distance)
                    .estimatedTravelTimeMin(etaMin)
                    .build();
            allocations.add(allocationRepository.save(allocation));

            timelineService.record(incident, resource.getResourceName() + " assigned (score "
                    + Math.round(score) + ")");
            assigned++;
        }

        if (!allocations.isEmpty()) {
            incidentService.updateStatus(incidentId, IncidentStatus.RESOURCE_ASSIGNED);
            notificationService.send(
                    "Resources Assigned",
                    assigned + " resource(s) assigned to incident #" + incidentId,
                    "INFO",
                    incident);
        }

        return allocations;
    }

    public List<ResourceAllocation> getByIncident(Long incidentId) {
        return allocationRepository.findByIncidentId(incidentId);
    }

    public List<ResourceAllocation> getAll() {
        return allocationRepository.findAll();
    }
}
