package com.resqnet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(max = 255) private String name;
    @Email @NotBlank @Size(max = 255) private String email;
    @Size(max = 255)
    private String phone;
    @NotBlank @Size(min = 8, max = 72) private String password;
    @Size(max = 255)
    private String location;
    private Double latitude;
    private Double longitude;
}
