package com.company.service.impl;

import com.company.exception.ConflictException;
import com.company.exception.ResourceNotFoundException;
import com.company.mapper.UserMapper;
import com.company.model.dto.request.UserRequest;
import com.company.model.dto.response.UserResponse;
import com.company.security.Credentials;
import com.company.service.KeycloakAdminService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakAdminServiceImpl implements KeycloakAdminService {
    private final Keycloak keycloak;
    private final UserMapper userMapper;
    @Value("${keycloak.realm}")
    private String realm;
    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }
    private UserRepresentation createUserRepresentation(UserRequest userRequest) {
        CredentialRepresentation credentialRepresentation = Credentials.createPasswordCredentials(userRequest.password());

        UserRepresentation user = new UserRepresentation();

        user.setUsername(userRequest.username());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        user.setEmail(userRequest.email());

        user.setCredentials(Collections.singletonList(credentialRepresentation));

        user.singleAttribute("createdAt", LocalDateTime.now().toString());
        user.singleAttribute("lastModifiedAt", LocalDateTime.now().toString());

        user.setEnabled(true);

        return user;
    }

    @Override
    public UserResponse addUser(UserRequest userRequest) {
        // --- Step 1: Pre-check username ---
        List<UserRepresentation> usersWithUsername = getUsersResource()
                .search(userRequest.username(), true);
        if (!usersWithUsername.isEmpty()) {
            throw new ConflictException("Username already exists");
        }

        // --- Step 2: Pre-check email ---
        boolean emailExists = getUsersResource().search(userRequest.email(), true).stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(userRequest.email()));

        if (emailExists) {
            throw new ConflictException("Email already exists");
        }

        // --- Step 3: Create user in Keycloak ---
        UserRepresentation user = createUserRepresentation(userRequest);
        Response response = getUsersResource().create(user);

        int status = response.getStatus();

        // --- Step 4: Handle race conditions / Keycloak conflict ---
        if (status == Response.Status.CONFLICT.getStatusCode()) {
            // Read Keycloak error message
            String errorMessage = response.readEntity(String.class);

            if (errorMessage.contains("email")) {
                throw new ConflictException("Email already exists");
            } else if (errorMessage.contains("username")) {
                throw new ConflictException("Username already exists");
            } else {
                throw new ConflictException("Username or email already exists");
            }
        }

        // --- Step 5: Handle unexpected status ---
        if (status != Response.Status.CREATED.getStatusCode()) {
            throw new RuntimeException("Failed to create user. Keycloak returned status: " + status);
        }

        // --- Step 6: Get created user ---
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserRepresentation savedUser = getUsersResource().get(userId).toRepresentation();

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(String userId, UserRequest userRequest) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }

        UserRepresentation existingUser =
                getUsersResource().get(userId).toRepresentation();

        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        UserRepresentation user = createUserRepresentation(userRequest);

        // Preserve createdAt
        user.singleAttribute(
                "createdAt",
                existingUser.getAttributes().get("createdAt").getFirst()
        );

        getUsersResource().get(userId).update(user);

        UserRepresentation savedUser =
                getUsersResource().get(userId).toRepresentation();

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return getUsersResource().list()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(String userId) {
        try {
            UserRepresentation user =
                    getUsersResource().get(userId).toRepresentation();

            return userMapper.toUserResponse(user);

        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        List<UserRepresentation> users =
                getUsersResource().searchByUsername(username, true);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        return userMapper.toUserResponse(users.getFirst());

    }

    @Override
    public UserResponse getUserByEmail(String email) {

        List<UserRepresentation> emailS =
                getUsersResource().searchByEmail(email, true);

        if (emailS.isEmpty()) {
            throw new ResourceNotFoundException("Email not found");
        }

        return userMapper.toUserResponse(emailS.getFirst());
    }

}
