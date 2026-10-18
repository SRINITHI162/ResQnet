package com.resqnet.controller;

import com.resqnet.entity.Road;
import com.resqnet.enums.RoadConditionType;
import com.resqnet.service.RoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
@RequiredArgsConstructor
public class RoadController {

    private final RoadService roadService;

    @GetMapping
    public ResponseEntity<List<Road>> getAll() {
        return ResponseEntity.ok(roadService.getAll());
    }

    @PostMapping
    public ResponseEntity<Road> create(@RequestBody Road road) {
        return ResponseEntity.ok(roadService.create(road));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Road> updateStatus(@PathVariable Long id,
                                              @RequestParam RoadConditionType condition,
                                              @RequestParam(required = false) Boolean blocked) {
        return ResponseEntity.ok(roadService.updateCondition(id, condition, blocked));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
