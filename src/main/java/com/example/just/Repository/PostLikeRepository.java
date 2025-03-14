package com.example.just.Repository;

import com.example.just.Dao.Post;
import com.example.just.Dao.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT pl FROM postLike pl WHERE pl.member = :member AND pl.post= :post")
//    PostLike findByMemberAndPostWithLock(Member member, Post post);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = :post")
    long countByPostId(@Param("post") Post post);


    Long countAllByPost(Post post);

    @Query("SELECT pl FROM PostLike pl WHERE pl.member.id = :memberId AND pl.post.post_id = :postId")
    PostLike findByMemberAndPost(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLike pl WHERE pl.member.id = :memberId AND pl.post.post_id = :postId")
    void deleteByMemberAndPost(@Param("memberId") Long memberId, @Param("postId") Long postId);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO post_like (member_id, post_id) VALUES (:memberId, :postId)", nativeQuery = true)
    void saveMemberAndPost(@Param("memberId") Long memberId, @Param("postId") Long postId);


}
