package com.example.just.Service;


import static com.example.just.Dao.QComment.comment;
import static com.example.just.Dao.QHashTag.hashTag;
import static com.example.just.Dao.QHashTagMap.hashTagMap;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QBlame;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Dao.QPostContent;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Repository.BlameRepository;

import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagMapRepository;
import com.example.just.Repository.PostContentRepository;
import com.example.just.Response.ResponseGetMemberPostDto;
import com.example.just.Response.ResponsePutPostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
public class PostService {
    @Autowired
    private DataSource dataSource;
    private final EntityManager em;
    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory query;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private PostContentRepository postContentRepository;

    @Autowired
    private HashTagESRepository hashTagESRepository;
    @Autowired
    private BlameRepository blameRepository;
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private GptService gptService;

    @Autowired
    PostContentESRespository postContentESRespository;
    @Autowired
    private HashTagMapRepository hashTagMapRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String redisKey = "hashTag::";  // Redis Key Prefix

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    @Transactional(readOnly = true)
    public Member checkMember(Long member_id) {
         System.out.println("Transaction ReadOnly Second: " + TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get();   //존재한다면 객체 생성
        return member;
    }

    private Post checkPost(Long post_id) throws NotFoundException {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NotFoundException();
        }
        Post post = optionalPost.get();
        return post;
    }

    @Transactional(readOnly = false)
    public PostPostDto write(Member member, PostPostDto postDto) {    //글 작성
        // System.out.println("Transaction ReadOnly Second: " + TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Post post = new Post();

        post.writePost(postDto, member);
        Post p = postRepository.save(post);

        List<String> hashTags = postDto.getHash_tag();
        saveHashTag(hashTags, p);

        PostDocument postDocument = new PostDocument(p);
        postContentESRespository.save(new PostDocument(p));
        return postDto;
    }

    private void saveHashTag(List<String> hashTags, Post p) { // Redis
        for (int i = 0; i < hashTags.size(); i++) {
            HashTag hashTag = findTag(hashTags, i);
            HashTagMap hashTagMap = new HashTagMap();
            if (hashTag == null) {
                HashTag newHashTag = new HashTag(hashTags.get(i));
                newHashTag.setTagCount(1L);
                newHashTag = hashTagRepository.save(newHashTag);
                hashTagESRepository.save(new HashTagDocument(newHashTag));
                hashTagMap = new HashTagMap(newHashTag, p); //객체 그래프 설정
            } else {
                hashTag.setTagCount(hashTag.getTagCount() + 1);
                hashTagRepository.save(hashTag);
                hashTagESRepository.save(new HashTagDocument(hashTag));
                hashTagMap = new HashTagMap(hashTag, p); //객체 그래프 설정
            }
            hashTagMapRepository.save(hashTagMap);
        }
    }

    @Transactional(readOnly = false)
    private HashTag findTag(List<String> hashTags, int i) {
        return hashTagRepository.findByName(hashTags.get(i));
    }


    //글 삭제
    public void deletePost(Long post_id) throws NotFoundException {
        Post post = checkPost(post_id);
        if (post == null) {
            throw new NotFoundException();
        } else {
            // Elasticsearch에서 해당 포스트의 내용 삭제
            postContentESRespository.deleteById(post_id);
            deleteHashTag(post);
            postRepository.deleteById(post_id);
        }
    }

    //글 수정
    public ResponsePutPostDto putPost(Long member_id, PutPostDto postDto) throws NotFoundException {
        Long post_id = postDto.getPost_id();
        Member member = checkMember(member_id);
        Post checkPost = checkPost(post_id);
        List<HashTagMap> hashTagMaps = checkPost.getHashTagMaps();

        deleteHashTag(checkPost);

        postDto.setPost_content(postDto.getPost_content());

        checkPost.changePost(postDto, member, checkPost);

        Post p = postRepository.save(checkPost);
        saveHashTag(postDto.getHash_tage(), p);

        postContentESRespository.save(new PostDocument(checkPost));

        ResponsePutPostDto responsePutPostDto = new ResponsePutPostDto(p);
        return responsePutPostDto;
    }

