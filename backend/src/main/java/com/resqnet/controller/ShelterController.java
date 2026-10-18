package com.resqnet.controller;

import com.resqnet.entity.Shelter;
import com.resqnet.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
public class ShelterController {

    private final ShelterService shelterService;

    @GetMapping
    public ResponseEntity<List<Shelter>> getAll() {
        return ResponseEntity.ok(shelterService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shelter> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.getById(id));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<Shelter>> recommend(@RequestParam double lat, @RequestParam double lng,
                                                     @RequestParam(defaultValue = "1") int minCapacity) {
        return ResponseEntity.ok(shelterService.recommend(lat, lng, minCapacity));
    }

    @PostMapping
    public ResponseEntity<Shelter> create(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.create(shelter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shelter> update(@PathVariable Long id, @RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.update(id, shelter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shelterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
