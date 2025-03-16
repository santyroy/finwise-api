package com.roy.finwise.security.service;

import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.security.model.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(username);
        User user = userOpt.orElseThrow(() -> new NotFoundException("User with email: " + username + " not found"));
        return new AppUserDetails(user);
    }
}