    private void deleteHashTag(Post post) {
        List<HashTagMap> hashTagMaps = post.getHashTagMaps();
        for (int i = 0; i < hashTagMaps.size(); i++) {
            hashTagRepository.findById(hashTagMaps.get(i).getHashTag().getId())
                    .ifPresent(
                            hashTag -> {
                                if (hashTag.getTagCount() != 1) {
                                    hashTag.setTagCount(hashTag.getTagCount() - 1);
                                    hashTagESRepository.save(new HashTagDocument(hashTag));
                                    hashTagRepository.save(hashTag);
                                } else {
                                    hashTagESRepository.deleteById(hashTag.getId());
                                    hashTagRepository.deleteById(hashTag.getId());
                                }
                            });
        }
    }

    public List<Post> getAllPostList() {
        return postRepository.findAll();
    }



    public ResponseGetPost searchByCursor(Long limit, Long member_id) throws NotFoundException, SQLException { //글 조

        QPost post = QPost.post;
        QHashTagMap hashTagMaps = QHashTagMap.hashTagMap;
        QPostContent postContent = QPostContent.postContent;
        Set<Long> viewedPostIds = new HashSet<>();

        JPAQuery<Post> postQuery = query.select(post)
                .from(post)
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())  // 랜덤 정렬
                .limit(limit);

        JPAQuery<Post> postContentQuery = postQuery
                .select(post)
                .from(post)
                .leftJoin(post.postContent, postContent)
                .where(post.post_id.eq(postContent.post.post_id))
                .fetchJoin();

        List<Post> postsWithContent = postContentQuery.fetch();

        List<Long> postIds = postsWithContent.stream()
                .map(Post::getPost_id)
                .collect(Collectors.toList());

        JPAQuery<HashTagMap> hashTagQuery = query.select(hashTagMaps)
                .select(hashTagMaps)
                .from(hashTagMaps)
                .where(hashTagMaps.post.post_id.in(postIds));

        List<HashTagMap> hashTagMaps1 = hashTagQuery.fetch();
        List<Post> postsWithHashTags2 = postsWithContent.stream()
                .map(post3 -> {
                    post3.setHashTagMaps(hashTagMaps1.stream()
                            .filter(hashTagMap -> hashTagMap.getPost().getPost_id().equals(post3.getPost_id()))
                            .collect(Collectors.toList()));
                    return post3;
                })
                .collect(Collectors.toList());

        List<Long> hash = hashTagMaps1.stream()
                .map(hashTagMap -> hashTagMap.getHashTag().getId())
                .collect(Collectors.toList());



        List<String> hashTagNames = getRedisHashTags(hash);


        Map<Long, Post> postMap = postsWithHashTags2.stream()
                .collect(Collectors.toMap(Post::getPost_id, Function.identity()));

