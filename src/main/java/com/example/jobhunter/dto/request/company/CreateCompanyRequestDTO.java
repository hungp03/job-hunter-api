package com.example.jobhunter.dto.request.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCompanyRequestDTO {
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;
    private String description;
    private String address;
    private String logo;
}
