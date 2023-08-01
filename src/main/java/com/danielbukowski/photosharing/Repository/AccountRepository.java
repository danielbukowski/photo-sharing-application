package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Transactional
    @Modifying
    @Query(
            "UPDATE Account a " +
            "SET a.password = :password " +
            "WHERE a.id = :id"
    )
    void updatePasswordById(String password, UUID id);

    @Query(
            "SELECT DISTINCT a " +
            "FROM Account a " +
            "LEFT JOIN FETCH a.images i " +
            "WHERE LOWER(a.email) = LOWER(:email)"
    )
    @EntityGraph(attributePaths = { "images" })
    Optional<Account> findByEmailIgnoreCase(String email);

    @Override
    @Query(
            "SELECT a, i " +
            "FROM Account a " +
            "LEFT JOIN FETCH a.images i " +
            "WHERE a.id = :id"
    )
    @EntityGraph(attributePaths = { "images" })
    Optional<Account> findById(UUID id);

    @Query(
            "SELECT COUNT(a) > 0 " +
            "FROM Account a " +
            "WHERE LOWER(a.email) = LOWER(:email)"
    )
    boolean existsByEmailIgnoreCase(String email);

}
