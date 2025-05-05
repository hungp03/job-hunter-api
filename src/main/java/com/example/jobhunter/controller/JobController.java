package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.dto.request.job.CreateJobRequestDTO;
import com.example.jobhunter.dto.request.job.UpdateJobRequestDTO;
import com.example.jobhunter.entity.Job;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.dto.response.job.ResCreateJobDTO;
import com.example.jobhunter.dto.response.job.ResUpdateJobDTO;
import com.example.jobhunter.service.JobService;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("jobs")
    @ApiMessage("Create job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody CreateJobRequestDTO job){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("jobs")
    @ApiMessage("Update job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody UpdateJobRequestDTO job){
        return ResponseEntity.ok().body(this.jobService.update(job));
    }


    @DeleteMapping("jobs/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id){
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("jobs/{id}")
    @ApiMessage("Get a job")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id){
        return ResponseEntity.ok(this.jobService.findById(id));
    }

    @GetMapping("jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> specification, Pageable pageable){
        return ResponseEntity.ok(this.jobService.getAllJobs(specification,pageable));
    }
}
