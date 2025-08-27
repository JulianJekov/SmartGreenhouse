package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.GreenhouseUserDetails;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.enums.Role;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::maToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private UserDetails maToUserDetails(User user) {
        return new GreenhouseUserDetails(
                user.getEmail(),
                user.getPassword(),
                mapRoles(user.getRole()),
                user.getId(),
                user.getName(),
                user.getRole()
        );
    }

    private Collection<? extends GrantedAuthority> mapRoles(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
