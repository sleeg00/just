package com.example.just.Repository;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByMemberAndPost(Member member, Post post);

    @Query("SELECT pl FROM PostLike pl WHERE pl.member = :member AND pl.post = :post")
    PostLike findByMemberAndPostWithLock(@Param("member") Member member,
                                                   @Param("post") Post post);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = :post")
    long countByPostId(@Param("post") Post post);

    Long countAllByPost(Post post);
}
