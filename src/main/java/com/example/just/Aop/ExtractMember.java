package com.example.just.Aop; // 패키지 경로 확인!

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
public @interface ExtractMember {
}