package com.company.service;

import com.company.model.dto.request.UserRequest;
import com.company.model.dto.response.UserResponse;

import java.util.List;

public interface KeycloakAdminService {

    UserResponse addUser(UserRequest userRequest);

    UserResponse updateUser(String userId, UserRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(String userId);

    UserResponse getUserByUsername(String username);

    UserResponse getUserByEmail(String email);
}
