package com.resqnet.controller;

import com.resqnet.dto.IncidentRequest;
import com.resqnet.entity.Incident;
import com.resqnet.entity.IncidentTimeline;
import com.resqnet.enums.IncidentStatus;
import com.resqnet.service.IncidentService;
import com.resqnet.service.IncidentTimelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentTimelineService timelineService;

    @GetMapping
    public ResponseEntity<List<Incident>> getAll() {
        return ResponseEntity.ok(incidentService.getAll());
    }

    @GetMapping("/priority-queue")
    public ResponseEntity<List<Incident>> getActiveByPriority() {
        return ResponseEntity.ok(incidentService.getActiveIncidentsByPriority());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getById(id));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<IncidentTimeline>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(timelineService.getTimeline(id));
    }

    @PostMapping
    public ResponseEntity<Incident> create(@Valid @RequestBody IncidentRequest request, Authentication auth) {
        return ResponseEntity.ok(incidentService.createIncident(request, auth.getName()));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<Incident> verify(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(incidentService.verifyIncident(id, auth.getName()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(@PathVariable Long id, @RequestParam IncidentStatus status) {
        return ResponseEntity.ok(incidentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
