package com.example.just.Service;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QBlame;
import com.example.just.Dao.QComment;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Dao.QPostContent;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Exception.NotFoundException;
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
import java.sql.SQLException;
import java.util.function.Function;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
;
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
    private HashTagService hashTagService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String redisKey = "hashTag::";  // Redis Key Prefix

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    //@Transactional(readOnly = true)
    public Member checkMember(Long member_id) {
        System.out.println(
                "Transaction ReadOnly Second: " + TransactionSynchronizationManager.isCurrentTransactionReadOnly());
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

    public Post write(Member member, PostPostDto postDto) {
        Post post = new Post().writePost(postDto, member);
        postRepository.save(post);

        // 해시태그 존재시
        Optional.ofNullable(postDto.getHash_tag())
                .ifPresent(hashTags -> hashTagService.saveHashTag(hashTags, post));

        postContentESRespository.save(new PostDocument(post));
        return post;
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
        hashTagService.saveHashTag(postDto.getHash_tage(), p);

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


    public ResponseGetPost searchByCursor(Long cursor, Long limit, Long member_id)
            throws NotFoundException, SQLException { //글 조
        // Querydsl Impl 생성후 PostRepository 상속
        List<Tuple> posts = postRepository.findPostsByCursor(cursor, limit);

        boolean hasNext = posts.size() > limit;
        if (!posts.isEmpty() && hasNext) {
            posts.remove(posts.size() - 1);
        } else {
            throw new NoSuchElementException("마지막 페이지입니다.");
        }

        List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(posts, member_id);
        return resultPostIds(posts, getPostDtos, hasNext);
    }


    //@Cacheable(cacheNames = "hashTag", key = "#hash_ids")
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


    private ResponseGetPost resultPostIds(List<Tuple> results,
                                          List<ResponseGetMemberPostDto> getPostDtos, boolean hasNext) {
        // Slice 객체를 생성해서 반환합니다.
        ResponseGetPost responseGetPost = new ResponseGetPost(
                getPostDtos, hasNext);
        return responseGetPost;
    }

    private List<ResponseGetMemberPostDto> createResponseGetMemberPostDto(List<Tuple> results, Long member_id) {
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();

        for (Tuple tuple : results) {
            ResponseGetMemberPostDto dto = new ResponseGetMemberPostDto();
            Post post = tuple.get(QPost.post);
            HashTag hashTag = tuple.get(QHashTag.hashTag);
            dto.setPost_id(post.getPost_id());
            dto.setPost_content(post.getPostContent());
            dto.setPost_picture(post.getPost_picture());
            dto.setPost_create_time(post.getPost_create_time());
            dto.setBlamed_count(post.getBlamedCount());
            dto.setSecret(post.getSecret());
            dto.setPost_like_size(post.getPost_like());
            // 해시태그 정보 매핑 (hashTag가 존재하는 경우)
            if (hashTag != null) {
                dto.setHash_tag(hashTag.getName());
            }
            // 회원 ID 비교 (게시글 작성자와 현재 요청한 회원)
            if (member_id != -1) {
                dto.setMine(post.getMember().getId().equals(member_id));
            }
            getPostDtos.add(dto);
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
                .leftJoin(post.hashTagMaps, QHashTagMap.hashTagMap).fetchJoin()
                .leftJoin(hashTagMaps.hashTag, hashTag).fetchJoin()
                .leftJoin(post.postContent, postContent).fetchJoin()
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull(),
                        post.post_id.notIn(targetPostIds),
                        post.member.id.notIn(targetMemberIds),
                        QHashTagMap.hashTagMap.post.post_id.eq(post.post_id),
                        QHashTagMap.hashTagMap.hashTag.id.eq(hashTag.id),
                        postContent.post.post_id.eq(post.post_id))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit);

        List<Post> postsWithHashTags = postHashTagsQuery.fetch();

        List<Long> postIds = postsWithHashTags.stream()
                .map(Post::getPost_id)
                .collect(Collectors.toList());

        JPAQuery<Post> postCommentsQuery = query.select(post)
                .from(post)
                .leftJoin(post.comments, QComment.comment).fetchJoin()
                .where(post.post_id.in(postIds),
                        QComment.comment.post.post_id.in(postIds));
        List<Post> postsWithComments = postCommentsQuery.fetch();
        Map<Long, Post> postMap = postsWithHashTags.stream()
                .collect(Collectors.toMap(Post::getPost_id, Function.identity()));
        int i = 0;
        for (Post postMapValue : postMap.values()) {
            if (postsWithComments.size() == 0) {
                postMapValue.setComments(Collections.emptyList());
            } else {
                postMapValue.setComments(postsWithComments.get(i++).getComments());
            }
        }
        List<Tuple> results = null;

        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            HashMap<Long, String> map = null;
            getPostDtos = createResponseGetMemberPostDto(results, member_id);
            // 가져온 글들의 ID를 저장합니다.
            return resultPostIds(results, getPostDtos, true);
        }

    }

    public List<ResponseGetMemberPostDto> getMyPost(Long member_id) {
        List<Tuple> posts = postRepository.getMemberPost(member_id);
        // 조회된 결과가 없으면 예외 발생
        if (posts.isEmpty()) {
            throw new NotFoundException("해당 사용자의 게시글을 찾을 수 없습니다.");
        }
        List<ResponseGetMemberPostDto> getPostDtos;
        getPostDtos = createResponseGetMemberPostDto(posts, member_id);

        return getPostDtos;
    }


    public List<ResponseGetMemberPostDto> getLikeMemberPost(Long member_id) throws NotFoundException {
        Member member = checkMember(member_id); //존재한다면 객체 생성
        List<Post> results = member.getLikedPosts();
        // results를 최신 순으로 정렬
        Collections.sort(results, Comparator.comparing(Post::getPost_create_time).reversed());
        HashMap<Long, String> map = null;
        List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(null, member_id);
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
            String key = redisKey + hashTag.getId();

            // Redis에 저장 (name, tag_count)
            redisTemplate.opsForHash().put(key, "name", hashTag.getName());
            redisTemplate.opsForHash().put(key, "tag_count", hashTag.getTagCount().toString());
        }
    }
}