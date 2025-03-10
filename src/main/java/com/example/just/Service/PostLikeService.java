package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostLike;
import com.example.just.Repository.PostLikeRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeService {
    @Autowired
    private PostLikeRepository postLikeRepository;

    public boolean addPostLikeIfExists(Long member_id, Long post_id) {
        Optional<PostLike> existingLike = Optional.ofNullable(postLikeRepository.findByMemberAndPost(member_id, post_id));
        if (existingLike.isPresent()) {
            postLikeRepository.deleteByMemberAndPost(member_id, post_id);
            return false;
        }

        postLikeRepository.saveMemberAndPost(member_id, post_id); // 비동기 처리
        return true;
    }
}
