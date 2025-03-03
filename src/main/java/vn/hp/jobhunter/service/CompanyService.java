package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.entity.Company;
import vn.hp.jobhunter.entity.User;
import vn.hp.jobhunter.dto.response.ResultPaginationDTO;
import vn.hp.jobhunter.repository.CompanyRepository;
import vn.hp.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(companyPage.getContent());
        return rs;
    }

    public Company updateCompany(Company c) {
        return this.companyRepository.findById(c.getId()).map(e -> {
            e.setLogo(c.getLogo());
            e.setName(c.getName());
            e.setDescription(c.getDescription());
            e.setAddress(c.getAddress());
            return this.companyRepository.save(e);
        }).orElse(null);
    }

    public void deleteCompany(long companyId) {
        Optional<Company> companyOptional = this.companyRepository.findById(companyId);
        if (companyOptional.isPresent()){
            Company company = companyOptional.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(companyId);
    }

    public Optional<Company> findById(long companyId){
        return this.companyRepository.findById(companyId);
    }
}
