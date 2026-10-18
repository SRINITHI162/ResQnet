package com.resqnet.service;

import com.resqnet.enums.IncidentStatus;
import com.resqnet.enums.PriorityLevel;
import com.resqnet.enums.ResourceStatus;
import com.resqnet.enums.TeamAvailability;
import com.resqnet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncidentRepository incidentRepository;
    private final ResourceRepository resourceRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final ShelterRepository shelterRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Map<String, Object> incidentStats = new HashMap<>();
        incidentStats.put("total", incidentRepository.count());
        incidentStats.put("critical", incidentRepository.countByPriorityLevel(PriorityLevel.CRITICAL));
        incidentStats.put("high", incidentRepository.countByPriorityLevel(PriorityLevel.HIGH));
        incidentStats.put("resolved", incidentRepository.countByStatus(IncidentStatus.RESOLVED));
        incidentStats.put("active", incidentRepository.countByStatusNotIn(
                List.of(IncidentStatus.RESOLVED, IncidentStatus.REJECTED)));
        stats.put("incidents", incidentStats);

        Map<String, Object> resourceStats = new HashMap<>();
        resourceStats.put("total", resourceRepository.count());
        resourceStats.put("available", resourceRepository.countByStatus(ResourceStatus.AVAILABLE));
        resourceStats.put("assigned", resourceRepository.countByStatus(ResourceStatus.ASSIGNED));
        resourceStats.put("inUse", resourceRepository.countByStatus(ResourceStatus.IN_USE));
        resourceStats.put("unavailable", resourceRepository.countByStatus(ResourceStatus.UNAVAILABLE));
        stats.put("resources", resourceStats);

        Map<String, Object> teamStats = new HashMap<>();
        teamStats.put("available", rescueTeamRepository.countByAvailabilityStatus(TeamAvailability.AVAILABLE));
        teamStats.put("assigned", rescueTeamRepository.countByAvailabilityStatus(TeamAvailability.ASSIGNED));
        teamStats.put("onMission", rescueTeamRepository.countByAvailabilityStatus(TeamAvailability.ON_MISSION));
        stats.put("rescueTeams", teamStats);

        Map<String, Object> shelterStats = new HashMap<>();
        shelterStats.put("total", shelterRepository.count());
        long totalCapacity = shelterRepository.sumTotalCapacity();
        long occupied = shelterRepository.sumOccupiedCapacity();
        shelterStats.put("totalCapacity", totalCapacity);
        shelterStats.put("occupiedCapacity", occupied);
        shelterStats.put("availableCapacity", totalCapacity - occupied);
        stats.put("shelters", shelterStats);

        return stats;
    }
}
