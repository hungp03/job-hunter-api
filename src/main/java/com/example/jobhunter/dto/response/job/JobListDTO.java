package com.example.jobhunter.dto.response.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
public class JobListDTO {
    private Long id;
    private String name;
    private String location;
    private double salary;
    private String level;
    private String companyName;
    private String companyLogo;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
