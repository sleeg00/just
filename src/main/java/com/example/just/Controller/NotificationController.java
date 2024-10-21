package com.example.just.Controller;

import com.example.just.Dto.TokenRequestDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.Service.FCMService;
import com.example.just.Service.NotificationService;
import com.example.just.jwt.JwtProvider;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import software.amazon.awssdk.core.Response;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @Autowired
    private FCMService fcmService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @ApiOperation(value = "알림", notes = "<big>알림 통신 연결</big>lastEventID는 값을 넣지않고, 헤더에 토큰만 있으면 됨\n" +
            "not_type : comment(댓글달림),postLike(게시글에 좋아요), commentLike(댓글좋아요), bigComment(대댓글달림)로 어떤 종류인지 판단, <big>String타입</big>\n" +
            "not_post_id : 알림의 target 게시글 id, <big>int</big> \n" +
            "not_datetime : 알람이온 시간(일단 필요해서 넣었음), <big>DateTime</big>\n" +
            "receiver : 수신자(게시글 작성자인 본인을 말함)id, <big>int</big>\n" +
            "senderId : 송신자id, <big>int</big>")
    @GetMapping(value = "/noti", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(HttpServletRequest request, @RequestHeader(value = "Last_Event-ID",required = false,defaultValue = "") String lastEventId){

        return notificationService.subscribe(request,lastEventId);

    }
    @PostMapping("/{id}/token")
    public String getToken(@PathVariable Long id){
        try {
            return fcmService.setToken(id, "fedGDsuzTfuGs5_lPExj3I:APA91bF5aiAvLGKc25p_EzlbGY4YDXFxRwOQaakC4Wl8wSSl2eBiGleCRuLZpbpzkgFf5drTNjFRScMQznhdcXTEgGoRyGQJaLb28jrz2CMhyDVQfS31ac3mCPo6j-bmoIrC_5vwpDJn");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "500";
        }
    }
    @PostMapping("/{id}/send")
    public String sendToken(@PathVariable Long id) {
        try {
            String token = memberRepository.findById(id).get().getToken();

            String body = "what";
            String title = "hi";
            fcmService.sendMessage(token, title, body);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그 출력
            return "0";
        }
        return "ok";
    }


    public Long getAccessTokenOfMemberId(HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return member_id;
    }
}
