package com.example.just.Service;

import com.example.just.Util.RedisKeyUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;

    public void incrementLikeCount(Long postId) {
        String countKey = redisKeyUtil.getPostLikeCountKey(postId);
        redisTemplate.opsForValue().increment(countKey);
    }

    public void decrementLikeCount(Long postId) {
        String countKey = redisKeyUtil.getPostLikeCountKey(postId);
        redisTemplate.opsForValue().decrement(countKey);
    }


    public Boolean changePostLikeStatusIfExists(Long member_id, Long post_id) {
        String postLikeKey = redisKeyUtil.getPostLikeKey(post_id);
        String trueValue = member_id + ":true";
        String falseValue = member_id + ":false";
        Double now = Double.valueOf(System.currentTimeMillis()/1000); // ms로 변환

        Double scoreTrue = redisTemplate.opsForZSet().score(postLikeKey, trueValue);
        Double scoreFalse = redisTemplate.opsForZSet().score(postLikeKey, falseValue);


        if (scoreTrue != null && scoreTrue <= now) {
            redisTemplate.opsForZSet().remove(postLikeKey, trueValue);
            scoreTrue = null;
        }
        if (scoreFalse != null && scoreFalse <= now) {
            redisTemplate.opsForZSet().remove(postLikeKey, falseValue);
            scoreFalse = null;
        }

        if (scoreTrue != null) {
            // 현재 좋아요 상태가 True → 토글하여 False로 변경
            redisTemplate.opsForZSet().remove(postLikeKey, trueValue);
            redisTemplate.opsForZSet().add(postLikeKey, falseValue, scoreTrue);
            return false; // 최종 상태: 좋아요 취소
        } else if (scoreFalse != null) {
            // 현재 좋아요 상태가 False → 토글하여 True로 변경
            redisTemplate.opsForZSet().remove(postLikeKey, falseValue);
            redisTemplate.opsForZSet().add(postLikeKey, trueValue, scoreFalse);
            return true;  // 최종 상태: 좋아요
        } else { // 데이터 없음 DB Search
            return null;
        }
    }

    public void insertPostLikeStatus(Long member_id, Long post_id, boolean status) {
        String postLikeKey = redisKeyUtil.getPostLikeKey(post_id);
        String value = member_id + ":" + status;
        long ttlMillis = 3 * 60 * 10000; // 30분
        long expireAt = System.currentTimeMillis() + ttlMillis;

        redisTemplate.opsForZSet().add(postLikeKey, value, expireAt);
    }
}
