package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hp.jobhunter.entity.Permission;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission create(Permission p){
        return this.permissionRepository.save(p);
    }

    public boolean isPermissionExisted(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(), p.getMethod());
    }

    public Permission update(Permission p){
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null){
            permissionDB.setName(p.getName());
            permissionDB.setApiPath(p.getApiPath());
            permissionDB.setMethod(p.getMethod());
            permissionDB.setModule(p.getModule());

            // update
            permissionDB = this.permissionRepository.save(permissionDB);
            return permissionDB;
        }

        return null;
    }

    public Permission fetchById(long id){
        return this.permissionRepository.findById(id).orElse(null);
    }
    public boolean existedId(long id){
        return !this.permissionRepository.existsById(id);
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable){
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec,pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(permissionPage.getContent());
        return res;
    }

    @Transactional
    public void delete(long id){
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            Permission curr = permissionOptional.get();
            curr.getRoles().forEach(job -> job.getPermissions().remove(curr));
            this.permissionRepository.delete(curr);
        }
    }

    public boolean isSameName(Permission p) {
        Permission pDB = this.fetchById(p.getId());
        return pDB != null && pDB.getName().equals(p.getName());
    }

}
