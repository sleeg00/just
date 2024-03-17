package com.example.just.Service;

<<<<<<< HEAD
import com.example.just.Dao.Post;
import com.example.just.Document.PostDocument;
=======
import com.example.just.Dao.HashTag;
import com.example.just.Dao.Post;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagRepository;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
<<<<<<< HEAD
    private PostContentESRespository postContentESRespository;

=======
    private HashTagRepository hashTagRepository;

    @Autowired
    private PostContentESRespository postContentESRespository;

    @Autowired
    private HashTagESRepository hashTagESRepository;

>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    public void migrationDB(){
        List<Post> dbPosts = postRepository.findAll();
        List<PostDocument> postDocuments = dbPosts.stream()
                .map(PostDocument::new)
                .collect(Collectors.toList());
        postContentESRespository.saveAll(postDocuments);
<<<<<<< HEAD
=======
        List<HashTag> dbTag = hashTagRepository.findAll();
        List<HashTagDocument> hashTagDocuments = dbTag.stream()
                .map(HashTagDocument::new)
                .collect(Collectors.toList());
        hashTagESRepository.saveAll(hashTagDocuments);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    }
}
