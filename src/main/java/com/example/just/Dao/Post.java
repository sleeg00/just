package com.example.just.Dao;

import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
@Fetch(FetchMode.SELECT)
    private List<PostContent> postContent = new ArrayList<>();

    @Column(name = "post_picture")
    private Long post_picture;

    @CreationTimestamp
    @Column(name = "post_create_time")  //글 생성 시간
    private Date post_create_time;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<HashTagMap> hashTagMaps = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 원하지 않는 데이터를 가져오지 않기 위해 LAZY로 설정
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "post", orphanRemoval = true, fetch = FetchType.EAGER)

    private List<Comment> comments = new ArrayList<>();
    @Column(name = "blamed_count")
    private Long blamedCount;


    @PrePersist
    public void prePersist() {
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.emoticon = this.emoticon == null ? "0" : this.emoticon;
    }

    public void writePost(PostPostDto postDto, Member member) { // 글 쓰기 생성자
        List<PostContent> contentList = new ArrayList<>();
        for(int i=0; i<postDto.getPost_content().size(); i++){
            PostContent postContent = new PostContent();
            postContent.setContent(postDto.getPost_content().get(i));
            postContent.setPost(this);
            contentList.add(postContent);
        }
        this.postContent = contentList;
        this.post_picture = postDto.getPost_picture();
        this.secret = postDto.getSecret();
        this.emoticon = "";
        this.post_like = 0L;
        this.member = member;
        this.blamedCount = 0L;
    }

    public void updatePost(String post_tag, Long post_like, Date post_create_time,
                           boolean secret, String emoticon, String post_category, Member member) {
        this.post_like = post_like;
        this.post_create_time = post_create_time;
        this.secret = secret;
        this.emoticon = emoticon;
        this.member = member;
        this.member.updateMember(this);
    }

    public void addLike(Member member) {
        if (!likedMembers.contains(member)) {
            member.getLikedPosts().add(this);//좋아한 글 List에 해당 글의 객체 추가
            post_like++;
        }
    }

    public void removeLike(Member member) {
        if (likedMembers.contains(member)) {
            member.getLikedPosts().remove(this);
            post_like--;
        }
    }


    public void addBlamed() {
        blamedCount++;
    }


    public boolean getSecret() {
        return this.secret;
    }

    public void changePost(PutPostDto postDto, Member member, Post post) {
        this.post_id = post.getPost_id();
        this.member = member;
        this.setPost_create_time(new Date(System.currentTimeMillis()));
        this.setPost_like(post.getPost_like());
        this.post_picture = postDto.getPost_picture();
        this.secret = postDto.getSecret();
        List<PostContent> contentList = new ArrayList<>();
        for(int i=0; i<postDto.getPost_content().size(); i++){
            PostContent postContent = new PostContent();
            postContent.setContent(postDto.getPost_content().get(i));
            postContent.setPost(this);
            contentList.add(postContent);
        }
        this.postContent = contentList;
        this.hashTagMaps = new ArrayList<>();
        for (int i = 0; i < postDto.getHash_tage().size(); i++) {
            HashTagMap hashTagMap = new HashTagMap();
            hashTagMap.setPost(this);
            hashTagMap.setHashTag(new HashTag(postDto.getHash_tage().get(i)));
            this.addHashTagMaps(hashTagMap);
        }
    }

    public List<HashTag> getHashTag() {
        List<HashTag> array = new ArrayList<>();
        /*
        if (this.hash_tag != null) {
            for (int i = 0; i < this.hash_tag.size(); i++) {
                array.add(this.hash_tag.get(i));
            }
        }

         */
        return array;
    }


    public void addHashTagMaps(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
}