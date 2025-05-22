package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(name = "refresh_expiration", nullable = false)
    private Instant refreshExpiration;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;
}
