package com.danielbukowski.photosharing.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "image_id"
    )
    private UUID id;

    @Column(
            updatable = false,
            nullable = false
    )
    private String title;

    @Column(
            updatable = false,
            nullable = false
    )
    private String contentType;

    @Column(
            updatable = false,
            nullable = false
    )
    private LocalDateTime creationDate;

    @Column(
            nullable = false
    )
    private boolean isPrivate;

    @ManyToMany
    @JoinTable(
            name = "images_likes",
            joinColumns = @JoinColumn(name = "image_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "account_id", nullable = false)
    )
    private Set<Account> likes = new HashSet<>();

    @OneToMany(
            mappedBy = "image"
    )
    private List<Comment> comments;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "account_id",
            nullable = false
    )
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id.equals(image.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
