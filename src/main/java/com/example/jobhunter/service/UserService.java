package com.example.jobhunter.service;

import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.dto.response.user.ResCreateUserDTO;
import com.example.jobhunter.dto.response.user.ResUpdateUserDTO;
import com.example.jobhunter.dto.response.user.ResUserDTO;
import com.example.jobhunter.entity.Role;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.UserRepository;
import com.example.jobhunter.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final RoleService roleService;

    public ResCreateUserDTO handleCreateUser(User user) {
        if (isExistedEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCompany(fetchCompany(user));
        user.setRole(fetchRole(user));
        return toResCreateDTO(userRepository.save(user));
    }

    public void deleteUser(long id) {
        User currentUser = getUserById(id);
        userRepository.deleteById(currentUser.getId());
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Người dùng với id " + id + " không tồn tại"));
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize(),
                        userPage.getTotalPages(),
                        userPage.getTotalElements()
                ),
                userPage.getContent().stream().map(this::toResUserDTO).toList()
        );
    }

    public ResUpdateUserDTO updateUser(User request) {
        User currentUser = getUserById(request.getId());
        currentUser.setName(request.getName());
        currentUser.setAge(request.getAge());
        currentUser.setGender(request.getGender());
        currentUser.setAddress(request.getAddress());
        currentUser.setCompany(fetchCompany(request));
        currentUser.setRole(fetchRole(request));
        return toResUpdateDTO(userRepository.save(currentUser));
    }

    public User getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByEmail(username))
                .orElseThrow(() -> new IdInvalidException("Người dùng " + username + " không tồn tại"));
    }

    public boolean isExistedEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    private Role fetchRole(User user) {
        return user.getRole() != null ? roleService.fetchById(user.getRole().getId()) : null;
    }

    private com.example.jobhunter.entity.Company fetchCompany(User user) {
        return user.getCompany() != null ? companyService.findById(user.getCompany().getId()) : null;
    }

    private ResCreateUserDTO toResCreateDTO(User user) {
        return new ResCreateUserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGender(),
                user.getAddress(),
                user.getAge(),
                user.getCreatedAt(),
                user.getCompany() != null ?
                        new ResCreateUserDTO.CompanyUser(user.getCompany().getId(), user.getCompany().getName()) : null
        );
    }

    public ResUserDTO toResUserDTO(User user) {
        return new ResUserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGender(),
                user.getAddress(),
                user.getAge(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCompany() != null ?
                        new ResUserDTO.CompanyUser(user.getCompany().getId(), user.getCompany().getName()) : null,
                user.getRole() != null ?
                        new ResUserDTO.RoleUser(user.getRole().getId(), user.getRole().getName()) : null
        );
    }

    private ResUpdateUserDTO toResUpdateDTO(User user) {
        return new ResUpdateUserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGender(),
                user.getAddress(),
                user.getAge(),
                user.getUpdatedAt(),
                user.getCompany() != null ?
                        new ResUpdateUserDTO.CompanyUser(user.getCompany().getId(), user.getCompany().getName()) : null
        );
    }
}
