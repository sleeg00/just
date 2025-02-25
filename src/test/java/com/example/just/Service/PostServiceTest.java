//package com.example.just.Service;
//
//import static org.assertj.core.util.DateUtil.now;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.example.just.Dao.Member;
//import com.example.just.Dao.Post;
//import com.example.just.Dao.Role;
//import com.example.just.Document.PostDocument;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.parameters.P;
//
//@SpringBootTest
//public class PostServiceTest {
//
//    private Member member;
//    private
//    @BeforeEach
//    void setup() {
//        member = new Member();
//        member.setId(1L);
//        member.setEmail("test@gmail.com");
//        member.setToken("test_token");
//        member.setNickname("tester");
//        member.setProvider_id("test");
//        member.setAuthority(Role.USER);
//        member.setCreateTime(now());
//        member.setRefreshToken("tester");
//    }
//    @Test
//    @DisplayName("게시글 작성 정상 테스트")
//    void write() {
//        // System.out.println("Transaction ReadOnly Second: " + TransactionSynchronizationManager.isCurrentTransactionReadOnly());
//        Post post = new Post();
//
//        post.writePost(postDto, member);
//        Post p = postRepository.save(post);
//
//        List<String> hashTags = postDto.getHash_tag();
//        saveHashTag(hashTags, p);
//
//        PostDocument postDocument = new PostDocument(p);
//        postContentESRespository.save(new PostDocument(p));
//        return postDto;
//    }
//}