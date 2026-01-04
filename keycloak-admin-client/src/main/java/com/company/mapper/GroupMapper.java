package com.company.mapper;

import com.company.model.dto.response.GroupResponse;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface GroupMapper {
    @Mapping(target = "groupId", source = "id")
    @Mapping(target = "groupName", source = "name")
    GroupResponse toGroupResponse(GroupRepresentation groupRepresentation);

}
