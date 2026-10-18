package com.resqnet.repository;

import com.resqnet.entity.RescueTeam;
import com.resqnet.enums.TeamAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Long> {
    List<RescueTeam> findByAvailabilityStatus(TeamAvailability status);
    long countByAvailabilityStatus(TeamAvailability status);
}
