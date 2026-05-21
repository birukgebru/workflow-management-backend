package com.workflowsystem.demo.auth.security;


import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService (UserRepository usRepository){
        this.userRepository = usRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
            )
        );
    }
}
