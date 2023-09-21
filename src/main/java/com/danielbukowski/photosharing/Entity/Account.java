package com.danielbukowski.photosharing.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account implements UserDetails {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "account_id"
    )
    private UUID id;

    @Column(
            unique = true,
            nullable = false
    )
    private String email;

    @Column(
            unique = true,
            nullable = false
    )
    private String nickname;

    private String biography;

    private boolean isLocked;

    @Column(
            nullable = false
    )
    private String password;

    private boolean isEmailVerified;

    @OneToMany(
            mappedBy = "account",
            cascade = PERSIST
    )
    private List<Image> images = new ArrayList<>();

    @ManyToMany(
            cascade = {MERGE, PERSIST}
    )
    @JoinTable(
            name = "accounts_roles",
            joinColumns = @JoinColumn(name = "account_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.getAccounts().add(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> accountAuthorities = new ArrayList<>(15);
        roles.forEach(e -> accountAuthorities.add(new SimpleGrantedAuthority("ROLE_" + e.getName())));

        if (!isEmailVerified) {
            accountAuthorities.add(new SimpleGrantedAuthority("USER:READ"));
            return accountAuthorities;
        }

        for (Role role : roles) {
            for (String permission : role.getPermissions().split(",")) {
                accountAuthorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return accountAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
