package com.example.jobhunter.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jobhunter.entity.Role;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.PermissionRepository;
import com.example.jobhunter.repository.RoleRepository;
import com.example.jobhunter.repository.UserRepository;
import com.example.jobhunter.entity.Permission;
import com.example.jobhunter.util.constant.GenderEnum;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("-----START INIT DATABASE-----");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/api/companies", "POST", "COMPANIES"));
            arr.add(new Permission("Update a company", "/api/companies", "PUT", "COMPANIES"));
            arr.add(new Permission("Delete a company", "/api/companies/{id}", "DELETE", "COMPANIES"));
            arr.add(new Permission("Get a company by id", "/api/companies/{id}", "GET", "COMPANIES"));
            arr.add(new Permission("Get companies with pagination", "/api/companies", "GET", "COMPANIES"));

            arr.add(new Permission("Create a job", "/api/jobs", "POST", "JOBS"));
            arr.add(new Permission("Update a job", "/api/jobs", "PUT", "JOBS"));
            arr.add(new Permission("Delete a job", "/api/jobs/{id}", "DELETE", "JOBS"));
            arr.add(new Permission("Get a job by id", "/api/jobs/{id}", "GET", "JOBS"));
            arr.add(new Permission("Get jobs with pagination", "/api/jobs", "GET", "JOBS"));

            arr.add(new Permission("Create a permission", "/api/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a resume", "/api/resumes", "POST", "RESUMES"));
            arr.add(new Permission("Update a resume", "/api/resumes", "PUT", "RESUMES"));
            arr.add(new Permission("Delete a resume", "/api/resumes/{id}", "DELETE", "RESUMES"));
            arr.add(new Permission("Get a resume by id", "/api/resumes/{id}", "GET", "RESUMES"));
            arr.add(new Permission("Get resumes with pagination", "/api/resumes", "GET", "RESUMES"));

            arr.add(new Permission("Create a role", "/api/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/users", "GET", "USERS"));

            arr.add(new Permission("Create a subscriber", "/api/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/api/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/api/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/api/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Get subscribers with pagination", "/api/subscribers", "GET", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/api/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println("-----SKIP INIT DATABASE ~ ALREADY HAVE DATA-----");
        } else
            System.out.println("-----END INIT DATABASE-----");
    }
}
