package com.example.just.Repository;

import com.querydsl.core.Tuple;
import java.util.List;

public interface PostCustomRepository {
    List<Tuple> findPostsByCursor(Long cursor, Long limit);

    List<Tuple> getMemberPost(Long member_id);
}
