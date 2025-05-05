package com.example.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.jobhunter.dto.request.company.CreateCompanyRequestDTO;
import com.example.jobhunter.dto.request.company.UpdateCompanyRequestDTO;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.entity.Company;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.CompanyRepository;
import com.example.jobhunter.repository.UserRepository;
import com.example.jobhunter.util.error.IdInvalidException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Company createCompany(CreateCompanyRequestDTO dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new IdInvalidException("Tên công ty đã tồn tại");
        }
        Company company = new Company();
        updateCompanyFromDto(company, dto.getName(), dto.getLogo(), dto.getAddress(), dto.getDescription());
        return companyRepository.save(company);
    }

    public Company updateCompany(UpdateCompanyRequestDTO dto) {
        Company company = findById(dto.getId());
        updateCompanyFromDto(company, dto.getName(), dto.getLogo(), dto.getAddress(), dto.getDescription());
        return companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> page = companyRepository.findAll(spec, pageable);

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
    public void deleteCompany(long companyId) {
        Company company = findById(companyId);
        List<User> users = userRepository.findByCompany(company);
        userRepository.deleteAll(users);
        companyRepository.delete(company);
    }

    public Company findById(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy thông tin công ty với id: " + id));
    }

    // ========== Private helper ==========
    private void updateCompanyFromDto(Company company, String name, String logo, String address, String description) {
        company.setName(name);
        company.setLogo(logo);
        company.setAddress(address);
        company.setDescription(description);
    }
}
