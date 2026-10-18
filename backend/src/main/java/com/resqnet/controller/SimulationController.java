package com.resqnet.controller;

import com.resqnet.dto.IncidentRequest;
import com.resqnet.entity.Incident;
import com.resqnet.enums.DisasterType;
import com.resqnet.enums.RoadConditionType;
import com.resqnet.service.AllocationService;
import com.resqnet.service.IncidentService;
import com.resqnet.service.RoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Module 9: Software-only data simulation, for live demos without hardware.
 */
@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class SimulationController {

    private final IncidentService incidentService;
    private final AllocationService allocationService;
    private final RoadService roadService;

    @PostMapping("/flood")
    public ResponseEntity<Incident> simulateFlood(@RequestParam double lat, @RequestParam double lng,
                                                    @RequestParam String locationName,
                                                    @RequestParam(defaultValue = "150") int affectedPeople,
                                                    Authentication auth) {
        IncidentRequest request = new IncidentRequest();
        request.setDisasterType(DisasterType.FLOOD);
        request.setTitle("Simulated Flood at " + locationName);
        request.setDescription("Auto-generated simulation scenario for demo purposes.");
        request.setLatitude(lat);
        request.setLongitude(lng);
        request.setLocationName(locationName);
        request.setSeverity(9);
        request.setAffectedPeople(affectedPeople);
        request.setInjuredPeople(Math.max(1, affectedPeople / 12));
        request.setVulnerablePeople(Math.max(1, affectedPeople / 8));
        request.setRequiredResources("ambulance, boat, rescue team");

        Incident incident = incidentService.createIncident(request, auth.getName());
        incidentService.verifyIncident(incident.getId(), auth.getName());
        allocationService.allocateBestResources(incident.getId(), 5);
        return ResponseEntity.ok(incidentService.getById(incident.getId()));
    }

    @PostMapping("/road-blockage/{roadId}")
    public ResponseEntity<?> simulateRoadBlockage(@PathVariable Long roadId) {
        return ResponseEntity.ok(roadService.updateCondition(roadId, RoadConditionType.BLOCKED, true));
    }
}