        List<Post> results = new ArrayList<>(postMap.values());
        //System.out.println("Transaction ReadOnly Second: " + TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(results, hashTagNames, member_id);
            return resultPostIds(viewedPostIds, results, getPostDtos);
        }
    }


    @Cacheable(cacheNames = "hashTag", key = "#hash_ids")
    public List<String> getRedisHashTags(List<Long> hash_ids) {
        List<Long> cachedHashTags = new ArrayList<>();
        List<String> hashTagNames = new ArrayList<>();
        // 캐시에서 데이터를 찾고 찾은 데이터를 리스트에 추가
        for (Long hashTagId : hash_ids) {
            String redisKey2 = "hashTag::" + hashTagId;  // Redis에서 사용하는 Key

            // Redis에서 Hash 값을 조회
            Map<Object, Object> cachedData = redisTemplate.opsForHash().entries(redisKey2);

            if (!cachedData.isEmpty()) {
                String name = (String) cachedData.get("name");  // "name" 필드 값 조회

                hashTagNames.add(name);
                cachedHashTags.add(hashTagId);
            }
        }

        // 캐시에서 조회되지 않은 hashTagId들 DB에서 조회하여 처리
        List<Long> missingHashIds = new ArrayList<>(hash_ids);

        for (Long id : cachedHashTags) {
            missingHashIds.remove(id);  // 캐시에서 찾은 hashTagId 제거
        }


        return hashTagNames;
    }



    private ResponseGetPost resultPostIds(Set<Long> viewedPostIds, List<Post> results,
                                          List<ResponseGetMemberPostDto> getPostDtos) {

        boolean hasNext = true;
        // Slice 객체를 생성해서 반환합니다.
        ResponseGetPost responseGetPost = new ResponseGetPost(
                getPostDtos, hasNext);
        return responseGetPost;
    }

    private List<ResponseGetMemberPostDto> createResponseGetMemberPostDto(List<Post> results, List<String> hashTagNames,
                                                                          Long member_id) {
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();

        for (Post post : results) {

            // ResponseGetMemberPostDto 생성 및 필드 세팅
            ResponseGetMemberPostDto responseGetMemberPostDto = new ResponseGetMemberPostDto();
            responseGetMemberPostDto.setPost_id(post.getPost_id());
            responseGetMemberPostDto.setPost_content(post.getPostContent());
            responseGetMemberPostDto.setPost_picture(post.getPost_picture());
            responseGetMemberPostDto.setHash_tag(hashTagNames);
            responseGetMemberPostDto.setPost_create_time(post.getPost_create_time());
            responseGetMemberPostDto.setBlamed_count(Math.toIntExact(post.getBlamedCount()));
            responseGetMemberPostDto.setSecret(post.getSecret());
            responseGetMemberPostDto.setPost_like_size(post.getPost_like());

            if (member_id != -1) {
                responseGetMemberPostDto.setMine(post.getMember().getId().equals(member_id));
            }

            getPostDtos.add(responseGetMemberPostDto);
        }

        return getPostDtos;
    }


    public Long blamePost(Long post_id) throws NotFoundException {
        Post post = checkPost(post_id);
        post.setBlamedCount(post.getBlamedCount() + 1);
        postRepository.save(post);
        return post_id;
    }

    public int blameGetPost(Long postId) throws NotFoundException {
        Post post = checkPost(postId);
        return Math.toIntExact(post.getBlamedCount());

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> postLikes(Long post_id, Long member_id) throws NotFoundException {    //글 좋아요

        Member member = checkMember(member_id);
        Post post = checkPost(post_id);

        ResponsePost responsePost;
        PostDocument postDocument = postContentESRespository.findById(post_id).get();
        System.out.println("!");
        if (post.getLikedMembers().contains(member)) {
            System.out.println("?");
            post.removeLike(member);
            System.out.println("remove");
            postDocument.setPostLikeSize(postDocument.getPostLikeSize() - 1);
            responsePost = new ResponsePost(post_id, "좋아요 취소");
        } else {
            System.out.println("?");
            post.addLike(member);
            System.out.println("yes");
            postDocument.setPostLikeSize(postDocument.getPostLikeSize() + 1);
            responsePost = new ResponsePost(post_id, "좋아요 완료");
        }
        postContentESRespository.save(postDocument);
        Post savePost = postRepository.save(post);

        return ResponseEntity.ok(responsePost);
    }

    public ResponseGetPost searchByCursorMember(String cursor, Long limit, Long member_id) throws NotFoundException {
        QPost post = QPost.post;
        QBlame blame = QBlame.blame;
        QHashTagMap hashTagMaps = QHashTagMap.hashTagMap;
        QHashTag hashTag = QHashTag.hashTag;
        QPostContent postContent = QPostContent.postContent;
        Set<Long> viewedPostIds = new HashSet<>();
        // 이전에 본 글들의 ID를 가져옵니다.
        if (cursor != null) {
            String[] viewedPostIdsArray = cursor.split(",");
            viewedPostIds = new HashSet<>();
            for (String viewedPostId : viewedPostIdsArray) {
                viewedPostIds.add(Long.parseLong(viewedPostId.trim()));
            }
        }

        Member realMember = checkMember(member_id);

        List<Tuple> blames = query.select(blame.targetPostId, blame.targetMemberId)
                .from(blame)
                .where(blame.blameMemberId.eq(realMember.getId()))
                .fetch();
        // 결과를 가져와서 리스트로 변환
        List<Long> targetPostIds = new ArrayList<>();
        List<Long> targetMemberIds = new ArrayList<>();
        for (Tuple tuple : blames) {
            if (tuple.get(blame.targetPostId) == null) {
                targetPostIds.add(tuple.get(blame.targetPostId));
            }
            if (tuple.get(blame.targetMemberId) == null) {
                targetMemberIds.add(tuple.get(blame.targetMemberId));
            }
        }

        JPAQuery<Post> postHashTagsQuery = query.select(post)
                .from(post)
                .leftJoin(post.hashTagMaps, hashTagMap).fetchJoin()
                .leftJoin(hashTagMaps.hashTag, hashTag).fetchJoin()
                .leftJoin(post.postContent, postContent).fetchJoin()
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull(),
                        post.post_id.notIn(targetPostIds),
                        post.member.id.notIn(targetMemberIds),
                        hashTagMap.post.post_id.eq(post.post_id),
                        hashTagMap.hashTag.id.eq(hashTag.id),
                        postContent.post.post_id.eq(post.post_id))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit);
        System.out.println("why?");
        List<Post> postsWithHashTags = postHashTagsQuery.fetch();
        System.out.println("?");

        System.out.println("not?");
        List<Long> postIds = postsWithHashTags.stream()
                .map(Post::getPost_id)
                .collect(Collectors.toList());

        JPAQuery<Post> postCommentsQuery = query.select(post)
                .from(post)
                .leftJoin(post.comments, comment).fetchJoin()
                .where(post.post_id.in(postIds),
                        comment.post.post_id.in(postIds));
        System.out.println("Innner?");
        List<Post> postsWithComments = postCommentsQuery.fetch();
        Map<Long, Post> postMap = postsWithHashTags.stream()
                .collect(Collectors.toMap(Post::getPost_id, Function.identity()));
        System.out.println("here?");
        int i = 0;
        for (Post postMapValue : postMap.values()) {
            if (postsWithComments.size() == 0) {
                postMapValue.setComments(Collections.emptyList());
            } else {
                postMapValue.setComments(postsWithComments.get(i++).getComments());
            }
        }
        System.out.println("Here?");
        List<Post> results = new ArrayList<>(postMap.values());

        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            getPostDtos = createResponseGetMemberPostDto(results, new ArrayList<>(), member_id);
            // 가져온 글들의 ID를 저장합니다.
            System.out.println("Where?");
            return resultPostIds(viewedPostIds, results, getPostDtos);
        }

    }

    public List<ResponseGetMemberPostDto> getMyPost(Long member_id) throws NotFoundException {

        List<Post> posts = postRepository.findByMemberId(member_id);
        List<Post> results = posts.stream()
                .sorted(Comparator.comparing(Post::getPost_create_time).reversed())
                .collect(Collectors.toList());
        Map<Long, Post> postMap = results.stream()
                .collect(Collectors.toMap(Post::getPost_id, post -> post));
        List<Long> postIds = postMap.keySet().stream()
                .collect(Collectors.toList());
        List<HashTagMap> hashTagMaps = hashTagMapRepository.findAllByPostIds(postIds);

        hashTagMaps.forEach(hashTagMap -> {
            Post correspondingPost = postMap.get((hashTagMap.getPost().getPost_id()));
            if (correspondingPost != null) {
                correspondingPost.setHashTagMaps(Collections.singletonList(hashTagMap));
            }
        });

        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            getPostDtos = createResponseGetMemberPostDto(results, new ArrayList<>(), member_id);
        }
        return getPostDtos;
    }

    public List<ResponseGetMemberPostDto> getLikeMemberPost(Long member_id) throws NotFoundException {
        Member member = checkMember(member_id); //존재한다면 객체 생성
        List<Post> results = member.getLikedPosts();
        // results를 최신 순으로 정렬
        Collections.sort(results, Comparator.comparing(Post::getPost_create_time).reversed());
        List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(results,new ArrayList<>(), member_id);
        return getPostDtos;
    }


    public String parsingJson(String json) {
        String response;
        try {
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(json);
            response = elem.get("convertedQuestion").toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public void saveAllHashTagsToRedis() {
        // MySQL에서 모든 HashTag 데이터를 조회
        List<HashTag> hashTags = hashTagRepository.findAll();

        for (HashTag hashTag : hashTags) {
            // Redis Key는 hash_tag_id로 설정
            String key =  redisKey + hashTag.getId();

            // Redis에 저장 (name, tag_count)
            redisTemplate.opsForHash().put(key, "name", hashTag.getName());
            redisTemplate.opsForHash().put(key, "tag_count", hashTag.getTagCount().toString());
        }
    }
}