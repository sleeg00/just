package com.example.just.Service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.PostPostDto;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private HashTagService hashTagService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostContentESRespository postContentESRespository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("글 쓰기 테스트")
    void write() {
        // Given: 기존 회원 Mock (ID=1)
        Member member = new Member();
        member.setId(1L);

        // 테스트용 DTO 생성
        PostPostDto postDto = new PostPostDto();
        postDto.setContent("테스트 글");
        postDto.setHash_tag(List.of("#tag1", "#tag2"));
        postDto.setSecret(false);
        postDto.setCurrentTime();
        System.out.println(postDto.getPost_create_time());
        // PostContent 엔티티 생성
        PostContent postContent = new PostContent();
        postContent.setContent(postDto.getContent()); // Post의 내용 저장


        // Mock 동작 정의
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 실제 넘긴 객체 그대로 반환
        doNothing().when(hashTagService).saveHashTag(any(), any()); // 해시태그 저장 Mock 아무 동작안하도록
        when(postContentESRespository.save(any(PostDocument.class)))
                .thenAnswer(invocation -> null); // 아무 동작 없이 null 반환



        // When: 글 작성 메서드 실행
        postService.write(member, postDto);

        // Then: postRepository.save()가 호출되었는지 검증
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();
        assertEquals("테스트 글", capturedPost.getPostContent().getContent());
        assertEquals(member.getId(), capturedPost.getMember().getId());
        assertEquals(postDto.getPost_create_time(), capturedPost.getPost_create_time());
        assertFalse(capturedPost.getSecret());
    }
}