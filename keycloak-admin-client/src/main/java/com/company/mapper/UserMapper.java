package com.company.mapper;

import com.company.model.dto.response.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "createdAt", expression = "java(mapCreatedAt(user))")
    @Mapping(target = "lastModifiedAt", expression = "java(mapLastModifiedAt(user))")
    UserResponse toUserResponse(UserRepresentation user);
    // 🔽 Helper methods

    default LocalDateTime mapCreatedAt(UserRepresentation user) {
        if (user.getCreatedTimestamp() == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(user.getCreatedTimestamp()),
                ZoneId.systemDefault()
        );
    }

    default LocalDateTime mapLastModifiedAt(UserRepresentation user) {
        if (user.getAttributes() == null) {
            return null;
        }
        List<String> values = user.getAttributes().get("lastModifiedAt");
        return (values != null && !values.isEmpty())
                ? LocalDateTime.parse(values.get(0))
                : null;
    }
}
