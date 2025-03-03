package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hp.jobhunter.entity.Permission;
import vn.hp.jobhunter.entity.Role;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.repository.PermissionRepository;
import vn.hp.jobhunter.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existedName(String name){
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role r){
        if (r.getPermissions() != null){
            List<Long> reqPermissions = r.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(r);
    }

    public Role fetchById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }


    public Role update(Role r){
        Role roleDB = this.fetchById(r.getId());
        // check permissions
        if (r.getPermissions() != null){
            List<Long> reqPermissions = r.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        roleDB.setPermissions(r.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable){
        Page<Role> rolePage = this.roleRepository.findAll(spec,pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(rolePage.getTotalPages());
        meta.setTotal(rolePage.getTotalElements());

        res.setMeta(meta);
        res.setResult(rolePage.getContent());
        return res;
    }

    public void delete(long id){
        this.roleRepository.deleteById(id);
    }

    public boolean existsById(long id){
        return this.roleRepository.existsById(id);
    }
}
