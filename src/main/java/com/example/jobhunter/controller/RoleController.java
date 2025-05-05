package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.entity.Role;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.service.RoleService;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("roles")
    @ApiMessage("Create role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role r){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role r){
        return ResponseEntity.ok(this.roleService.update(r));
    }

    @GetMapping("roles")
    @ApiMessage("Get roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.getRoles(spec, pageable));
    }

    @DeleteMapping("roles/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id){
        this.roleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id){
        return ResponseEntity.ok(this.roleService.fetchById(id));
    }
}
