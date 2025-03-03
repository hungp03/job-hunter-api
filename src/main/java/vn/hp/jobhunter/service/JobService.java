package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.entity.Company;
import vn.hp.jobhunter.entity.Job;
import vn.hp.jobhunter.entity.Skill;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.dto.response.job.ResCreateJobDTO;
import vn.hp.jobhunter.dto.response.job.ResUpdateJobDTO;
import vn.hp.jobhunter.repository.CompanyRepository;
import vn.hp.jobhunter.repository.JobRepository;
import vn.hp.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO create(Job job){
        if (job.getSkills() != null){
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        // check company
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            cOptional.ifPresent(job::setCompany);
        }

        Job curr = this.jobRepository.save(job);
        // Convert response
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(curr.getId());
        res.setName(curr.getName());
        res.setLocation(curr.getLocation());
        res.setSalary(curr.getSalary());
        res.setLevel(curr.getLevel());
        res.setStartDate(curr.getStartDate());
        res.setEndDate (curr.getEndDate());
        res.setActive (curr.isActive());
        res.setCreatedAt (curr.getCreatedAt());
        res.setCreatedBy (curr.getCreatedBy());
        if (curr.getSkills() != null) {
            List<String> skills = curr.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            res.setSkills (skills);
        }
        return res;
    }

    public ResUpdateJobDTO update(Job j, Job jobInDB) {

        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(Skill::getId)
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            cOptional.ifPresent(jobInDB::setCompany);
        }

        // update correct info
        jobInDB.setName(j.getName());
        jobInDB.setSalary(j.getSalary());
        jobInDB.setQuantity(j.getQuantity());
        jobInDB.setLocation(j.getLocation());
        jobInDB.setLevel(j.getLevel());
        jobInDB.setStartDate(j.getStartDate());
        jobInDB.setEndDate(j.getEndDate());
        jobInDB.setActive(j.isActive());

        // update job
        Job currentJob = this.jobRepository.save(jobInDB);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public boolean jobExisted(long id){
        return this.jobRepository.existsById(id);
    }

    public void delete(long id){
        this.jobRepository.deleteById(id);
    }

    public Optional<Job> getAJob(long id){
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> specification, Pageable pageable){
        Page<Job> jobPage = this.jobRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(jobPage.getContent());
        return rs;
    }

    public Optional<Job> findById(long id) {
        return this.jobRepository.findById(id);
    }
}
