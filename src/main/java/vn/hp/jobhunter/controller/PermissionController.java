package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.Permission;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.service.PermissionService;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission p) throws IdInvalidException {
        if (this.permissionService.isPermissionExisted(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission p) throws IdInvalidException {
        if (this.permissionService.existedId(p.getId())) {
            throw new IdInvalidException("Permission id: " + p.getId() + " không tồn tại");
        }

        if (this.permissionService.isPermissionExisted(p)) {
            if (this.permissionService.isSameName(p))
                throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.ok(this.permissionService.update(p));
    }

    @GetMapping("permissions")
    @ApiMessage("Get permission(s)")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.existedId(id)) {
            throw new IdInvalidException("Permission id = " + id + " không tồn tại.");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok(null);
    }

}
