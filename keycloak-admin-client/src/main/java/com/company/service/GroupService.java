package com.company.service;

import com.company.model.dto.request.GroupRequest;
import com.company.model.dto.response.GroupResponse;
import com.company.model.dto.response.GroupUserResponse;
import com.company.model.dto.response.UserGroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse createGroup(GroupRequest groupDTORequest);

    List<GroupResponse> getAllGroup();

    UserGroupResponse createUserToGroup(String userId, String groupId);

    GroupUserResponse getGroupUser(String groupId);
}
