package com.resqnet.controller;

import com.resqnet.entity.ResourceAllocation;
import com.resqnet.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @GetMapping
    public ResponseEntity<List<ResourceAllocation>> getAll() {
        return ResponseEntity.ok(allocationService.getAll());
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<ResourceAllocation>> getByIncident(@PathVariable Long incidentId) {
        return ResponseEntity.ok(allocationService.getByIncident(incidentId));
    }

    @PostMapping("/incident/{incidentId}/auto-allocate")
    public ResponseEntity<List<ResourceAllocation>> autoAllocate(@PathVariable Long incidentId,
                                                                   @RequestParam(defaultValue = "3") int maxResources) {
        return ResponseEntity.ok(allocationService.allocateBestResources(incidentId, maxResources));
    }
}
