package com.company.model.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record UserResponse(
        String userId,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
