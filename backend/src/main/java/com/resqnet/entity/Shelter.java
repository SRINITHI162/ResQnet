package com.resqnet.entity;

import com.resqnet.enums.ShelterStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shelters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shelter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private Double latitude;
    private Double longitude;

    private Integer totalCapacity;
    private Integer occupiedCapacity;

    @Builder.Default
    private Boolean foodAvailable = true;
    @Builder.Default
    private Boolean waterAvailable = true;
    @Builder.Default
    private Boolean medicalSupport = false;
    @Builder.Default
    private Boolean accessibilitySupport = false;

    @Enumerated(EnumType.STRING)
    private ShelterStatus status;

    public Integer getAvailableCapacity() {
        if (totalCapacity == null) return 0;
        int occ = occupiedCapacity == null ? 0 : occupiedCapacity;
        return totalCapacity - occ;
    }
}
