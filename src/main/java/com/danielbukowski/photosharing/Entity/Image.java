package com.danielbukowski.photosharing.Entity;


import com.danielbukowski.photosharing.Enum.FileExtension;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID id;

    @Column(unique = true)
    private String path;

    @Column(updatable = false, nullable = false)
    private String title;

    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private FileExtension extension;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

}
