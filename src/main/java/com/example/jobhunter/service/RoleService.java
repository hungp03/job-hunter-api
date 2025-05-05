package com.example.jobhunter.service;

import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.entity.Permission;
import com.example.jobhunter.entity.Role;
import com.example.jobhunter.repository.PermissionRepository;
import com.example.jobhunter.repository.RoleRepository;
import com.example.jobhunter.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role create(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IdInvalidException("Role " + role.getName() + " đã tồn tại");
        }
        role.setPermissions(fetchPermissions(role));
        return roleRepository.save(role);
    }

    public Role fetchById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role không tồn tại"));
    }

    public Role update(Role updatedRole) {
        Role existingRole = fetchById(updatedRole.getId());

        existingRole.setName(updatedRole.getName());
        existingRole.setDescription(updatedRole.getDescription());
        existingRole.setActive(updatedRole.isActive());
        existingRole.setPermissions(fetchPermissions(updatedRole));

        return roleRepository.save(existingRole);
    }

    public void delete(long id) {
        if (!roleRepository.existsById(id)) {
            throw new IdInvalidException("Role id = " + id + " không tồn tại");
        }
        roleRepository.deleteById(id);
    }

    public boolean existsById(long id) {
        return roleRepository.existsById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(spec, pageable);

        return new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize(),
                        rolePage.getTotalPages(),
                        rolePage.getTotalElements()
                ), rolePage.getContent()
        );
    }

    private List<Permission> fetchPermissions(Role role) {
        if (role.getPermissions() == null) return null;
        List<Long> ids = role.getPermissions().stream()
                .map(Permission::getId)
                .toList();
        return permissionRepository.findByIdIn(ids);
    }
}
