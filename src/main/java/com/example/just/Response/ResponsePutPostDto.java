package com.example.just.Response;

import com.example.just.Dao.Comment;
import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePutPostDto {


    private Long post_id;

    private List<PostContent> post_content;    //글 내용

    private List<HashTag> hash_tag;

    private Long post_picture;

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

    public ResponsePutPostDto(Post post) {
        this.post_content = post.getPostContent();
        this.post_id = post.getPost_id();
        this.post_picture = post.getPost_picture();
        this.hash_tag = post.getHashTag();
        this.secret = post.getSecret();
    }
}
