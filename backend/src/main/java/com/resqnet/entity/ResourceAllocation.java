package com.resqnet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource_allocations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    private Double allocationScore;
    private Double distanceKm;
    private Double estimatedTravelTimeMin;

    private LocalDateTime allocatedAt;

    @PrePersist
    public void prePersist() {
        allocatedAt = LocalDateTime.now();
    }
}
