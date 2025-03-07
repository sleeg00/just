package com.example.just.Service;

import com.example.just.Util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;

    public void incrementLikeCount(Long post_id) {
        String likeCountKey = redisKeyUtil.getPostLikeCountKey(post_id);
        redisTemplate.opsForValue().increment(likeCountKey);
    }

    public void decrementLikeCount(Long post_id) {
        String likeCountKey = redisKeyUtil.getPostLikeCountKey(post_id);
        redisTemplate.opsForValue().decrement(likeCountKey);
    }
}
