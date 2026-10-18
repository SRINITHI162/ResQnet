package com.resqnet.entity;

import com.resqnet.enums.TeamAvailability;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rescue_teams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RescueTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamName;

    private String specialization;
    private Integer membersCount;

    private String location;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private TeamAvailability availabilityStatus;

    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "assigned_incident_id")
    private Incident assignedIncident;
}
