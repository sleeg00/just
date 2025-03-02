package com.example.just.Dto.Post;

import com.example.just.Dao.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponseDto {
    private Long postId;
    private Long likeCount;

    public static PostLikeResponseDto fromEntity(Post post) {
        return new PostLikeResponseDto(post.getPost_id(), post.getPost_like());
    }
}
