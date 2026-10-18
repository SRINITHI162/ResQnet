package com.resqnet.service;

import com.resqnet.entity.Incident;
import com.resqnet.entity.Resource;
import com.resqnet.enums.PriorityLevel;
import com.resqnet.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Weighted greedy resource allocation engine.
 *
 * Allocation Score = Priority Weight + Resource Suitability + Distance Score
 *                     + Availability Score + Response Time Score
 *
 * Candidates are ranked by score (descending) and the best-fit available resources
 * are selected first -- this is NOT "assign the first available resource".
 *
 * Time complexity: O(n log n) per incident, where n = number of candidate resources.
 */
@Service
public class AllocationEngine {

    public double score(Incident incident, Resource resource) {
        double priorityWeight = priorityWeight(incident.getPriorityLevel());
        double suitability = suitabilityScore(incident, resource);
        double distanceKm = HaversineUtil.distanceKm(
                incident.getLatitude(), incident.getLongitude(),
                resource.getLatitude(), resource.getLongitude());
        double distanceScore = Math.max(0, 100 - (distanceKm / 50.0) * 100);
        double availabilityScore = (resource.getAvailableQuantity() != null && resource.getAvailableQuantity() > 0) ? 100 : 0;
        double responseTimeScore = distanceScore; // proxy: closer resource = faster response

        return (priorityWeight * 0.30)
                + (suitability * 0.30)
                + (distanceScore * 0.20)
                + (availabilityScore * 0.10)
                + (responseTimeScore * 0.10);
    }

    /** Rank candidate resources for a given incident, best match first. */
    public List<Resource> rankCandidates(Incident incident, List<Resource> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble((Resource r) -> score(incident, r)).reversed())
                .toList();
    }

    private double priorityWeight(PriorityLevel level) {
        if (level == null) return 50;
        return switch (level) {
            case CRITICAL -> 100;
            case HIGH -> 75;
            case MEDIUM -> 50;
            case LOW -> 25;
        };
    }

    private double suitabilityScore(Incident incident, Resource resource) {
        // Basic suitability: does the resource type make sense for the disaster type / required resources text?
        if (incident.getRequiredResources() == null) return 60;
        String required = incident.getRequiredResources().toLowerCase();
        String typeName = resource.getResourceType().name().toLowerCase().replace("_", " ");
        return required.contains(typeName.split(" ")[0]) ? 100 : 60;
    }
}
