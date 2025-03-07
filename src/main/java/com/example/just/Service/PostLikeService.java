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

    public boolean addPostLikeIfNotExists(Member member, Post post) {
        Optional<PostLike> existingLike = Optional.ofNullable(postLikeRepository.findByMemberAndPost(member, post));
        if (existingLike.isPresent()) {
            return false;
        }

        postLikeRepository.save(new PostLike(member, post));
        return true;
    }
}
