package com.example.just.Response;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QPost;
import com.google.firebase.database.annotations.Nullable;
import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetMemberPostDto {
    private Long post_id;

    private PostContent post_content;
    private String hash_tag;    //글 태그

    private Long post_picture;

    private Long post_create_time;  //글 생성 시간

    private Boolean secret; // nullable Boolean으로 변경


    private String post_category; //글 카테고리

    private Long comment_size;

    private Long post_like_size;
    private Long blamed_count;


    private boolean mine;

    public ResponseGetMemberPostDto() {

    }


    public static ResponseGetMemberPostDto from(Tuple tuple, @Nullable Long memberId) {
        QPost post = QPost.post;
        QHashTag tag = QHashTag.hashTag;

        Post p = tuple.get(post);
        HashTag t = tuple.get(tag);

        ResponseGetMemberPostDto dto = new ResponseGetMemberPostDto();
        dto.setPost_id(p.getPost_id());
        dto.setPost_content(p.getPostContent());
        dto.setPost_picture(p.getPost_picture());
        dto.setPost_create_time(p.getPost_create_time());
        dto.setBlamed_count(p.getBlamedCount());
        dto.setSecret(p.getSecret());
        dto.setPost_like_size(p.getPost_like());

        if (t != null) {
            dto.setHash_tag(t.getName());
        }
        if (memberId != null) {
            dto.setMine(p.getMember().getId().equals(memberId));
        }

        return dto;
    }

}