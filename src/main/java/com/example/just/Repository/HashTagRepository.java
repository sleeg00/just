package com.example.just.Repository;

import com.example.just.Dao.HashTag;
<<<<<<< HEAD
import com.example.just.Dao.Post;
import java.util.List;
=======
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
<<<<<<< HEAD
    List<HashTag> findByPost(Post post_id);
=======
    HashTag findByName(String s);
    // List<HashTag> findByPost(Post post_id);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
}
