package com.workflowsystem.demo.auth.seeder;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.workflowsystem.demo.auth.entity.Role;
import com.workflowsystem.demo.auth.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            for (com.workflowsystem.demo.auth.enums.Role role : com.workflowsystem.demo.auth.enums.Role.values()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }
        System.out.println("Roles seeded successfully");
    }
    
}
