package com.example.just.Service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.PostPostDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import java.util.ArrayList;
import java.util.Optional;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Test
    void find() {
        Member member = new Member(); // 기본 생성자로 객체 생성

        member.setId(1L);
        member.setEmail("slee000220@gmail.com");
        member.setProvider("GOOGLE");
        member.setProvider_id("google-12345");
        member.setNickname("slee000220");
        member.setBlamedCount(2);
        member.setBlameCount(1);
        member.setPosts(new ArrayList<>()); // 빈 리스트
        member.setNotifications(new ArrayList<>()); // 빈 리스트
        memberRepository.save(member);
        Optional<Member> cmp = memberRepository.findById(1L);
        assertEquals(member.getId(), cmp.get().getId());
        assertEquals(10, postRepository.count());

    }
    @Test
    @DisplayName("글 쓰기 테스트")
    void write_of_post() {

    }
}