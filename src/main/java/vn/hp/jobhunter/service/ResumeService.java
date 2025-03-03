package vn.hp.jobhunter.service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.entity.Resume;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.dto.response.resume.ResCreateResumeDTO;
import vn.hp.jobhunter.dto.response.resume.ResResumeDTO;
import vn.hp.jobhunter.dto.response.resume.ResUpdateResumeDTO;
import vn.hp.jobhunter.repository.JobRepository;
import vn.hp.jobhunter.repository.ResumeRepository;
import vn.hp.jobhunter.repository.UserRepository;
import vn.hp.jobhunter.util.SecurityUtil;

import java.util.List;
import java.util.Optional;



@Service
public class ResumeService {

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;


    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository, UserRepository userRepository1, JobRepository jobRepository1) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository1;
        this.jobRepository = jobRepository1;
    }

    public ResCreateResumeDTO create(Resume resume) {
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

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public boolean isResumeExisted(long id) {
        return this.resumeRepository.existsById(id);
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResResumeDTO getResume(Resume resume) {
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
        }
        res.setUser(new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO getAllResumes(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> resumes = this.resumeRepository.findAll(specification, pageable);
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
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(resumes.getTotalPages());
        meta.setTotal(resumes.getTotalElements());

        rs.setMeta(meta);

        // Remove sensitive data
        List<ResResumeDTO> listResume = resumes.getContent()
                .stream().map(this::getResume)
                .toList();

        rs.setResult(listResume);
        return rs;
    }

}
