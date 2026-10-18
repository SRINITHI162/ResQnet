package com.resqnet.controller;

import com.resqnet.entity.RescueTeam;
import com.resqnet.service.RescueTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rescue-teams")
@RequiredArgsConstructor
public class RescueTeamController {

    private final RescueTeamService rescueTeamService;

    @GetMapping
    public ResponseEntity<List<RescueTeam>> getAll() {
        return ResponseEntity.ok(rescueTeamService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RescueTeam> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rescueTeamService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RescueTeam> create(@RequestBody RescueTeam team) {
        return ResponseEntity.ok(rescueTeamService.create(team));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RescueTeam> update(@PathVariable Long id, @RequestBody RescueTeam team) {
        return ResponseEntity.ok(rescueTeamService.update(id, team));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rescueTeamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
