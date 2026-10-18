package com.resqnet.service;

import com.resqnet.entity.Shelter;
import com.resqnet.enums.ShelterStatus;
import com.resqnet.exception.ResourceNotFoundException;
import com.resqnet.repository.ShelterRepository;
import com.resqnet.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public Shelter create(Shelter shelter) {
        if (shelter.getStatus() == null) shelter.setStatus(ShelterStatus.OPEN);
        return shelterRepository.save(shelter);
    }

    public List<Shelter> getAll() {
        return shelterRepository.findAll();
    }

    public Shelter getById(Long id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + id));
    }

    public Shelter update(Long id, Shelter updated) {
        Shelter existing = getById(id);
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setTotalCapacity(updated.getTotalCapacity());
        existing.setOccupiedCapacity(updated.getOccupiedCapacity());
        existing.setFoodAvailable(updated.getFoodAvailable());
        existing.setWaterAvailable(updated.getWaterAvailable());
        existing.setMedicalSupport(updated.getMedicalSupport());
        existing.setAccessibilitySupport(updated.getAccessibilitySupport());
        existing.setStatus(updated.getStatus());
        return shelterRepository.save(existing);
    }

    /** Recommend nearest suitable shelters with available capacity, ranked by distance. */
    public List<Shelter> recommend(double lat, double lng, int minCapacityNeeded) {
        return shelterRepository.findAll().stream()
                .filter(s -> s.getStatus() != ShelterStatus.CLOSED)
                .filter(s -> s.getAvailableCapacity() >= minCapacityNeeded)
                .sorted(Comparator.comparingDouble(s ->
                        HaversineUtil.distanceKm(lat, lng, s.getLatitude(), s.getLongitude())))
                .toList();
    }

    public void delete(Long id) {
        shelterRepository.deleteById(id);
    }
}
