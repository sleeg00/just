package com.example.just.Service;


import com.example.just.Dao.PostLike;
import com.example.just.Repository.PostLikeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private PostLikeRepository postLikeRepository;


    public boolean addPostLikeIfExists(Long member_id, Long post_id) {
        Optional<PostLike> existingLike = postLikeRepository.findByMemberAndPost(member_id, post_id);
        if (existingLike.isPresent()) {
            return false;
        }
        return true;
    }


    @Transactional
    public void bulkInsertPostLikes(Set<Pair<Long, Long>> likeSet) {
        if (likeSet.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO post_like (member_id, post_id) VALUES ";

        List<String> values = new ArrayList<>();
        for (Pair<Long, Long> pair : likeSet) {
            values.add("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
        }
        sql += String.join(", ", values);

        em.createNativeQuery(sql).executeUpdate();
    }


    @Transactional
    public void bulkDeletePostLikes(Set<Pair<Long, Long>> unlikeSet) {
        if (unlikeSet.isEmpty()) {
            return;
        }

        String sql = "DELETE FROM post_like WHERE (member_id, post_id) IN ";
        List<String> values = new ArrayList<>();

        for (Pair<Long, Long> pair : unlikeSet) {
            values.add("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
        }

        sql += "(" + String.join(", ", values) + ")";
        em.createNativeQuery(sql).executeUpdate();
    }

}
