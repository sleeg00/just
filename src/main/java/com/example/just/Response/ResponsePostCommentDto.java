package com.example.just.Response;

import com.example.just.Dao.PostContent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDto {
    private PostContent post_content;
    private List<ResponseCommentDto> comments;

    public ResponsePostCommentDto(PostContent content, List<ResponseCommentDto> list){
        post_content = content;
        comments = list;
    }
}
