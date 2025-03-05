package com.example.just.Repository;

import com.example.just.Dao.Post;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    List<Post> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);

    @Query("SELECT COUNT(p) FROM Post p")
    long countAllPosts();


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :post_id") // 비관전 락
    Optional<Post> findByIdWithLock(@Param("post_id") Long post_id);

}
