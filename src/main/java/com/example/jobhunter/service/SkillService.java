package com.example.jobhunter.service;

import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.entity.Skill;
import com.example.jobhunter.repository.SkillRepository;
import com.example.jobhunter.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public Skill createSkill(Skill skill) {
        validateNameUniqueness(skill.getName());
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) {
        Skill existing = getSkillByIdOrThrow(skill.getId());
        if (!existing.getName().equals(skill.getName())) {
            validateNameUniqueness(skill.getName());
        }
        existing.setName(skill.getName());
        return skillRepository.save(existing);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> page = skillRepository.findAll(specification, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );

        return new ResultPaginationDTO(meta, page.getContent());
    }

    @Transactional
    public void deleteSkill(long id) {
        Skill skill = getSkillByIdOrThrow(id);

        // Remove from job_skill
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));
        // Remove from subscriber_skill
        skill.getSubscribers().forEach(sub -> sub.getSkills().remove(skill));

        skillRepository.delete(skill);
    }

    private Skill getSkillByIdOrThrow(long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Skill id = " + id + " không tồn tại"));
    }

    private void validateNameUniqueness(String name) {
        if (skillRepository.existsByName(name)) {
            throw new IdInvalidException("Skill " + name + " đã tồn tại");
        }
    }
}
