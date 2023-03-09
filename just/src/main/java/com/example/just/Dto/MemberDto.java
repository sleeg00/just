package com.example.just.Dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String provider;
}
