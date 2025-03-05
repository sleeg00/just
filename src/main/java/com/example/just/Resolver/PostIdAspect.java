package com.example.just.Resolver;

import com.example.just.Dao.Post;
import com.example.just.Exception.NotFoundException;
import com.example.just.Repository.PostRepository;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PostIdAspect implements HandlerMethodArgumentResolver {
    private final PostRepository postRepository;

    public PostIdAspect(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtractPost.class) &&
                parameter.getParameterType().equals(Post.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String postIdParam = request.getParameter("postId"); // Query Parameter에서 postId 가져오기

        if (postIdParam == null) {
            throw new IllegalArgumentException("postId가 필요합니다.");
        }

        Long postId = Long.valueOf(postIdParam);
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다."));
    }
}