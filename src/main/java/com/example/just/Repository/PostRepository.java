package com.example.just.Repository;

import com.example.just.Dao.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    List<Post> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);

    @Query("SELECT COUNT(p) FROM Post p")
    long countAllPosts();
}
