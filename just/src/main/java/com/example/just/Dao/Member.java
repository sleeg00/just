package com.example.just.Dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_email")
    private String member_email;

    @Column(name="member_register")
    private Data member_register;

    @OneToMany(mappedBy = "member")
    private List<Post> posts;

}
