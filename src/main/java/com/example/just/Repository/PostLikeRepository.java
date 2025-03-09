package com.example.just.Repository;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT pl FROM postLike pl WHERE pl.member = :member AND pl.post= :post")
//    PostLike findByMemberAndPostWithLock(Member member, Post post);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = :post")
    long countByPostId(@Param("post") Post post);


    Long countAllByPost(Post post);

    PostLike findByMemberAndPost(Long member_id, Long post_id);
}
