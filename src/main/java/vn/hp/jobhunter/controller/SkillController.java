package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.Skill;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.service.SkillService;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("skills")
    @ApiMessage("Create skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if (this.skillService.isExistedSkillName(skill.getName())) {
            throw new IdInvalidException("Skill " + skill.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skill));
    }

    @PutMapping("skills")
    @ApiMessage("Update Skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.getSkillById(skill.getId());
        if (currentSkill == null){
            throw new IdInvalidException("Skill id = " + skill.getId() + " không tồn tại");
        }
        if (this.skillService.isExistedSkillName(skill.getName())) {
            throw new IdInvalidException("Skill " + skill.getName() + " đã tồn tại");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.ok(this.skillService.updateSkill(currentSkill));
    }

    @GetMapping("skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkill(@Filter Specification<Skill> spec, Pageable pageable){
        return ResponseEntity.ok(this.skillService.fetchAllSkills(spec,pageable));
    }

    @DeleteMapping("skills/{id}")
    @ApiMessage("Delete Skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException{
        Skill currSkill = this.skillService.getSkillById(id);
        if (currSkill == null){
            throw new IdInvalidException("Skill id = " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }
}
