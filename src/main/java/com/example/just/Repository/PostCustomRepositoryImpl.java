package com.example.just.Repository;

import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Dao.QPostContent;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;

    public PostCustomRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Tuple> findPostsByCursor(Long cursor, Long limit) {
        QPost post = QPost.post;
        QHashTagMap hashTagMaps = QHashTagMap.hashTagMap;
        QPostContent postContent = QPostContent.postContent;
        QHashTag hashTag = QHashTag.hashTag;

        return query.select(post, hashTagMaps, hashTag)
                .from(post)
                .leftJoin(post.postContent, postContent).fetchJoin()
                .leftJoin(hashTagMaps).on(hashTagMaps.post.post_id.eq(post.post_id)).fetchJoin()
                .leftJoin(hashTag).on(hashTag.id.eq(hashTagMaps.hashTag.id)).fetchJoin()
                .where(post.post_create_time.lt(cursor))
                .orderBy(post.post_create_time.desc())
                .limit(limit + 1)
                .fetch();
    }
}
