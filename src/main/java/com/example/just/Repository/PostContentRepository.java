package com.example.just.Repository;

import com.example.just.Dao.PostContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostContentRepository extends JpaRepository<PostContent, Long> {
}
