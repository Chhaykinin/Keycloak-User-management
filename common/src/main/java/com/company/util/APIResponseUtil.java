package com.company.util;

import com.company.response.ApiResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class APIResponseUtil {
    public static <T> ApiResponse<T> apiResponse(T payload, HttpStatus status) {
        return ApiResponse.<T>builder()
                .payload(payload)
                .status(status)
                .code(status.value())
                .localDateTime(LocalDateTime.now())
                .build();
    }
}
