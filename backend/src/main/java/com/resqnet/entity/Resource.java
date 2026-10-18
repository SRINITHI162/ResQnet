package com.resqnet.entity;

import com.resqnet.enums.ResourceStatus;
import com.resqnet.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    private Integer quantity;
    private Integer availableQuantity;

    private String location;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_incident_id")
    private Incident assignedIncident;

    private LocalDateTime lastUpdated;

    @PrePersist @PreUpdate
    public void touch() {
        lastUpdated = LocalDateTime.now();
        if (status == null) status = ResourceStatus.AVAILABLE;
    }
}
