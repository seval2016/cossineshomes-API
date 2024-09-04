package com.project.repository.user;

import com.project.repository.business.entity.concretes.user.UserRole;
import com.project.repository.business.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    @Query("SELECT r FROM UserRole r WHERE r.role = ?1")
    Optional<UserRole> findByEnumRoleEquals(Role role);

}