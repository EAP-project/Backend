package com.automobileproject.EAP.service;

import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        // ADD THESE CHECKS
        if (!user.getEmailVerified()) {
            log.warn("User email not verified: {}", email);
            throw new RuntimeException("Email not verified. Please verify your email before logging in.");
        }

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", email);
            throw new RuntimeException("Account is disabled");
        }
//

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .accountExpired(false)      // ⭐ ADD THIS
                .accountLocked(false)       // ⭐ ADD THIS
                .credentialsExpired(false)  // ⭐ ADD THIS
                .disabled(!user.getEnabled()) // ⭐ ADD THIS

                .build();
    }
}