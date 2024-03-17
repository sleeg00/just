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

    @ElementCollection
    @CollectionTable(name = "post_content", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "content", length = 300)
    @JsonIgnore
    private List<String> postContent;
<<<<<<< HEAD

    //글 태그
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column(name = "tag")
    private List<HashTag> hash_tag;

=======
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
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

<<<<<<< HEAD
=======

>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @ManyToMany()
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @JsonIgnore
    @Builder.Default
    private List<Member> likedMembers = new ArrayList<>();

<<<<<<< HEAD
=======
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<HashTagMap> hashTagMaps = new ArrayList<>();

>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @ManyToOne()
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

<<<<<<< HEAD
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
=======
    @OneToMany(mappedBy = "post", orphanRemoval = true)
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    private List<Comment> comments = new ArrayList<>();
    @Column(name = "blamed_count")
    private Long blamedCount;


    @PrePersist
    public void prePersist() {
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.emoticon = this.emoticon == null ? "0" : this.emoticon;
    }

    public void writePost(PostPostDto postDto, Member member) { // 글 쓰기 생성자
        List<String> contentList = postDto.getPost_content();
        this.postContent = contentList;
<<<<<<< HEAD
        if (postDto.getHash_tag() != null) {
            for (int i = 0; i < postDto.getHash_tag().size(); i++) {
                String hashTag_name = postDto.getHash_tag().get(i);
                HashTag hashTag = new HashTag(hashTag_name);
                addHashTag(hashTag);
            }
        }
=======
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
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

<<<<<<< HEAD
    public void addHashTag(HashTag hashTag) {
        if (hash_tag == null) {
            hash_tag = new ArrayList<>();
        }
        hash_tag.add(hashTag);
        hashTag.setPost(this);
    }
=======
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

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
        this.postContent = postDto.getPost_content();
<<<<<<< HEAD
        this.hash_tag = null;
        if (postDto.getHash_tage() != null) {
            for (int i = 0; i < postDto.getHash_tage().size(); i++) {
                String hashTag_name = postDto.getHash_tage().get(i);
                HashTag hashTag = new HashTag(hashTag_name);
                addHashTag(hashTag);
            }
=======
        this.hashTagMaps = new ArrayList<>();
        for (int i = 0; i < postDto.getHash_tage().size(); i++) {
            HashTagMap hashTagMap = new HashTagMap();
            hashTagMap.setPost(this);
            hashTagMap.setHashTag(new HashTag(postDto.getHash_tage().get(i)));
            this.addHashTagMaps(hashTagMap);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
        }
    }

    public List<HashTag> getHashTag() {
        List<HashTag> array = new ArrayList<>();
<<<<<<< HEAD
=======
        /*
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
        if (this.hash_tag != null) {
            for (int i = 0; i < this.hash_tag.size(); i++) {
                array.add(this.hash_tag.get(i));
            }
        }
<<<<<<< HEAD
        return array;
    }
=======

         */
        return array;
    }


    public void addHashTagMaps(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
}