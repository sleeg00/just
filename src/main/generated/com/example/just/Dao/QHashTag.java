package com.example.just.Dao;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHashTag is a Querydsl query type for HashTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHashTag extends EntityPathBase<HashTag> {

    private static final long serialVersionUID = 918158047L;

<<<<<<< HEAD
    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHashTag hashTag = new QHashTag("hashTag");

=======
    public static final QHashTag hashTag = new QHashTag("hashTag");

    public final ListPath<HashTagMap, QHashTagMap> hashTagMaps = this.<HashTagMap, QHashTagMap>createList("hashTagMaps", HashTagMap.class, QHashTagMap.class, PathInits.DIRECT2);

>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

<<<<<<< HEAD
    public final QPost post;

    public QHashTag(String variable) {
        this(HashTag.class, forVariable(variable), INITS);
    }

    public QHashTag(Path<? extends HashTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHashTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHashTag(PathMetadata metadata, PathInits inits) {
        this(HashTag.class, metadata, inits);
    }

    public QHashTag(Class<? extends HashTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
=======
    public final NumberPath<Long> tagCount = createNumber("tagCount", Long.class);

    public QHashTag(String variable) {
        super(HashTag.class, forVariable(variable));
    }

    public QHashTag(Path<? extends HashTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHashTag(PathMetadata metadata) {
        super(HashTag.class, metadata);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    }

}

