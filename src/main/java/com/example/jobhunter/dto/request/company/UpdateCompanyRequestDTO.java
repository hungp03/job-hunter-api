package com.example.jobhunter.dto.request.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCompanyRequestDTO {
    private long id;
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;
    private String description;
    private String address;
    private String logo;
}

