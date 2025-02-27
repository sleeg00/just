package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.PostContent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

//
public class PostPostDto {

    private String content;    //글 내용

    private List<String> hash_tag;    //글 태그

    private Long post_picture;

    @JsonIgnore
    private Long post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부

    @JsonIgnore
    private List<Member> liked_members = new ArrayList<>();

    @JsonIgnore // Swagger에 postDto만 넘기려고
    private Member member;  //글을쓴 Member_id

    @JsonIgnore
    private List<Comment> comments;


    public boolean getSecret() {
        return this.secret;
    }
    // 현재 시간을 "yyyyMMddHHmmss" 형식으로 설정하는 메서드
    public void setCurrentTime() {
        this.post_create_time = Long.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }
}
