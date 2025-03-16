package com.example.just.Dto;


import jakarta.persistence.Id;

import lombok.Getter;

import lombok.Setter;


@Getter
@Setter
public class Fcm {
    @Id
    public int memberId;
    public String token;

    public Fcm() {

    }
}