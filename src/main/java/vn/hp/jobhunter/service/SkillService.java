package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hp.jobhunter.entity.Skill;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean isExistedSkillName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill getSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        return skillOptional.orElse(null);
    }

    public Skill updateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(skillPage.getTotalPages());
        meta.setTotal(skillPage.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(skillPage.getContent());
        return rs;
    }

    @Transactional
    public void deleteSkill(long id){
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currSkill = skillOptional.get();
        //Delete data in job_skill table
        currSkill.getJobs().forEach(job -> job.getSkills().remove(currSkill));
        //Delete data in subscriber_skill table
        currSkill.getSubscribers().forEach(skill -> skill.getSkills().remove(currSkill));
        this.skillRepository.delete(currSkill);
    }
}
