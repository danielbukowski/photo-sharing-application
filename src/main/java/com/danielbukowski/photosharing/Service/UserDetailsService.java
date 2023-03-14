package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("Email not found: " + email));
    }
}
