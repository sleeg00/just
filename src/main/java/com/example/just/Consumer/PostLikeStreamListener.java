package com.example.just.Consumer;

import static net.minidev.asm.DefaultConverter.convertToLong;

import com.example.just.Service.PostLikeService;
import com.example.just.Util.RedisKeyUtil;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroup;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroups;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Service
public class PostLikeStreamListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostLikeService postLikeService;
    private final RedisKeyUtil redisKeyUtil;
    private final BlockingQueue<MapRecord<String, Object, Object>> messageQueue;
    private final ExecutorService executorService;
    private static final int BATCH_SIZE = 50;
    private static final int TIMEOUT = 2; // 최대 2초 대기

    public PostLikeStreamListener(RedisTemplate<String, Object> redisTemplate,
                                  PostLikeService postLikeService,
                                  RedisKeyUtil redisKeyUtil) {
        this.redisTemplate = redisTemplate;
        this.postLikeService = postLikeService;
        this.redisKeyUtil = redisKeyUtil;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newSingleThreadExecutor();
        initStreamListener();
    }

    private void initStreamListener() {
        String streamName = redisKeyUtil.getPostLikeStreamName();
        String groupName = redisKeyUtil.getPostLikeGroup();

        // ✅ 스트림과 그룹을 생성하는 로직 추가
        createStreamAndGroupIfNotExist(streamName, groupName);

        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(redisTemplate.getConnectionFactory(), options);

        // ✅ Consumer 그룹을 사용하여 메시지 수신
        container.receive(Consumer.from(groupName, "consumer-1"), StreamOffset.create(streamName, ReadOffset.from(">")),
                this::queueMessage);
        container.start();

        // ✅ 50개씩 모아서 처리하는 별도 스레드 실행
        executorService.submit(this::processBatches);
    }

    /**
     * 스트림과 컨슈머 그룹이 없으면 생성하는 메서드
     */
    private void createStreamAndGroupIfNotExist(String streamName, String groupName) {
        try {
            // ✅ 1. 스트림 존재 여부 확인
            Boolean streamExists = redisTemplate.hasKey(streamName);
            List<XInfoGroup> groups = new ArrayList<>();
            if (Boolean.TRUE.equals(streamExists)) {

               groups = redisTemplate.execute((RedisCallback<XInfoGroups>) connection -> {
                    try {
                        return connection.streamCommands().xInfoGroups(streamName.getBytes());
                    } catch (Exception e) {
                        return null; // 스트림이 없을 경우 null 반환
                    }
                }).toList();
            }

            if (groups == null || groups.isEmpty()) {
                // ✅ 2-1. 컨슈머 그룹이 없으면 생성
                redisTemplate.execute((RedisCallback<Void>) connection -> {
                    try {
                        connection.streamCommands().xGroupCreate(
                                streamName.getBytes(), groupName, ReadOffset.from("0"), true
                        );
                        System.out.println("✅ Redis Consumer Group 생성됨: " + groupName);
                    } catch (Exception e) {
                        System.err.println("❌ 컨슈머 그룹 생성 중 오류 발생: " + e.getMessage());
                    }
                    return null;
                });
            }
        } catch (Exception e) {
            System.err.println("❌ Stream/Group 생성 중 오류 발생: " + e.getMessage());
        }
    }


    private void queueMessage(MapRecord<String, String, String> message) {
        try {
            // String → Object로 변환하여 저장
            messageQueue.put((MapRecord<String, Object, Object>) (Object) message);
            if (messageQueue.size() >= BATCH_SIZE) {
                processBatches();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void processBatches() {

        try {
            List<MapRecord<String, Object, Object>> batch = new ArrayList<>();
            batch.add(messageQueue.take()); // ✅ 최소 1개는 기다렸다가 받음

            // ✅ 50개가 되거나 2초가 지나면 배치 실행
            long startTime = System.currentTimeMillis();
            while (batch.size() < BATCH_SIZE && (System.currentTimeMillis() - startTime) < 10 * 1000) {
                MapRecord<String, Object, Object> message = messageQueue.poll(500, TimeUnit.MILLISECONDS);
                if (message != null) {
                    batch.add(message);
                }
            }

            // ✅ 50개를 모았거나 2초가 지나면 처리
            if (!batch.isEmpty()) {
                processBatch(batch);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(List<MapRecord<String, Object, Object>> messages) {
        Set<Pair<Long, Long>> likeSet = new HashSet<>();
        Set<Pair<Long, Long>> unlikeSet = new HashSet<>();
        List<RecordId> ackIds = new ArrayList<>(); // ✅ RecordId 타입으로 변경
        for (MapRecord<String, Object, Object> message : messages) {
            Map<Object, Object> values = message.getValue();
            System.out.println(values);
            Long postId =  Long.parseLong((String) values.get("postId"));
            Long memberId = Long.parseLong ((String) values.get("memberId"));
            Boolean isLiked = Boolean.parseBoolean ((String) values.get("isLiked"));

            Pair<Long, Long> likeEntry = Pair.of(memberId, postId);
            System.out.println(isLiked);
            if (isLiked) {
                unlikeSet.remove(likeEntry);
                likeSet.add(likeEntry);
            } else {
                likeSet.remove(likeEntry);
                unlikeSet.add(likeEntry);
            }

            ackIds.add(message.getId()); // ✅ RecordId 추가
        }

        // ✅ 명확한 RecordId 타입 지정
        if (!ackIds.isEmpty()) {
            System.out.println(likeSet.size() + " 좋아요 ? ");
            System.out.println(unlikeSet.size() + " 싫어요 ? ");
            System.out.println("ACK");
            redisTemplate.opsForStream()
                    .acknowledge(redisKeyUtil.getPostLikeGroup(), redisKeyUtil.getPostLikeStreamName(),
                            ackIds.toArray(new RecordId[0])); // ✅ `RecordId[]` 명확히 지정
            postLikeService.bulkInsertPostLikes(likeSet);
            postLikeService.bulkDeletePostLikes(unlikeSet);
        }
    }
}
