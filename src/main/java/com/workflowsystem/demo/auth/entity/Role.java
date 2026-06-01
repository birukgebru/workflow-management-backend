package com.workflowsystem.demo.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private com.workflowsystem.demo.auth.enums.Role name = com.workflowsystem.demo.auth.enums.Role.ROLE_REQUESTER;

    public Role() {}

    public Role(com.workflowsystem.demo.auth.enums.Role name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public com.workflowsystem.demo.auth.enums.Role getName() {
        return name;
    }

    public void setName(com.workflowsystem.demo.auth.enums.Role name) {
        this.name = name;
    }
}
