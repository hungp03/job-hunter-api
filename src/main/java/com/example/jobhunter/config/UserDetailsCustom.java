package com.example.jobhunter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.example.jobhunter.service.UserService;

import java.util.Collections;

@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.jobhunter.entity.User user = this.userService.getUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("Tài khoản không hợp lệ");
        }
        return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}
