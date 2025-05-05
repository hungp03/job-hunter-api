package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.entity.Company;
import com.example.jobhunter.entity.Job;
import com.example.jobhunter.entity.Resume;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.dto.response.resume.ResCreateResumeDTO;
import com.example.jobhunter.dto.response.resume.ResResumeDTO;
import com.example.jobhunter.dto.response.resume.ResUpdateResumeDTO;
import com.example.jobhunter.service.ResumeService;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;


    @PostMapping("resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) {
        ResUpdateResumeDTO updated = resumeService.update(resume);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id){
        this.resumeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("resumes/{id}")
    @ApiMessage("Get a resume")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") long id) {
        ResResumeDTO resume = resumeService.getResumeById(id);
        return ResponseEntity.ok(resume);
    }


    @GetMapping("resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(@Filter Specification<Resume> specification, Pageable pageable){
        return ResponseEntity.ok().body(this.resumeService.getAllResumes(specification, pageable));

    }

    @GetMapping("resumes/me")
    @ApiMessage("Get list resume by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable){
        return ResponseEntity.ok(this.resumeService.fetchResumeByUser(pageable));
    }
}
