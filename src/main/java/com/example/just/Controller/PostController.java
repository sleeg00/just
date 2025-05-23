package com.example.just.Controller;


import com.example.just.Dto.PostCursor;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Resolver.ExtractMember;
import com.example.just.Resolver.ExtractPost;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;

import com.example.just.Repository.MemberRepository;
import com.example.just.Response.ResponseGetPostDto;
import com.example.just.Service.PostService;
import com.example.just.SortType;
import com.example.just.jwt.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/posts")
@Tag(name = "post controller")
@RestController
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MemberRepository memberRepository;


    @Operation(summary = "비회원용 게시글 조회 - 최신순", description = "cursor를 기준으로 최신순 게시글을 조회합니다.")
    @PostMapping("/posts/recent")
    public ResponseEntity<?> getRecentPosts(@RequestBody PostCursor postCursor) throws NotFoundException {

            return ResponseEntity.ok(postService.getPostsByRecent(postCursor));
    }

    @Operation(summary = "좋아요순 게시글 조회 (커서 기반)", description = "좋아요순 정렬 기준으로 페이징된 게시글을 조회합니다.")
    @PostMapping("/posts/like")
    public ResponseEntity<?> getPostsByLike(@RequestBody PostCursor postCursor) {
        return ResponseEntity.ok(postService.getPostsByLike(postCursor));
    }
    @PostMapping("/posts/member/recent")
    public ResponseEntity<?> getRecentMemberPosts(
            @RequestBody PostCursor postCursor, @ExtractMember Member member) throws NotFoundException {
        if (member!=null) {
            postCursor.setMemberId(member.getId());
        }

        return ResponseEntity.ok(postService.getPostsByRecent(postCursor));
    }

    @Operation(summary = "좋아요순 게시글 조회 (커서 기반)", description = "좋아요순 정렬 기준으로 페이징된 게시글을 조회합니다.")
    @PostMapping("/posts/like")
    public ResponseEntity<?> getPostsByMemberLike(@RequestBody PostCursor postCursor,
                                                   @ExtractMember Member member) {
        if (member!=null) {
            postCursor.setMemberId(member.getId());
        }
        return ResponseEntity.ok(postService.getPostsByLike(postCursor));
    }


//    @Operation(summary = "게시글 랜덤하게 조회(회원용) api", description = "자기가 좋아요한 글을 조회했다면"
//            + "\n like : true 아니라면 like : false 이다.")
//    @GetMapping("/member")
//    public ResponseEntity<Object> getMemberPosts(@ExtractMember Member member, @RequestParam Long cursor,
//                                                 @RequestParam(defaultValue = "30") Long size) {
//        return ResponseEntity.ok(postService.searchByCursorMember(cursor, size, member.getId()));
//    }
//
//
//    @Operation(summary = "자기의 게시글을 조회하는 API", description = "<big> 자신의 게시글을 조회한다</big>")
//    @GetMapping("/my")
//    public ResponseEntity<Object> getMyPosts(@ExtractMember Member member) throws NotFoundException {
//        return ResponseEntity.ok(postService.getMyPost(member));
//    }
//

//    @Operation(summary = "게시글 랜덤하게 조회(회원용) api", description = "자기가 좋아요한 글을 조회했다면"
//            + "\n like : true 아니라면 like : false 이다.")
//    @GetMapping("/member")
//    public ResponseEntity<Object> getMemberPosts(@ExtractMember Member member, @RequestParam Long cursor,
//                                                 @RequestParam(defaultValue = "30") Long size) {
//        return ResponseEntity.ok(postService.searchByCursorMember(cursor, size, member.getId()));
//    }

    @Operation(summary = "게시글 작성 API", description = "게시글을 작성합니다.")
    @PostMapping("")
    public ResponseEntity<Post> write(@ExtractMember Member member, @RequestBody PostPostDto postDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(postService.write(member, postDto));
    }


    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@ExtractPost Post post) {
        postService.deletePost(post);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "게시글 수정 api", description = "JSON넘길 때 null이 하나도 있으면 안됨 꼭 다채워서 넘기기")
    @PutMapping("/put/post")
    public ResponseEntity<Object> putPost(HttpServletRequest request,
                                          @RequestBody PutPostDto postDto) throws NotFoundException {
        Long member_id = getAccessTokenOfMemberId(request);
        return ResponseEntity.ok(postService.putPost(member_id, postDto));
    }

    @Operation(summary = "게시글 좋아요 api")
    @PostMapping("/likes")
    public ResponseEntity<?> togglePostLike(@RequestParam Long post_id,
                                            @RequestParam Long liker_id) {
        String updatePost = postService.togglePostLike(post_id, liker_id);
        return ResponseEntity.ok(updatePost);
    }

    @Operation(summary = "게시글 신고")
    @GetMapping("/get/post/blame/{postId}")
    public int blameGetComment(@PathVariable Long postId) throws NotFoundException {
        return postService.blameGetPost(postId);
    }

    @Operation(summary = "자신이 좋아요한 글 조회")
    @GetMapping("/get/like/member/post")
    public ResponseEntity<Object> getLikeMemberPost(HttpServletRequest request) throws NotFoundException {
        Long member_id = getAccessTokenOfMemberId(request);
        return (ResponseEntity<Object>) ResponseEntity.ok();
    }

    public Long getAccessTokenOfMemberId(HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return member_id;
    }
}