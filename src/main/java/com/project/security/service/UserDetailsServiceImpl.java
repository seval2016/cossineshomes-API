package com.project.security.service;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.USER_IS_NOT_FOUND_BY_EMAIL, email)));

        if (user != null) {
            // GrantedAuthority Set'i oluşturma
            Set<GrantedAuthority> authorities = user.getUserRole().stream()
                    .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().name())) // Enum rol adını alıyoruz
                    .collect(Collectors.toSet());

            return new UserDetailsImpl(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.isBuiltIn(),
                    user.getPasswordHash(),
                    authorities,
                    user.getPhone()
            );
        }

        throw new UsernameNotFoundException("User '" + email + "' not found");
    }
}