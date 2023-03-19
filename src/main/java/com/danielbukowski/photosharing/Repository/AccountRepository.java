package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(name = "SELECT a FROM Account a WHERE LOWER(a.email) = LOWER(:email)")
    Optional<Account> findByEmailIgnoreCase(String email);

    @Query(name = "SELECT a FROM Account a WHERE LOWER(a.email) = LOWER(:email)")
    Account getByEmailIgnoreCase(String email);
}
