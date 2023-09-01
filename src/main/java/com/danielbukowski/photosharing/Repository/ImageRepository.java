package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Transactional
    @Modifying
    @Query(
            "DELETE FROM Image i " +
            "WHERE i.account.id = :accountId"
    )
    void deleteByAccountId(UUID accountId);

    @Query(
            "SELECT i FROM Image i " +
            "WHERE i.id = :imageId " +
            "AND i.account.id = :accountId"
    )
    Optional<Image> findByImageIdAndAccountId(UUID imageId, UUID accountId);

}
