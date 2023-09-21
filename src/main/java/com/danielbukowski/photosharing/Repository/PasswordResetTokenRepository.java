package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, UUID> {

    @Override
    @EntityGraph(attributePaths = "account")
    Optional<PasswordResetToken> findById(UUID id);
}
