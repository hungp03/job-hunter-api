package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.Job;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.dto.response.job.ResCreateJobDTO;
import vn.hp.jobhunter.dto.response.job.ResUpdateJobDTO;
import vn.hp.jobhunter.service.JobService;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("jobs")
    @ApiMessage("Create job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job j){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(j));
    }

    @PutMapping("jobs")
    @ApiMessage("Update job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.findById(job.getId());
        if (currentJob.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok()
                .body(this.jobService.update(job, currentJob.get()));
    }


    @DeleteMapping("jobs/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
        boolean isJobExisted = this.jobService.jobExisted(id);
        if (!isJobExisted){
            throw new IdInvalidException("Job id = " + id + " không tồn tại");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("jobs/{id}")
    @ApiMessage("Get a job")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        boolean isJobExisted = this.jobService.jobExisted(id);
        if (!isJobExisted) {
            throw new IdInvalidException("Job id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.jobService.getAJob(id).get());
    }

    @GetMapping("jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> specification, Pageable pageable){
        return ResponseEntity.ok(this.jobService.getAllJobs(specification,pageable));
    }
}
