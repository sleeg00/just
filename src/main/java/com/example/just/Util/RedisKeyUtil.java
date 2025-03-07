package com.example.just.Util;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
    private static final String POST_LIKE_KEY_FORMAT = "post%d:like"; // "post1:like", "post2:like"

    public String getPostLikeCountKey(Long postId) {
        return String.format(POST_LIKE_KEY_FORMAT, postId);
    }
}
