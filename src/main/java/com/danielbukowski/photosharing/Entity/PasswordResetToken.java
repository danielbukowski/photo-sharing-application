package com.danielbukowski.photosharing.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "password_reset_token_id"
    )
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "account_id",
            nullable = false
    )
    private Account account;

    private LocalDateTime expirationDate;

    private boolean isAlreadyUsed;
}