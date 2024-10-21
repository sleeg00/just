package com.example.just.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TokenRequestDto {

    @Schema(description = "FCM Token", example = "")
    String token;
}
