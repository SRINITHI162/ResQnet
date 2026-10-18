package com.resqnet.service;

import com.resqnet.entity.Incident;
import com.resqnet.enums.DisasterType;
import com.resqnet.enums.PriorityLevel;
import org.springframework.stereotype.Service;

/**
 * Rule-based priority calculation engine.
 *
 * Weighted formula (0-100 scale):
 *   Severity Score       = 30%
 *   Affected People      = 20%
 *   Medical Urgency      = 20%   (injured people ratio)
 *   Vulnerability        = 10%   (vulnerable people ratio)
 *   Response Time        = 10%   (estimated distance to nearest resource; lower = better)
 *   Disaster Type Weight = 10%   (some disaster types are inherently higher risk)
 *
 * Time complexity: O(1) per incident.
 */
@Service
public class PriorityEngine {

    public double calculateScore(Incident incident, double nearestResourceDistanceKm) {
        double severityScore = normalizeSeverity(incident.getSeverity());          // 0-100
        double affectedScore = normalizeCount(incident.getAffectedPeople(), 500);  // 0-100
        double medicalScore = normalizeCount(incident.getInjuredPeople(), 100);    // 0-100
        double vulnerabilityScore = normalizeCount(incident.getVulnerablePeople(), 100);
        double responseTimeScore = normalizeDistance(nearestResourceDistanceKm);   // 0-100 (closer = higher)
        double disasterTypeScore = disasterTypeWeight(incident.getDisasterType()); // 0-100

        double total =
                  (severityScore * 0.30)
                + (affectedScore * 0.20)
                + (medicalScore * 0.20)
                + (vulnerabilityScore * 0.10)
                + (responseTimeScore * 0.10)
                + (disasterTypeScore * 0.10);

        return Math.round(total * 100.0) / 100.0;
    }

    public PriorityLevel levelFromScore(double score) {
        if (score >= 80) return PriorityLevel.CRITICAL;
        if (score >= 60) return PriorityLevel.HIGH;
        if (score >= 35) return PriorityLevel.MEDIUM;
        return PriorityLevel.LOW;
    }

    private double normalizeSeverity(Integer severity) {
        if (severity == null) return 0;
        return Math.min(100, (severity / 10.0) * 100);
    }

    private double normalizeCount(Integer count, int cap) {
        if (count == null || count <= 0) return 0;
        return Math.min(100, (count / (double) cap) * 100);
    }

    /** Closer resources => higher score. Distance beyond 50km contributes ~0. */
    private double normalizeDistance(double distanceKm) {
        if (distanceKm < 0) return 50; // unknown, neutral
        double score = 100 - (distanceKm / 50.0) * 100;
        return Math.max(0, Math.min(100, score));
    }

    private double disasterTypeWeight(DisasterType type) {
        if (type == null) return 50;
        return switch (type) {
            case EARTHQUAKE, BUILDING_COLLAPSE, FIRE -> 100;
            case FLOOD, CYCLONE, MEDICAL_EMERGENCY -> 85;
            case LANDSLIDE, STORM -> 70;
            case ACCIDENT -> 60;
            case OTHER -> 40;
        };
    }
}
