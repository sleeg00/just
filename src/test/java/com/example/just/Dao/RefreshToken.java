package com.example.just.Dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
}