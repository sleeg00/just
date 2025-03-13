package com.example.just.Scheduled;

import com.example.just.Service.RedisService;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeCountScheduler {

    private final RedisService redisService;
    private final Cache<Long, AtomicLong> postLikeCache;
    private final EntityManager em;

    //  매일 00:05에 Redis → DB로 좋아요 동기화
    @Scheduled(cron = "0 5 0 * * ?")  // 매일 00:05 실행
    public void syncPostLikeCounts() {

        Map<Long, Long> like = postLikeCache.asMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        if (!like.isEmpty()) {
            updatePostLikeCount(like);
        }
    }

    // postId 리스트 기반으로 DB 업데이트
    @Transactional
    public void updatePostLikeCount(Map<Long, Long> postLikeCount) {
        // 쿼리 시작
        String sql = "UPDATE post SET post_like = CASE post_id ";

        // 각 post_id에 대해 업데이트할 쿼리 부분을 생성
        List<Long> postIds = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : postLikeCount.entrySet()) {
            Long postId = entry.getKey();
            Long likeCount = entry.getValue();

            // 조건문 생성
            sql += "WHEN " + postId + " THEN " + likeCount + " ";
            postIds.add(postId);
        }

        // WHERE절에 조건을 추가
        sql += "END WHERE post_id IN (" + String.join(", ",
                postIds.stream().map(String::valueOf).toArray(String[]::new)) + ")";

        // 쿼리 실행
        em.createNativeQuery(sql).executeUpdate();
    }
}
