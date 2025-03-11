package com.example.just.Consumer;

import static net.minidev.asm.DefaultConverter.convertToLong;

import com.example.just.Service.PostLikeService;
import com.example.just.Util.RedisKeyUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikeConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private RedisKeyUtil redisKeyUtil;

    @PostConstruct
    public void init() {
        try {
            String streamName = redisKeyUtil.getPostLikeStreamName();
            String groupName = redisKeyUtil.getPostLikeGroup();
            redisTemplate.opsForStream().createGroup(streamName, groupName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000) // 1초마다 실행
    public void processLikes() {
        System.out.println("배치 시작");
        String streamName = redisKeyUtil.getPostLikeStreamName();
        String groupName = redisKeyUtil.getPostLikeGroup();

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                .read(
                        Consumer.from(groupName, "consumer-1"),
                        StreamReadOptions.empty().count(50),
                        StreamOffset.create(streamName, ReadOffset.lastConsumed())
                );

        if (messages == null || messages.isEmpty()) {
            return;
        }

        Set<Pair<Long, Long>> likeSet = new HashSet<>();
        Set<Pair<Long, Long>> unlikeSet = new HashSet<>();

        for (MapRecord<String, Object, Object> message : messages) {
            Map<Object, Object> values = message.getValue();

            Long postId = convertToLong(values.get("postId"));
            Long memberId = convertToLong(values.get("memberId"));
            Boolean isLiked = convertToBoolean(values.get("isLiked"));
            System.out.println(postId + " " + memberId + " " + isLiked);
            Pair<Long, Long> likeEntry = Pair.of(memberId, postId);

            if (isLiked) {
                unlikeSet.remove(likeEntry);
                likeSet.add(likeEntry);
            } else {
                likeSet.remove(likeEntry);
                unlikeSet.add(likeEntry);
            }

            // 메시지 ACK 처리 (읽은 메시지 삭제)
            redisTemplate.opsForStream().acknowledge(groupName, message);
        }

        postLikeService.bulkInsertPostLikes(likeSet);
        postLikeService.bulkDeletePostLikes(unlikeSet);

    }
    private Boolean convertToBoolean(Object value) {
        if (value == null) {
            return false; // 기본값 설정 가능
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

}
