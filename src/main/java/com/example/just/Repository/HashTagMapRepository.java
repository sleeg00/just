package com.example.just.Repository;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Post;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagMapRepository extends JpaRepository<HashTagMap, Long> {
    void deleteByHashTagId(Long id);
    @EntityGraph(attributePaths = {"hashTag"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT htm FROM HashTagMap htm WHERE htm.post.id IN :postIds")
    List<HashTagMap> findAllByPostIds(@Param("postIds") List<Long> postIds);
}
