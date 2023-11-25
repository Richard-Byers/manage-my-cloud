package com.authorisation.security;

import com.authorisation.user.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationDetailsService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userEntityRepository.findByEmail(email)
                .map(UserRegistrationDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
