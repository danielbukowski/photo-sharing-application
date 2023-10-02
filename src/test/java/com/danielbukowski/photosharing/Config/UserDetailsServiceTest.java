package com.danielbukowski.photosharing.Config;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Role;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.UUID;


@TestConfiguration
public class UserDetailsServiceTest implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username.equalsIgnoreCase("userEmailVerified")) {
            return Account.builder()
                    .email("user1@gmail.com")
                    .password("password")
                    .id(new UUID(1,1))
                    .isEmailVerified(true)
                    .isLocked(false)
                    .nickname("user1")
                    .roles(
                            Set.of(
                                    Role.builder()
                                            .name("USER")
                                            .permissions("USER:READ,USER:CREATE,USER:UPDATE,USER:DELETE")
                                            .build()
                            )
                    )
                    .build();
        }

        if (username.equalsIgnoreCase("UserNotEmailVerified")) {
            return Account.builder()
                    .email("user2@gmail.com")
                    .password("password")
                    .id(new UUID(2,2))
                    .isEmailVerified(false)
                    .isLocked(false)
                    .nickname("user2")
                    .roles(
                            Set.of(
                                    Role.builder()
                                            .name("USER")
                                            .permissions("USER:READ,USER:CREATE,USER:UPDATE,USER:DELETE")
                                            .build()
                            )
                    )
                    .build();
        }
        return null;
    }
}
