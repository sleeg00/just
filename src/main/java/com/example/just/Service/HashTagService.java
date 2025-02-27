package com.example.just.Service;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Post;
import com.example.just.Document.HashTagDocument;
import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagMapRepository;
import com.example.just.Repository.HashTagRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HashTagService {
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private HashTagESRepository hashTagESRepository;
    @Autowired
    private HashTagMapRepository hashTagMapRepository;

    public void saveHashTag(List<String> hashTags, Post post) {
        List<HashTag> updatedTags = hashTags.stream()
                .map(tagName -> findTag(tagName)
                        .orElseGet(() -> {  // 없으면 새로 생성
                            HashTag newTag = new HashTag(tagName);
                            return newTag; // 여기서는 저장하지 않음!
                        }))
                .peek(tag -> tag.setTagCount(tag.getTagCount() + 1))  // 태그 카운트 증가
                .collect(Collectors.toList());

        hashTagRepository.saveAll(updatedTags);
        hashTagESRepository
                .saveAll(updatedTags.stream()
                        .map(HashTagDocument::new)
                        .collect(Collectors.toList())); // Stream은 1회성이라서

        // HashTagMap 저장
        List<HashTagMap> hashTagMaps = updatedTags.stream()
                .map(tag -> new HashTagMap(tag, post))
                .collect(Collectors.toList());
        hashTagMapRepository.saveAll(hashTagMaps);
    }

    private Optional<HashTag> findTag(String name) {
        return Optional.ofNullable(hashTagRepository.findByName(name));
    }

}
