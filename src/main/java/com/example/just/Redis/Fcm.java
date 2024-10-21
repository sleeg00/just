package com.example.just.Redis;


import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class Fcm {
    @Id
    public Long member_id;
    public String token;
}
