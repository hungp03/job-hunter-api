package com.example.jobhunter.dto.request.job;

import lombok.Getter;
import lombok.Setter;
import com.example.jobhunter.util.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public abstract class BaseJobRequestDTO {
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private List<Long> skills;
    private Long companyId;
}
