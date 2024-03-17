package com.example.just.Repository;
import com.example.just.Document.PostDocument;
import org.elasticsearch.search.SearchHits;
import org.h2.mvstore.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostContentESRespository extends ElasticsearchRepository<PostDocument,Long>,
        CrudRepository<PostDocument,Long> {
<<<<<<< HEAD
    List<PostDocument> findByPostContent_ContentContains(String text);

=======
//    List<PostDocument> findByPostContent_ContentContains(String text);
    List<PostDocument> findByPostContentContaining(String text);

    List<PostDocument> findByHashTagIn(String text);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

}
