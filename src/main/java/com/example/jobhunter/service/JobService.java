package com.example.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.jobhunter.dto.request.job.BaseJobRequestDTO;
import com.example.jobhunter.dto.request.job.CreateJobRequestDTO;
import com.example.jobhunter.dto.request.job.UpdateJobRequestDTO;
import com.example.jobhunter.entity.Job;
import com.example.jobhunter.entity.Skill;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.dto.response.job.ResCreateJobDTO;
import com.example.jobhunter.dto.response.job.ResUpdateJobDTO;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.CompanyRepository;
import com.example.jobhunter.repository.JobRepository;
import com.example.jobhunter.repository.SkillRepository;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final CompanyService companyService;
    public ResCreateJobDTO create(CreateJobRequestDTO createJobDTO) {
        Job job = new Job();
        mapJobFields(job, createJobDTO);
        return toResCreateJobDTO(jobRepository.save(job));
    }

    public ResUpdateJobDTO update(UpdateJobRequestDTO jobRequestDTO) {
        Job job = findById(jobRequestDTO.getId());
        mapJobFields(job, jobRequestDTO);
        return toResUpdateJobDTO(jobRepository.save(job));
    }

    public boolean jobExisted(long id) {
        return jobRepository.existsById(id);
    }

    public void delete(long id) {
        if (!jobExisted(id)){
            throw new IdInvalidException("Job id = " + id + " không tồn tại");
        }
        jobRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> specification, Pageable pageable) {
        Page<Job> jobPage = jobRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(jobPage.getContent());
        return resultPaginationDTO;
    }

    public Job findById(long id) {
        return jobRepository.findById(id).orElseThrow(() -> new IdInvalidException("Job id = " + id + " không tồn tại"));
    }

    // Helper methods to convert entities to DTOs
    private ResCreateJobDTO toResCreateJobDTO(Job job) {
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setLevel(job.getLevel());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());
        res.setCreatedAt(job.getCreatedAt());
        res.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            res.setSkills(skills);
        }

        return res;
    }

    private ResUpdateJobDTO toResUpdateJobDTO(Job job) {
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setUpdatedBy(job.getUpdatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    private void mapJobFields(Job job, BaseJobRequestDTO dto) {
        job.setName(dto.getName());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setQuantity(dto.getQuantity());
        job.setLevel(dto.getLevel());
        job.setDescription(dto.getDescription());
        job.setStartDate(dto.getStartDate());
        job.setEndDate(dto.getEndDate());
        job.setActive(dto.isActive());

        if (dto.getSkills() != null) {
            List<Skill> dbSkills = skillRepository.findByIdIn(dto.getSkills());
            job.setSkills(dbSkills);
        }

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User u = userService.getUserByUsername(email);
        if (u.getCompany() != null) {
            job.setCompany(u.getCompany());
        } else if (dto.getCompanyId() != null) {
            job.setCompany(companyService.findById(dto.getCompanyId()));
        } else {
            throw new IdInvalidException("User không có company hoặc company không hợp lệ");
        }
    }
}
