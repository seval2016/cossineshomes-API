package com.project.repository.user;

import com.project.repository.business.entity.concretes.user.User;
import com.project.repository.business.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameEquals(String username);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.userRole r WHERE r.roleName = ?1 ORDER BY u.firstName DESC")
    Page<User> findByUserByRole(String roleName, Pageable pageable);

    List<User> getUserByFirstNameContaining(String name);

    @Query("SELECT u FROM User u WHERE u.id IN :customerIds")
    List<User> findByIdsEquals(Long[] customerIds);

    @Query(value = "SELECT COUNT(u) FROM User u JOIN u.userRole ur WHERE ur.role = ?1")
    long countAdmin(Role role);

    Optional<User> findByEmail(String email);
}

