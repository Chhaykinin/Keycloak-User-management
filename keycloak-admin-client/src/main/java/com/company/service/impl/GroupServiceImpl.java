package com.company.service.impl;

import com.company.exception.ResourceNotFoundException;
import com.company.mapper.GroupMapper;
import com.company.mapper.UserMapper;
import com.company.model.dto.request.GroupRequest;
import com.company.model.dto.response.GroupResponse;
import com.company.model.dto.response.GroupUserResponse;
import com.company.model.dto.response.UserGroupResponse;
import com.company.model.dto.response.UserResponse;
import com.company.service.GroupService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    @Override
    public GroupResponse createGroup(GroupRequest groupDTORequest) {

        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(groupDTORequest.groupName());

        try (Response response = groupsResource.add(groupRepresentation)) {

            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException(
                        "Failed to create group. Status: " + response.getStatus()
                );
            }

            String createdGroupId = CreatedResponseUtil.getCreatedId(response);

            GroupRepresentation createdGroup =
                    groupsResource.group(createdGroupId).toRepresentation();

            // MapStruct does the mapping
            return groupMapper.toGroupResponse(createdGroup);
        }
    }

    @Override
    public List<GroupResponse> getAllGroup() {
        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();
        List<GroupRepresentation> groupList = groupsResource.groups();
        return groupList.stream()
                .map(groupMapper::toGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserGroupResponse createUserToGroup(String userId, String groupId) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        GroupsResource groupsResource = realmResource.groups();

        try {
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            GroupRepresentation group = groupsResource.group(groupId).toRepresentation();

            usersResource.get(userId).joinGroup(groupId);

            return UserGroupResponse.builder()
                    .user(userMapper.toUserResponse(user))
                    .group(groupMapper.toGroupResponse(group))
                    .build();

        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException("User or Group not found");
        }
    }

    @Override
    public GroupUserResponse getGroupUser(String groupId) {
        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();

        // Get group
        GroupRepresentation groupRepresentation;
        try {
            groupRepresentation = groupsResource.group(groupId).toRepresentation();
        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        // Get group members
        List<UserRepresentation> userRepresentations = groupsResource.group(groupId).members();

        // Map to DTOs using MapStruct
        List<UserResponse> userList = userRepresentations.stream()
                .map(userMapper::toUserResponse)
                .toList();

        GroupResponse groupDto = groupMapper.toGroupResponse(groupRepresentation);

        // Build response
        return GroupUserResponse.builder()
                .group(groupDto)
                .userList(userList)
                .build();
    }

    @Override
    public GroupResponse updateGroup(String groupId, GroupRequest groupRequest) {
        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();
        GroupRepresentation existingGroup =groupsResource.group(groupId).toRepresentation();
        existingGroup.setName(groupRequest.groupName());
        // saving to keycloak
        groupsResource.group(groupId).update(existingGroup);
        return groupMapper.toGroupResponse(existingGroup);
    }

    @Override
    public void deleteGroup(String groupId) {
        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();
        try {
            groupsResource.group(groupId).remove();
        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
    }

    @Override
    public GroupResponse getGroupById(String groupId) {
        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();
        try {
            GroupRepresentation groupRepresentation = groupsResource.group(groupId).toRepresentation();
            return groupMapper.toGroupResponse(groupRepresentation);
        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
    }
}
