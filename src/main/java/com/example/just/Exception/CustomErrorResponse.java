package com.example.just.Exception;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class CustomErrorResponse {
    private int status;
    private String message;

    public CustomErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ResponseEntity<CustomErrorResponse> create(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new CustomErrorResponse(status.value(), message));
    }
}
