package com.company.controller;

import com.company.model.dto.request.UserRequest;
import com.company.model.dto.response.UserResponse;
import com.company.response.ApiResponse;
import com.company.service.KeycloakAdminService;
import com.company.util.APIResponseUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.keycloak.jose.jwk.JWK;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@SecurityRequirement(name = "oauth")
public class UserController {
    private final KeycloakAdminService keycloakAdminService;
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> addUser(@Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(
                APIResponseUtil.apiResponse(
                        keycloakAdminService.addUser(userRequest),
                        HttpStatus.CREATED
                )
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserById(@PathVariable String userId, @Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(
                APIResponseUtil.apiResponse(
                        keycloakAdminService.updateUser(userId, userRequest),
                        HttpStatus.OK
                )
        );
    }

    @GetMapping
    public  ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
        return ResponseEntity.ok(
                APIResponseUtil.apiResponse(
                        keycloakAdminService.getAllUsers(),
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId){

        return ResponseEntity.ok(
            APIResponseUtil.apiResponse(
                    keycloakAdminService.getUserById(userId),
                    HttpStatus.OK
            )
        );
    }

    @GetMapping("/username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@RequestParam String username){
        return  ResponseEntity.ok(
                APIResponseUtil.apiResponse(
                        keycloakAdminService.getUserByUsername(username),
                        HttpStatus.OK
                )
        );
    }


    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email ){
        return ResponseEntity.ok(
                APIResponseUtil.apiResponse(
                        keycloakAdminService.getUserByEmail(email),
                        HttpStatus.OK
                )
        );
    }

}
