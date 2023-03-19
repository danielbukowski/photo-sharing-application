package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
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
    public UserDetails loadUserByUsername(String email) {
        return accountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(
                        () -> new AccountNotFoundException("An account with this email: %s doesn't exist".formatted(email))
                );
    }
}
