package com.project.security.service;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameEquals(username);
        if (user != null) {
            Set<GrantedAuthority> authorities = user.getUserRole().stream()
                    .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName())) // RoleType'dan rol ismini alıyoruz
                    .collect(Collectors.toSet());

            return new UserDetailsImpl(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getPasswordHash(),
                    authorities
            );
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}