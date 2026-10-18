package com.resqnet.entity;

import com.resqnet.enums.RoadConditionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Road {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;
    private Double sourceLat;
    private Double sourceLng;

    @Column(nullable = false)
    private String destination;
    private Double destLat;
    private Double destLng;

    private Double distanceKm;
    private Double estimatedTimeMin;

    /** 1 (free flowing) - 5 (gridlock) */
    private Integer trafficLevel;

    @Enumerated(EnumType.STRING)
    private RoadConditionType roadCondition;

    /** 0 (dry) - 5 (impassable) */
    private Integer floodLevel;

    @Builder.Default
    private Boolean blocked = false;
}
