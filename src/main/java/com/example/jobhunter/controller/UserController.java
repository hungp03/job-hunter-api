package com.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.dto.response.user.ResCreateUserDTO;
import com.example.jobhunter.dto.response.user.ResUpdateUserDTO;
import com.example.jobhunter.dto.response.user.ResUserDTO;
import com.example.jobhunter.dto.response.ResultPaginationDTO;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(user));
    }

    @DeleteMapping("users/{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id){
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") long id){
        User user = this.userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.toResUserDTO(user));
    }

    @GetMapping("users")
    @ApiMessage("Fetch users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("users")
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user){
        return ResponseEntity.ok(this.userService.updateUser(user));
    }
}
