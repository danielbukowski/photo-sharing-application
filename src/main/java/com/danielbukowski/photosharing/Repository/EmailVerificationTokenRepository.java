package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, UUID> {

    @Override
    @Query(
            "SELECT t FROM EmailVerificationToken t " +
            "LEFT JOIN FETCH  t.account " +
            "WHERE t.id = :id"
    )
    @EntityGraph(attributePaths = "account")
    Optional<EmailVerificationToken> findById(UUID id);


    @Query(
            "SELECT t FROM EmailVerificationToken t " +
            "WHERE t.account.id = :accountId"
    )
    Optional<EmailVerificationToken> findByAccountId(UUID accountId);
}
