package com.workflowsystem.demo.auth.seeder;


import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.workflowsystem.demo.auth.entity.Role;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.RoleRepository;
import com.workflowsystem.demo.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private Role savedRole = new Role();

    @Value("${app.seed-data:false}")
    private boolean seedData;
    
    @Override
    public void run(String... args) throws Exception {
        if(!seedData)
            return;

        if (roleRepository.count() == 0) {
            for (com.workflowsystem.demo.auth.enums.Role role : com.workflowsystem.demo.auth.enums.Role.values()) {
                Role newRole = new Role();
                newRole.setName(role);
                savedRole =  roleRepository.save(newRole);
               
                String name = role.toString().substring(5).toLowerCase();
                User user = new User();
                user.setName(name);
                user.setEmail(name+"@workflow.local");
                user.setPassword(passwordEncoder.encode("Pass123"));
                user.setEnabled(true);
                user.setRoles(Set.of(savedRole));

                userRepository.save(user);
            }
        }




    }
    
}
