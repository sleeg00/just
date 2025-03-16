//package com.example.just.Service;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import com.example.just.Dao.Member;
//import com.example.just.Dao.Post;
//
//import com.example.just.Dto.PostPostDto;
//import com.example.just.Repository.MemberRepository;
//import com.example.just.Repository.PostLikeRepository;
//import com.example.just.Repository.PostRepository;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//@ActiveProfiles("test")
//@SpringBootTest
//class PostServiceTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private PostService postService;
//    @Autowired
//    private PostLikeRepository postLikeRepository;
//    private final Logger logger = LoggerFactory.getLogger(PostServiceTest.class);
//    private List<Member> members = new ArrayList<>();
//
//    @BeforeEach
//    void setUp() {
//        for (int i = 1; i <= 10; i++) {
//            Member member = new Member();
//            member.setId(Long.valueOf(i));
//            members.add(member);
//        }
//    }
//
//    //
//    @DisplayName("글_쓰기_테스트")
//    @ParameterizedTest
//    @ValueSource(ints = {0, 1, 2, 3, 4})
//    void write_of_post(int index) {
//        Member member = members.get(index);
//        PostPostDto postDto = createDefaultPost(member);
//
//        Post post = postService.write(member, postDto);
//
//        assertEquals(postDto.getContent(), post.getPostContent().getContent());
//        assertEquals(postDto.getMember().getId(), post.getMember().getId());
//        assertEquals(postDto.getHash_tag().get(0), post.getHashTagMaps().get(0).getHashTag().getName());
//        assertEquals(postDto.getHash_tag().get(1), post.getHashTagMaps().get(1).getHashTag().getName());
//    }
//
//    @DisplayName("비회원_글_조회_테스트")
//    @Test
//    void guest_read_of_post() throws SQLException {
//        long cursor = 20250206205819L;
//        long limit = 1L;
//
//        ResponseGetPost responseGetPost = postService.searchByCursor(cursor, limit);
//
//        assertEquals(responseGetPost.isHasNext(), true);
//    }
//
//    @DisplayName("글_좋아요_동시성_안전_테스트")
//    @Test
//    void concurrency_check_post_like() throws InterruptedException {
//        Long postId = 1L;  // 테스트할 게시물 ID
//        Long memberId = 1L; // 기본 회원 ID
//        Post post = postRepository.findById(postId).get();
//        int threadCount = 990;  // 동시에 실행할 스레드 개수
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            Long finalMemberId = memberId + i; // 각 스레드마다 다른 사용자 ID 부여
//            executorService.submit(() -> {
//                try {
//                    postService.togglePostLike(postId, finalMemberId);
//                } catch (Exception e) {
//                    // 기존 로그를 sout으로 변경
//                    System.out.println(String.format("Error in thread %s: %s",
//                            Thread.currentThread().getName(), e.getMessage()));
//
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await(); // 모든 스레드가 종료될 때까지 대기
//        executorService.shutdown();
//
//        long likeCount = postRepository.findById(1L).get().getPost_like();
//        long storedLikes = postLikeRepository.countByPostId(post);
//
//
//    }
//
//    private PostPostDto createDefaultPost(Member member) {
//        PostPostDto postDto = new PostPostDto();
//        List<String> hashTags = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            hashTags.add("test" + i);
//        }
//        postDto.setHash_tag(hashTags);
//        postDto.setPost_picture(0L);
//        postDto.setContent("Test");
//        postDto.setSecret(true);
//        postDto.setMember(member);
//        return postDto;
//    }
//}
//
