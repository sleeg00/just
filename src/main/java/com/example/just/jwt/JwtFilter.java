package com.example.just.jwt;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       // System.out.println("doFilter");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();
        String accessToken = resolveToken(httpServletRequest);
        String refreshToken = httpServletRequest.getHeader("refresh_token");
        if(StringUtils.hasText(accessToken)&& jwtProvider.validateToken(accessToken)){
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
           // System.out.println("정상작동");
           // System.out.println("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}" + authentication.getName() + requestURI);
        }
        else if(!jwtProvider.validateToken(accessToken)&&refreshToken!=null){
            String token= jwtProvider.getMemberFromRefreshToken(refreshToken);
            String member = refreshToken.replace("Bearer ", "");
            if(jwtProvider.validateToken(member)&& member!=null){
               // System.out.println("토큰터지고 리프레시 있음");
                //String member = jwtProvider.getMemberFromRefreshToken(refreshToken);
                String newToken = jwtProvider.createaccessToken(member);
                HttpHeaders httpHeaders = new HttpHeaders();
                ((HttpServletResponse) response).setHeader(JwtFilter.AUTHORIZATION_HEADER,"Bearer " + newToken);
                Authentication authentication = jwtProvider.getAuthentication(newToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
               // System.out.println(authentication.getName());
              //  System.out.println(newToken);
            }
        }else{
            System.out.println("유효한 JWT 토큰이 없습니다, uri: {}" + requestURI);
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
