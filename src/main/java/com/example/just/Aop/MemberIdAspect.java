package com.example.just.Aop;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtProvider;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect // AOP 기능 클래스를 나타냄
@Component // Bean으로 관리
@RequiredArgsConstructor
public class MemberIdAspect {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Around("@annotation(ExtractMember)") // EtractMember 메서드 실행 가로챔
    public Object extractMember(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); // Servlet가져옴
        HttpServletRequest request = attributes.getRequest();

        String token = jwtProvider.getAccessToken(request);
        Long memberId = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Object[] args = joinPoint.getArgs(); // Object매게변수 가져옴
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Member) { // 원래는 null인 매게변수의 값 넣어줌
                args[i] = member;  // 기존 인자를 Member 객체로 변경
                break;
            }
        }

        return joinPoint.proceed(args);
    }
}
