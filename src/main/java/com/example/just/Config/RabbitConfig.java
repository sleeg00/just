package com.example.just.Config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 원본 큐 (좋아요 메시지 처리용)
    @Bean
    public Queue postLikeQueue() {
        return QueueBuilder.durable("postLikeQueue") // 내구성 큐로 생성 (큐 저장된 메시지 삭제 X, 유지)
                .withArgument("x-dead-letter-exchange", "") // 기본 교환기로 DLQ(실패하면 여기로 보냄)로 메시지 이동
                .withArgument("x-dead-letter-routing-key", "deadLetterRoutingKey") // DLQ 라우팅 키
                // 실패 메시지가 DLQ로 보내질 때 사용할 라우터 키
                .build();
    }

    // 실패한 메시지를 전송할 DLQ (Dead Letter Queue)
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("deadLetterQueue", true); // true- > 내구성 큐로 생성
    }

    // 기본 교환기
    @Bean
    public DirectExchange postLikeExchange() {
        return new DirectExchange("postLikeExchange"); // 라우팅 키, 메시지를 특정 큐로 전달
    }

    // 큐와 교환기 연결 (선이라고 생각)
    @Bean
    public Binding bindingPostLikeQueue(Queue postLikeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(postLikeQueue).to(exchange).with("postLikeRoutingKey");
    }


    // DLQ와 교환기 연결 (선이 2개지요!)
    @Bean
    public Binding bindingDeadLetterQueue(Queue deadLetterQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deadLetterQueue).to(exchange).with("deadLetterRoutingKey");
    }
}