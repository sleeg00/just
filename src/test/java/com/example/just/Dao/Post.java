package com.example.just.Dao;

import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
@Data
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PostContent postContent;

    @Column(name = "post_picture")
    private Long post_picture;

    @Column(name = "post_create_time")  //글 생성 시간
    private Long post_create_time;

    @Column(name = "post_like")
    private Long post_like;

    @Column(name = "secret")
    private boolean secret;

    @Column(name = "emoticon")
    private String emoticon;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @JsonIgnore
    @Builder.Default
    private List<Member> likedMembers = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<HashTagMap> hashTagMaps = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 원하지 않는 데이터를 가져오지 않기 위해 LAZY로 설정
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "post", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    @Column(name = "blamed_count")
    private Long blamedCount;


    @PrePersist
    public void prePersist() {
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.emoticon = this.emoticon == null ? "0" : this.emoticon;
    }



    public boolean getSecret() {
        return this.secret;
    }



    public List<HashTag> getHashTag() {
        List<HashTag> array = new ArrayList<>();




        return array;
    }


    public void addHashTagMaps(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
}