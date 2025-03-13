package com.example.just.Consumer;

import com.example.just.Service.PostLikeService;
import com.example.just.Service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.amqp.core.Message;
@Service
public class PostLikeConsumer implements ChannelAwareMessageListener {
    // Channel -> Interface -> RabbitMQ Message 처리하는 Channel을 다룰수 있게 해줌

    @Autowired
    private ObjectMapper objectMapper;  // Jackson ObjectMapper
    @Autowired
    private PostLikeService postLikeService;

    private static final int BATCH_SIZE = 50;
    private static final long TIMEOUT = 5000; // 타임아웃 5초 (5초 후에도 배치 처리를 강제로 실행)
    private final List<String> messageQueue = new ArrayList<>();
    private long lastMessageTime = System.currentTimeMillis(); // 마지막 메시지 처리 시간 기록

    @RabbitListener(queues = "postLikeQueue", ackMode = "MANUAL") // likeQueue 소비, ackMode 수동
    @Override // onMessage를 통해 메서지 수동 처리 ACK 및 NACK를 수동으로 보냄
    public void onMessage(Message message, Channel channel) throws IOException {
        try {
            String messageBody = new String(message.getBody());
            messageQueue.add(messageBody);

            // 타임아웃과 또는 배치 크기 조건을 만족하면 배치 처리
            if (messageQueue.size() >= BATCH_SIZE || (System.currentTimeMillis() - lastMessageTime) >= TIMEOUT) {
                processBatches();
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                lastMessageTime = System.currentTimeMillis(); // 메시지 처리 후 마지막 처리 시간을 업데이트
            }

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);  // 실패한 메시지를 다시 큐로 재전송
            // 실패 메시지를 DLQ로, "null" -> 추가적 속성 없음 보통 null, message.getBody() -> 메시지)
            channel.basicPublish("", "deadLetterQueue", null, message.getBody());
        }
    }

    private void processBatches() {
        List<String> batch = new ArrayList<>(messageQueue);
        messageQueue.clear(); // 배치 처리 후 큐 초기화

        Set<Pair<Long, Long>> likeSet = new HashSet<>();
        Set<Pair<Long, Long>> unlikeSet = new HashSet<>();

        for (String msg : batch) {
            try {
                // 메시지 파싱
                Map<String, Object> values = objectMapper.readValue(msg, Map.class);

                // 안전하게 값을 가져와 처리
                Long postId = getLongValue(values, "postId");
                Long memberId = getLongValue(values, "memberId");
                Boolean isLiked = getBooleanValue(values, "isLiked");

                Pair<Long, Long> likeEntry = Pair.of(memberId, postId);

                // 좋아요/싫어요 처리
                if (isLiked != null && isLiked) {
                    unlikeSet.remove(likeEntry);
                    likeSet.add(likeEntry);
                } else {
                    likeSet.remove(likeEntry);
                    unlikeSet.add(likeEntry);
                }

            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
                e.printStackTrace();  // 에러 로그 추가 (예: 디버깅 정보)
            }
        }

        postLikeService.bulkInsertPostLikes(likeSet);
        postLikeService.bulkDeletePostLikes(unlikeSet);
    }

    private Long getLongValue(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();  // Integer를 Long으로 변환
        } else if (value instanceof Long) {
            return (Long) value;  // 이미 Long이면 그대로 반환
        }
        return null;  // 값이 없으면 null 반환
    }

    // Map에서 안전하게 Boolean 값을 가져오는 방법
    private Boolean getBooleanValue(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);  // 문자열 "true"/"false"를 Boolean으로 변환
        }
        return null;  // 값이 없으면 null 반환
    }
}
