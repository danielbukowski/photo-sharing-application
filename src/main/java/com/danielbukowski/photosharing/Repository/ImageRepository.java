package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Transactional
    @Modifying
    @Query(
            "DELETE FROM Image i " +
            "WHERE i.account.id = :accountId " +
            "AND i.id = :imageId"
    )
    void deleteByImageIdAndAccountId(UUID imageId, UUID accountId);

    @Query(
            "SELECT i FROM Image i " +
            "WHERE i.account.id = :accountId"
    )
    List<Image> getImagesByAccountId(UUID accountId);

    Page<Image> getAllImagesByAccountId(Pageable pageable, UUID accountId);

    Page<Image> findAllByIsPrivateFalse(Pageable pageable);
}
