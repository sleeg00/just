package com.example.just.Document;

<<<<<<< HEAD
=======
import com.example.just.Dao.HashTag;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import com.example.just.Dao.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.format.annotation.DateTimeFormat;

<<<<<<< HEAD
@Document(indexName = "items")
=======
@Document(indexName = "posts")
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Mapping(mappingPath = "elastic/post-mapping.json")
@Setting(settingPath = "elastic/post-setting.json")
public class PostDocument {
    @Id
    private Long id;

<<<<<<< HEAD
    @Field(type = FieldType.Nested)
    private List<ContentArray> postContent;
=======
    @Field(type = FieldType.Text)
    private List<String> postContent;

    @Field(type = FieldType.Text)
    private List<String> hashTag;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

    @Field(type = FieldType.Long)
    private Long postPicture;

    @Field(type = FieldType.Long)
    private Date postCreateTime;

<<<<<<< HEAD
    @Field(type = FieldType.Long)
    private Long postLike;

    @Field(type = FieldType.Text)
    private String emoticon;
=======
    @Field(type = FieldType.Boolean)
    private Boolean secret;

    @Field(type = FieldType.Long)
    private Long commentSize;

    @Field(type = FieldType.Long)
    private Long postLikeSize;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

    @Field(type = FieldType.Long)
    private Long blamedCount;

<<<<<<< HEAD
    @Field(type = FieldType.Text)
    private String memberNickname;

    @Field(type = FieldType.Long)
    private Long memberId;

    @Field(type = FieldType.Integer)
    private Integer commentCount;
=======
    @Field(type = FieldType.Long)
    private Long memberId;


>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

//    //글 태그
//    @Field(name = "hash_tag", type = FieldType.Nested)
//    private List<HashTag> hash_tag;
//
//
//    @Field(type = FieldType.Nested)
//    private List<Member> likedMembers = new ArrayList<>();
//
//    @Field(type = FieldType.Object)
//    private Member member;
//
//    @Field(type = FieldType.Nested)
//    private List<Comment> comments;


    public PostDocument(Post post) {
        this.id = post.getPost_id();
<<<<<<< HEAD
        this.postContent = post.getPostContent().stream()
                .map(ContentArray::new)
                .collect(Collectors.toList());
        this.postPicture = post.getPost_picture();
        this.postCreateTime = post.getPost_create_time();
        this.postLike = post.getPost_like();
        this.emoticon = post.getEmoticon();
        this.blamedCount = post.getBlamedCount();
        this.memberNickname = post.getMember().getNickname();
        this.memberId = post.getMember().getId();
        this.commentCount = post.getComments().size();

=======
        this.postContent = post.getPostContent();
        this.hashTag = post.getHashTagMaps().stream()
                        .map(hashTagMap -> hashTagMap.getHashTag().getName())
                        .collect(Collectors.toList());
        this.postPicture = post.getPost_picture();
        this.postCreateTime = post.getPost_create_time();
        this.secret = post.getSecret();
        this.commentSize = (long) post.getComments().size();
        this.postLikeSize = post.getPost_like();
        this.blamedCount = post.getBlamedCount();
        this.memberId = post.getMember().getId();
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
//        this.hash_tag = post.getHash_tag();
//        this.likedMembers = post.getLikedMembers();
//        this.member = post.getMember();
//        this.comments = post.getComments();
    }
}
