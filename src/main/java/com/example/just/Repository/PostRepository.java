package com.example.just.Repository;

import com.example.just.Dao.Post;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Dao.QPostContent;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    List<Post> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);

    @Query("SELECT COUNT(p) FROM Post p")
    long countAllPosts();
}
