package com.company.model.dto.response;

import lombok.Builder;

@Builder
public record UserGroupResponse(
        UserResponse user,
        GroupResponse group
) {
}
