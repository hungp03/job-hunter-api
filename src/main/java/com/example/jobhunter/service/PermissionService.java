package com.example.jobhunter.service;

import com.example.jobhunter.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.jobhunter.entity.Permission;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.repository.PermissionRepository;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission create(Permission p){
        if (isPermissionExisted(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return this.permissionRepository.save(p);
    }

    public boolean isPermissionExisted(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(), p.getMethod());
    }

    public Permission update(Permission p) {
        Permission permissionDB = fetchById(p.getId());
        if (permissionDB == null) {
            throw new IdInvalidException("Permission id: " + p.getId() + " không tồn tại");
        }
        if (isPermissionExisted(p) && !isSameName(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        permissionDB.setName(p.getName());
        permissionDB.setApiPath(p.getApiPath());
        permissionDB.setMethod(p.getMethod());
        permissionDB.setModule(p.getModule());

        return permissionRepository.save(permissionDB);
    }


    public Permission fetchById(long id){
        return this.permissionRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(spec, pageable);
        return new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                ), page.getContent()
        );
    }


    @Transactional
    public void delete(long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Permission id = " + id + " không tồn tại."));
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        permissionRepository.delete(permission);
    }


    public boolean isSameName(Permission p) {
        Permission pDB = this.fetchById(p.getId());
        return pDB != null && pDB.getName().equals(p.getName());
    }

}
