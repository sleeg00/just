package com.example.just.Controller;


import com.example.just.Resolver.ExtractMember;
import com.example.just.Resolver.ExtractPost;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.Post.PostLikeResponseDto;
import com.example.just.Dto.Post.PostPostDto;
import com.example.just.Dto.Post.PutPostDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.Service.PostService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/api/posts")
@Api(tags = {"post controller"}, description = "게시글 관련 api")
@RestController
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MemberRepository memberRepository;


    @Operation(
            summary = "비회원용 최근 게시글 조회",
            description = "cursor(기준 시간)를 바탕으로 최근 게시글을 조회합니다."
    )
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentPosts(
            @RequestParam Long cursor,
            @RequestParam(defaultValue = "30") Long size) throws NotFoundException {
        try {
            return ResponseEntity.ok(postService.searchByCursor(cursor, size));
        } catch (SQLException e) {
            return ResponseEntity.notFound().build();
        }
    }


//    @Operation(summary = "자기의 게시글을 조회하는 API", description = "<big> 자신의 게시글을 조회한다</big>")
//    @GetMapping("/my")
//    public ResponseEntity<Object> getMyPosts(@ExtractMember Member member) throws NotFoundException {
//        return ResponseEntity.ok(postService.getMyPost(member));
//    }
//
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
        Post updatePost = postService.togglePostLike(post_id, liker_id);
        return ResponseEntity.ok(updatePost);
    }
    @Operation(summary = "게시글 좋아요 취소 API")
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<PostLikeResponseDto> cancelPostLike(@RequestParam Long post_id,
                                                              @RequestParam Long liker_id) {
        Post updatedPost = postService.cancelPostLike(post_id, liker_id);
        return ResponseEntity.ok(PostLikeResponseDto.fromEntity(updatedPost));
    }

    @ApiOperation(value = "댓글 신고 횟수 조회")
    @GetMapping("/get/post/blame/{postId}")
    public int blameGetComment(@PathVariable Long postId) throws NotFoundException {
        return postService.blameGetPost(postId);
    }

    @ApiOperation(value = "자신이 좋아요한 글 조회")
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