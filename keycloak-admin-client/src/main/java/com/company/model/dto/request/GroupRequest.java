package com.company.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupRequest(
        @NotBlank(message = "Group name is required")
        @Size(min = 2, max = 50, message = "Group name must be between 2 and 50 characters")
        String groupName
) {
}
