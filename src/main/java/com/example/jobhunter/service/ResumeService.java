package com.example.jobhunter.service;

import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.dto.response.resume.ResCreateResumeDTO;
import com.example.jobhunter.dto.response.resume.ResResumeDTO;
import com.example.jobhunter.dto.response.resume.ResUpdateResumeDTO;
import com.example.jobhunter.entity.Company;
import com.example.jobhunter.entity.Job;
import com.example.jobhunter.entity.Resume;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.JobRepository;
import com.example.jobhunter.repository.ResumeRepository;
import com.example.jobhunter.repository.UserRepository;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ResumeService {

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;


    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResCreateResumeDTO create(Resume resume) {
        if (!checkResumeExistByUserAndJob(resume)) {
            throw new IdInvalidException("User/Job_id không tồn tại ");
        }
        if (!isValidJobApplicationTime(resume.getJob().getId())) {
            throw new IdInvalidException("Ngoài thời gian ứng tuyển công việc.");
        }
        Resume curr = this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(curr.getId());
        res.setCreatedAt(curr.getCreatedAt());
        res.setCreatedBy(curr.getCreatedBy());
        return res;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // Check user by id
        if (resume.getUser() == null || !userRepository.existsById(resume.getUser().getId())) {
            return false;
        }
        // Check job by id
        return resume.getJob() != null && jobRepository.existsById(resume.getJob().getId());
    }

    public ResUpdateResumeDTO update(Resume inputResume) {
        Resume existingResume = resumeRepository.findById(inputResume.getId())
                .orElseThrow(() -> new IdInvalidException("Resume id = " + inputResume.getId() + " không tồn tại"));
        existingResume.setStatus(inputResume.getStatus());
        Resume updated = resumeRepository.save(existingResume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(updated.getUpdatedAt());
        res.setUpdatedBy(updated.getUpdatedBy());
        return res;
    }


    public boolean isResumeExisted(long id) {
        return this.resumeRepository.existsById(id);
    }

    public void delete(long id) {
        if (!isResumeExisted(id)) {
            throw new IdInvalidException("Resume id = " + id + " không tồn tại");
        }
        this.resumeRepository.deleteById(id);
    }

    public ResResumeDTO getResumeById(long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Resume id = " + id + " không tồn tại"));
        return mapToDto(resume);
    }

    private ResResumeDTO mapToDto(Resume resume) {
        ResResumeDTO res = new ResResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
            res.setJob(new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        }

        res.setUser(new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        return res;
    }


    public ResultPaginationDTO getAllResumes(Specification<Resume> specification, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs.stream().map(Job::getId)
                            .collect(Collectors.toList());
                }
            }
        }

        assert arrJobIds != null;
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(specification);
        Page<Resume> resumes = this.resumeRepository.findAll(finalSpec, pageable);
        return createResultPaginationDTO(resumes, pageable);
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> resumes = this.resumeRepository.findAll(spec, pageable);
        return createResultPaginationDTO(resumes, pageable);
    }

    private ResultPaginationDTO createResultPaginationDTO(Page<Resume> resumes, Pageable pageable) {
        // Remove sensitive data
        List<ResResumeDTO> listResume = resumes.getContent()
                .stream().map(this::mapToDto)
                .toList();

        return new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize(),
                        resumes.getTotalPages(),
                        resumes.getTotalElements()
                ), listResume
        );
    }


    private boolean isValidJobApplicationTime(long jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return false;
        }

        Job job = optionalJob.get();
        Instant now = Instant.now();

        return (job.getStartDate() == null || !now.isBefore(job.getStartDate())) &&
                (job.getEndDate() == null || !now.isAfter(job.getEndDate()));
    }
}
