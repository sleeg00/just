package com.example.just.Repository.Querydsl.Strategy;

import com.example.just.Dao.QPost;
import com.example.just.Dto.PostCursor;
import com.example.just.Repository.Querydsl.Template.AbstractPostQueryTemplate;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

@Component("like")
public class SelectLikePostQuery extends AbstractPostQueryTemplate {

    public SelectLikePostQuery(JPAQueryFactory query) {
        super(query);
    }

    @Override
    protected void applyWhere(JPQLQuery<?> q, PostCursor cursor) {
        Long likeCursor = cursor.getLikeCursor();
        Long idCursor = cursor.getPostIdCursor();

        if (likeCursor != 0 && idCursor != 0) {
            q.where(QPost.post.post_like.lt(likeCursor)
                    .or(QPost.post.post_like.eq(likeCursor)
                            .and(QPost.post.post_id.lt(idCursor))));
        }
    }

    @Override
    protected void applyOrderBy(JPQLQuery<?> q) {
        q.orderBy(QPost.post.post_like.desc(), QPost.post.post_id.desc());
    }
}
