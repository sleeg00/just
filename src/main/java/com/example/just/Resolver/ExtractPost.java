package com.example.just.Resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 메서드 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
public @interface ExtractPost {
}
