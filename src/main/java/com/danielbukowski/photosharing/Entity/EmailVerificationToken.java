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
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
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

}
