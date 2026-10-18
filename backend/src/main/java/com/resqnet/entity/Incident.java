package com.resqnet.entity;

import com.resqnet.enums.DisasterType;
import com.resqnet.enums.IncidentStatus;
import com.resqnet.enums.PriorityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Incident {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisasterType disasterType;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String locationName;

    /** 1 (minor) - 10 (catastrophic) raw severity input */
    private Integer severity;

    private Integer affectedPeople;
    private Integer injuredPeople;
    private Integer vulnerablePeople;

    @Column(length = 1000)
    private String requiredResources;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    private Double priorityScore;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @ManyToOne
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    private LocalDateTime reportedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        reportedAt = LocalDateTime.now();
        if (status == null) status = IncidentStatus.REPORTED;
    }
}
