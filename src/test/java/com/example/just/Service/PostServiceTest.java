package com.example.just.Service;



import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import com.example.just.Dto.PostPostDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.google.cloud.Tuple;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


@ActiveProfiles("test")
@SpringBootTest
class PostServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    private List<Member> members = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 10; i++) {
            Member member = new Member();
            member.setId(Long.valueOf(i));
            members.add(member);
        }
    }

    @DisplayName("글_쓰기_테스트")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void write_of_post(int index) {
        Member member = members.get(index);
        PostPostDto postDto = createDefaultPost(member);

        Post post = postService.write(member, postDto);

        assertEquals(postDto.getContent(), post.getPostContent().getContent());
        assertEquals(postDto.getMember().getId(), post.getMember().getId());
        assertEquals(postDto.getHash_tag().get(0),post.getHashTagMaps().get(0).getHashTag().getName());
        assertEquals(postDto.getHash_tag().get(1),post.getHashTagMaps().get(1).getHashTag().getName());
    }

    @DisplayName("비회원_글_조회_테스트")
    @Test
    void guest_read_of_post() throws SQLException {
        long cursor = 20250206205819L;
        long limit = 1L;

        ResponseGetPost responseGetPost = postService.searchByCursor(cursor, limit);

        assertEquals(responseGetPost.isHasNext(), true);
    }
    private PostPostDto createDefaultPost(Member member) {
        PostPostDto postDto = new PostPostDto();
        List<String> hashTags = new ArrayList<>();
        for(int i=0; i<2; i++)
            hashTags.add("test"+i);
        postDto.setHash_tag(hashTags);
        postDto.setPost_picture(0L);
        postDto.setContent("Test");
        postDto.setSecret(true);
        postDto.setMember(member);
        return postDto;
    }
}