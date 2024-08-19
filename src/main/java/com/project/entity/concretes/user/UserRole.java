package com.project.entity.concretes.user;


import com.project.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 30)
    private String roleName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

}
