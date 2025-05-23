package com.example.just.Service;
import com.example.just.Producer.PostLikeProducer;

import com.example.just.Repository.Querydsl.Strategy.SelectLikePostQuery;
import com.example.just.Repository.Querydsl.Strategy.SelectRecentPostQuery;
import com.github.benmanes.caffeine.cache.Cache;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    @Mock
    private PostLikeService postLikeService;  // 좋아요 DB 관련 로직 Mock

    @Mock
    private RedisService redisService; // Redis 관련 로직 Mock

    @Mock
    private PostLikeProducer postLikeProducer; // RabbitMQ 메시지 큐 Mock
    @Mock
    private Cache<Long, AtomicLong> postLikeCache;
    @Mock
    private EntityManager em;
    @Mock
    private JPAQueryFactory query;

    @InjectMocks
    private PostService postService; // 위 Mock 객체들을 주입받는 PostService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
        // postLikeCache가 `asMap()`을 호출할 때 NullPointerException 발생하지 않도록 설정
        ConcurrentMap<Long, AtomicLong> mockCacheMap = mock(ConcurrentMap.class);
        when(postLikeCache.asMap()).thenReturn(mockCacheMap);

        // 특정 postId에 대해 캐시 값 반환 설정
        Long postId = 1L;
        AtomicLong mockCounter = new AtomicLong(10); // 좋아요 개수 10으로 설정
        when(mockCacheMap.computeIfAbsent(eq(postId), any())).thenReturn(mockCounter);

        postService = new PostService(em, query, redisService, postLikeService, postLikeProducer,
                postLikeCache, new SelectLikePostQuery(query), new SelectRecentPostQuery(query)); // 직접 주입
    }

    @Test
    @DisplayName("좋아요 기능 캐시 존재시 작동 테스트")
    void testTogglePostLike_WhenCacheExists() throws Exception {
        // Given: Redis에 좋아요 상태가 이미 존재하는 경우
        Long postId = 1L, memberId = 10L;
        when(redisService.changePostLikeStatusIfExists(memberId, postId)).thenReturn(true);

        // When: 좋아요 기능 실행
        String result = postService.togglePostLike(postId, memberId);

        // Then: 결과 검증
        assertEquals("ok..", result);
        verify(redisService, times(1)).changePostLikeStatusIfExists(memberId, postId);
        verify(postLikeService, never()).addPostLikeIfExists(anyLong(), anyLong()); // DB 호출 X
        verify(postLikeProducer, times(1)).sendPostLikeMessage(postId, memberId, true);
    }
}
