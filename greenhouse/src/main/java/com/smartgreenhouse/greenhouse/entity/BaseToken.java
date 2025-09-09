package com.smartgreenhouse.greenhouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false)
    private Boolean revoked = false;

    @Column(nullable = false, updatable = false)
    private Instant created =  Instant.now();

    public abstract User getUser();
    public abstract void setUser(User user);
}
