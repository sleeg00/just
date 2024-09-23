package com.example.just.Response;

import com.example.just.Dao.PostContent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDtoBefore {
    private PostContent post_content;
    private List<ResponseCommentDtoBefore> comments;

    public ResponsePostCommentDtoBefore(PostContent content, List<ResponseCommentDtoBefore> list){
        post_content = content;
        comments = list;
    }
}