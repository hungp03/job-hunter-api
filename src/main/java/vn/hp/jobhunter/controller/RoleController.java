package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.Role;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.service.RoleService;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("roles")
    @ApiMessage("Create role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role r) throws IdInvalidException {
        if (this.roleService.existedName(r.getName())) {
            throw new IdInvalidException("Role " + r.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role r) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException("Role id = " + r.getId() + " không tồn tại");
        }

        // check name
        /*if (this.roleService.existedName(r.getName())) {
            throw new IdInvalidException("Role " + r.getName() + " đã tồn tại");
        }*/

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
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        if (!this.roleService.existsById(id)) {
            throw new IdInvalidException("Role id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Role r = this.roleService.fetchById(id);
        if (r == null) {
            throw new IdInvalidException("Role id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(r);
    }
}
