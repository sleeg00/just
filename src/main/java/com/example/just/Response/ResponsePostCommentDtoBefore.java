package com.example.just.Response;

import com.example.just.Dao.PostContent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDtoBefore {
    private List<PostContent> post_content;
    private List<ResponseCommentDtoBefore> comments;

    public ResponsePostCommentDtoBefore(List<PostContent> content, List<ResponseCommentDtoBefore> list){
        post_content = content;
        comments = list;
    }
}