package com.danielbukowski.photosharing.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "email_verification_token_id"
    )
    private UUID id;

    @JoinColumn(
            name = "account_id",
            nullable = false,
            updatable = false
    )
    @OneToOne(
            optional = false
    )
    private Account account;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime expirationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerificationToken that = (EmailVerificationToken) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
