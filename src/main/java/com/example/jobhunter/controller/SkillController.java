package com.example.jobhunter.controller;

import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.entity.Skill;
import com.example.jobhunter.service.SkillService;
import com.example.jobhunter.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("skills")
    @ApiMessage("Create skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(skill));
    }

    @PutMapping("skills")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.updateSkill(skill));
    }

    @GetMapping("skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok(skillService.fetchAllSkills(spec, pageable));
    }

    @DeleteMapping("skills/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok().build();
    }
}
