package com.resqnet.dto;

import com.resqnet.enums.DisasterType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IncidentRequest {
    @NotNull private DisasterType disasterType;
    @NotBlank @Size(max = 255) private String title;
    @Size(max = 2000)
    private String description;
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private Double latitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private Double longitude;
    @Size(max = 255)
    private String locationName;
    @Min(1) @Max(10) private Integer severity;
    @PositiveOrZero
    private Integer affectedPeople;
    @PositiveOrZero
    private Integer injuredPeople;
    @PositiveOrZero
    private Integer vulnerablePeople;
    @Size(max = 1000)
    private String requiredResources;
    @Size(max = 255)
    private String imageUrl;
}
