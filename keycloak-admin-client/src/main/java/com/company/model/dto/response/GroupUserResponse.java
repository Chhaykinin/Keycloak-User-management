package com.company.model.dto.response;

import lombok.Builder;

import java.util.List;
@Builder
public record GroupUserResponse(
        GroupResponse group,
         List<UserResponse>userList
) {
}
