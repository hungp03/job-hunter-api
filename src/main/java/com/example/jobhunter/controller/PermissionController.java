package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.entity.Permission;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.service.PermissionService;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission p){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission p){
        return ResponseEntity.ok(this.permissionService.update(p));
    }

    @GetMapping("permissions")
    @ApiMessage("Get permission(s)")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id){
        this.permissionService.delete(id);
        return ResponseEntity.ok(null);
    }

}
