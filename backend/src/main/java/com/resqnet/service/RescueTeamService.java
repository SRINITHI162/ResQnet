package com.resqnet.service;

import com.resqnet.entity.RescueTeam;
import com.resqnet.exception.ResourceNotFoundException;
import com.resqnet.repository.RescueTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RescueTeamService {

    private final RescueTeamRepository rescueTeamRepository;

    public RescueTeam create(RescueTeam team) {
        return rescueTeamRepository.save(team);
    }

    public List<RescueTeam> getAll() {
        return rescueTeamRepository.findAll();
    }

    public RescueTeam getById(Long id) {
        return rescueTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rescue team not found: " + id));
    }

    public RescueTeam update(Long id, RescueTeam updated) {
        RescueTeam existing = getById(id);
        existing.setTeamName(updated.getTeamName());
        existing.setSpecialization(updated.getSpecialization());
        existing.setMembersCount(updated.getMembersCount());
        existing.setLocation(updated.getLocation());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setAvailabilityStatus(updated.getAvailabilityStatus());
        existing.setContactNumber(updated.getContactNumber());
        return rescueTeamRepository.save(existing);
    }

    public void delete(Long id) {
        rescueTeamRepository.deleteById(id);
    }
}
