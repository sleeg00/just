package com.example.just.Util;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
    private static final String POST_LIKE_KEY_FORMAT = "post:like:%d"; // "post1:like", "post2:like"
    private static final String POST_LIKE_COUNT_KEY_FORMAT = "post:like:count:%d"; // "post1:like", "post2:like"
    public String getPostLikeKey(Long post_id) {
        return String.format(POST_LIKE_KEY_FORMAT, post_id);
    }

    public String getPostLikeCountKey(Long post_id) {
        return String.format(POST_LIKE_COUNT_KEY_FORMAT, post_id);
    }
}
