package com.example.just.Service;


import com.example.just.Redis.Fcm;

import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class FCMService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate; // RedisTemplate 주입

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    NotificationRepository notificationRepository;


    @CachePut(cacheNames = "token", key = "#memberId", cacheManager = "contentCacheManager")
    public String setToken(int memberId, String token) throws Exception {
        // 해당 아이디 가진 유저가 존재하는지 검사
        Fcm fcm = new Fcm();
        fcm.setMemberId(memberId);
        fcm.setToken(token);
        String cache = getToken(memberId);
        System.out.println(cache);
       // setToken(1, "fedGDsuzTfuGs5_lPExj3I:APA91bF5aiAvLGKc25p_EzlbGY4YDXFxRwOQaakC4Wl8wSSl2eBiGleCRuLZpbpzkgFf5drTNjFRScMQznhdcXTEgGoRyGQJaLb28jrz2CMhyDVQfS31ac3mCPo6j-bmoIrC_5vwpDJn");
        return token;
    }


    public String sendMessage(String title, String body) throws Exception {

        String token = getToken(1);

        Notification notification2 = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification2)
                .build();
        com.example.just.Dao.Notification notification = new com.example.just.Dao.Notification(
                "comment",
                1L,
                new Date(),
                false,
                null,
                1L
        );
        notificationRepository.save(notification);
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                // 토큰이 유효하지 않은 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            } else if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                // 재발급된 이전 토큰인 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            } else { // 그 외, 오류는 런타임 예외로 처리
                throw new RuntimeException(e);
            }
        }
    }

    @Cacheable(value = "token", key = "#memberId" ,cacheManager = "contentCacheManager")
    public String getToken(int memberId) {
        return redisTemplate.opsForValue().get("token::" + memberId); // Redis에서 값을 가져옴
    }
}


