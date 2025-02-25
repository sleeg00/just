package com.example.just.Response;

import static com.example.just.Dao.QPost.post;

import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
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


    public ResponseGetMemberPostDto(List<Post> results, Long member_id, int i, List<HashTagMap> hashTagMaps) {

        this.post_id = results.get(i).getPost_id();
        this.post_content = results.get(i).getPostContent();
        this.post_picture = results.get(i).getPost_picture();

        this.hash_tag = hashTagMaps.get(0).getHashTag().getName();
        this.post_create_time = results.get(i).getPost_create_time();
        this.blamed_count = results.get(i).getBlamedCount();
        this.secret = results.get(i).getSecret();
        this.post_like_size = results.get(i).getPost_like();
        this.comment_size = ((long) results.get(i).getComments().size());
        if (member_id != -1) {
            if (results.get(i).getMember().getId() == member_id) {
                this.mine = true;
            } else {
                this.mine = false;
            }
        }
    }

}