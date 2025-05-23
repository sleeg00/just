package com.example.just.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCursor {

    private Long likeCursor;
    private Long postIdCursor;
    private Long authorIdCursor;
    private Long timeCursor;
    private Long memberId;
}
