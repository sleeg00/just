package com.example.just.Repository.Querydsl.Template;

import com.example.just.Dao.Post;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Dao.QPostContent;

import com.example.just.Dto.PostCursor;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public abstract class AbstractPostQueryTemplate {

    protected final JPAQueryFactory query;

    public AbstractPostQueryTemplate(JPAQueryFactory query) {
        this.query = query;
    }

    public List<Tuple> execute(PostCursor postCursor) {
        QPost post = QPost.post;
        QPostContent postContent = QPostContent.postContent;
        QHashTagMap tagMap = QHashTagMap.hashTagMap;
        QHashTag tag = QHashTag.hashTag;

        JPAQuery<Tuple> q = query.select(post, tagMap, tag)
                .from(post)
                .leftJoin(post.postContent, postContent).fetchJoin()
                .leftJoin(tagMap).on(tagMap.post.post_id.eq(post.post_id)).fetchJoin()
                .leftJoin(tag).on(tag.id.eq(tagMap.hashTag.id)).fetchJoin();

        // 🔁 Hook: join 추가 (옵션)
        applyJoin(q, postCursor);

        // 🔁 Hook: where 조건 분기
        applyWhere(q, postCursor);

        // 🔁 Hook: 정렬 기준
        applyOrderBy(q);

        return q.limit(31).fetch();
    }

    // 🔁 선택적으로 추가 join
    protected void applyJoin(JPQLQuery<?> q, PostCursor postCursor) {}

    // 🔁 필수: where 조건
    protected abstract void applyWhere(JPQLQuery<?> q, PostCursor postCursor);

    // 🔁 필수: 정렬 기준
    protected abstract void applyOrderBy(JPQLQuery<?> q);
}
