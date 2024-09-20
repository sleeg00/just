package com.example.just.Repository;

import com.example.just.Dao.Post;
import javax.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);


    @EntityGraph(attributePaths = {"comments", "member"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Post> findByMemberId(Long member_id);

    @Query("SELECT COUNT(p) FROM Post p")
    long countAllPosts();
}
