package com.example.just.Resolver; // 패키지 경로 확인!

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 메서드의 파라미터에서 사용 가능하도록 변경
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
public @interface ExtractMember {
}
