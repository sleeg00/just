package com.example.just.Response;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseMyCommentDto {
    private Long comment_id;
    private String comment_content;
    private ResponseGetMemberPostDto post;
    private Date time;

    private boolean isMine;

    public ResponseMyCommentDto(Comment comment, Long member_id, Member member, boolean isMine){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        post = new ResponseGetMemberPostDto(comment.getPost(), member_id, null);
        time = comment.getComment_create_time();
        this.isMine = isMine;
    }
}