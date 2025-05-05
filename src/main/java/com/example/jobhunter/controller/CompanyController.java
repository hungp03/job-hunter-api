package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.dto.request.company.CreateCompanyRequestDTO;
import com.example.jobhunter.dto.request.company.UpdateCompanyRequestDTO;
import com.example.jobhunter.entity.Company;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.service.CompanyService;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.fetchAllCompanies(spec, pageable));
    }

    @GetMapping("companies/{id}")
    @ApiMessage("Get company by id")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long id){
        return ResponseEntity.ok(this.companyService.findById(id));
    }

    @PostMapping("companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CreateCompanyRequestDTO company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.createCompany(company));
    }

    @PutMapping("companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody UpdateCompanyRequestDTO company) {
        return ResponseEntity.ok(this.companyService.updateCompany(company));
    }

    @DeleteMapping("companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

}
