package com.example.just.Service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.just.Dao.PostLike;
import com.example.just.Repository.PostLikeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private EntityManager em;

    @Mock
    private Query query;


    @InjectMocks
    private PostLikeService postLikeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void 좋아요가_이미_존재하면_false를_반환() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Optional<PostLike> existingLike = Optional.of(new PostLike(1L, 1L)); // 존재하는 좋아요 객체
        when(postLikeRepository.findByMemberAndPost(memberId, postId)).thenReturn(existingLike);

        // when
        boolean result = postLikeService.addPostLikeIfExists(memberId, postId);

        // then
        assertFalse(result); // 좋아요가 존재하면 false여야 함
    }

    @Test
    void 좋아요가_존재하지_않으면_true를_반환() {
        // given
        Long memberId = 2L;
        Long postId = 2L;
        // mock 설정: 좋아요가 없는 경우 Optional.empty() 반환
        when(postLikeRepository.findByMemberAndPost(memberId, postId))
                .thenReturn(Optional.empty());

        // when
        boolean result = postLikeService.addPostLikeIfExists(memberId, postId);

        // then
        assertTrue(result); // 좋아요가 없으면 true여야 함
    }

    @Test
    void bulkInsertPostLikes_호출시_SQL_실행_검증() {
        // given
        Set<Pair<Long, Long>> likeSet = new HashSet<>();
        likeSet.add(Pair.of(1L, 101L));
        likeSet.add(Pair.of(2L, 102L));

        when(em.createNativeQuery(anyString())).thenReturn(query); // SQL 실행을 Mocking
        when(query.executeUpdate()).thenReturn(2); // 2개의 row가 삽입된다고 가정

        // when
        postLikeService.bulkInsertPostLikes(likeSet);

        // then
        verify(em, times(1)).createNativeQuery(anyString()); // SQL 생성 완료
        verify(query, times(1)).executeUpdate(); // 실행 여부 검증
    }

    @Test
    void bulkInsertPostLikes_빈_셋이면_SQL_실행되지_않음() {
        // given
        Set<Pair<Long, Long>> likeSet = new HashSet<>();

        // when
        postLikeService.bulkInsertPostLikes(likeSet);

        // then
        verify(em, never()).createNativeQuery(anyString()); // SQL 실행되지 않아야 함
    }

    @Test
    void bulkDeletePostLikes_호출시_SQL_실행_검증() {
        // given
        Set<Pair<Long, Long>> unlikeSet = new HashSet<>();
        unlikeSet.add(Pair.of(1L, 101L));
        unlikeSet.add(Pair.of(2L, 102L));

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        // when
        postLikeService.bulkDeletePostLikes(unlikeSet);

        // then
        verify(em, times(1)).createNativeQuery(anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    void bulkDeletePostLikes_빈_셋이면_SQL_실행되지_않음() {
        // given
        Set<Pair<Long, Long>> unlikeSet = new HashSet<>();

        // when
        postLikeService.bulkDeletePostLikes(unlikeSet);

        // then
        verify(em, never()).createNativeQuery(anyString());
    }

}
