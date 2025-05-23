package com.example.just.Repository.Querydsl.Strategy;


import com.example.just.Dao.QPost;
import com.example.just.Dto.PostCursor;
import com.example.just.Repository.Querydsl.Template.AbstractPostQueryTemplate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

@Component("recent")
public class SelectRecentPostQuery extends AbstractPostQueryTemplate {

    public SelectRecentPostQuery(JPAQueryFactory query) {
        super(query);
    }

    @Override
    protected void applyWhere(JPQLQuery<?> q, PostCursor postCursor) {
        Long timeCursor = postCursor.getTimeCursor();
        Long idCursor = postCursor.getPostIdCursor();

        if (timeCursor != 0 && idCursor != 0) {
            q.where(QPost.post.post_create_time.lt(timeCursor)
                    .or(QPost.post.post_create_time.eq(timeCursor)
                            .and(QPost.post.post_id.lt(idCursor))));
        }
    }

    @Override
    protected void applyOrderBy(JPQLQuery<?> q) {
        QPost post = QPost.post;
        q.orderBy(post.post_create_time.desc(), post.post_id.desc());
    }

    @Override
    protected void applyJoin(JPQLQuery<?> q, PostCursor postCursor) {

    }
}
