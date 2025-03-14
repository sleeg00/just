package com.example.just.Producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;  // Jackson ObjectMapper

    public void sendPostLikeMessage(Long postId, Long memberId, Boolean isLiked) throws Exception {
        // 메시지 객체 생성
        Map<String, Object> message = new HashMap<>();
        message.put("postId", postId);
        message.put("memberId", memberId);
        message.put("isLiked", isLiked);

        // 객체를 JSON 문자열로 변환
        String jsonMessage = objectMapper.writeValueAsString(message);

        // RabbitMQ로 전송 (likeExchange와 likeRoutingKey 설정)
        amqpTemplate.convertAndSend("postLikeExchange", "postLikeRoutingKey", jsonMessage);
    }
    public void sendPostLikeCountMessage(Long postId, Long postLikeCount) throws Exception {
        // 메시지 객체 생성
        Map<String, Object> message = new HashMap<>();
        message.put("postId", postId);
        message.put("postLikeCount", postLikeCount);

        // 객체를 JSON 문자열로 변환
        String jsonMessage = objectMapper.writeValueAsString(message);

        // RabbitMQ로 전송 (likeExchange와 likeRoutingKey 설정)
        amqpTemplate.convertAndSend("postLikeExchange", "postLikeCountRoutingKey", jsonMessage);
    }
}
