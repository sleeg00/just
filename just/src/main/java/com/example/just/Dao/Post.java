package com.example.just.Dao;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @Column(name = "post_content")  //글 내용
    private String post_content;

    @Column(name = "post_tag")  //글 태그
    private String post_tag;

    @Column(name = "post_like") //공감 회수
    private Long post_like;

    @Column(name = "post_create_time")  //글 생성 시간
    private Date post_create_time;

    @Column(name = "secret")    //글 공개 여부
    private boolean secret;

    @Column(name = "emoticon")  //글 이모티콘
    private String emoticon;

    @Column(name = "post_category") //글 카테고리
    private Long Post_category;

    @ManyToOne
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    private Member member;

    @OneToMany(mappedBy = "post")   //글에 대한 알림
    private List<Notification> notifications;


}
