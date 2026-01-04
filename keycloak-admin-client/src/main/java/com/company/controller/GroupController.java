package com.company.controller;

import com.company.model.dto.request.GroupRequest;
import com.company.model.dto.response.GroupResponse;
import com.company.model.dto.response.GroupUserResponse;
import com.company.model.dto.response.UserGroupResponse;
import com.company.response.ApiResponse;
import com.company.service.GroupService;
import com.company.util.APIResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@AllArgsConstructor
@SecurityRequirement(name = "oauth")
public class GroupController {
    private final GroupService groupService;
    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody GroupRequest groupDTORequest) {
        GroupResponse groupDto = groupService.createGroup(groupDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponseUtil.apiResponse(groupDto, HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroup(){
        List<GroupResponse> groupDtoList = groupService.getAllGroup();
        return ResponseEntity.ok(APIResponseUtil.apiResponse
                (groupDtoList, HttpStatus.OK)
        );
    }

    @PostMapping("/{groupId}/user/{userId}")
    public ResponseEntity<ApiResponse<UserGroupResponse>> createUserToGroup(
            @PathVariable String userId,
            @PathVariable String groupId) {
        UserGroupResponse userGroupResponse = groupService.createUserToGroup(userId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponseUtil.apiResponse(userGroupResponse, HttpStatus.CREATED));
    }

    @GetMapping("/{groupId}/users")
    public ResponseEntity<ApiResponse<GroupUserResponse>> getGroupUser(@PathVariable String groupId) {
        GroupUserResponse groupUserResponse = groupService.getGroupUser(groupId);
        return ResponseEntity.ok(APIResponseUtil.apiResponse(groupUserResponse, HttpStatus.OK));
    }

    @PutMapping("{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(@PathVariable String groupId,@Valid @RequestBody GroupRequest groupRequest
    ){
        GroupResponse groupResponse= groupService.updateGroup(groupId,groupRequest);
        return ResponseEntity.ok(APIResponseUtil.apiResponse(groupResponse, HttpStatus.OK));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<?>> deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok(APIResponseUtil
                .apiResponse("Group deleted successfully", HttpStatus.OK));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable String groupId) {
        GroupResponse groupResponse = groupService.getGroupById(groupId);
        return ResponseEntity.ok(APIResponseUtil.apiResponse(groupResponse, HttpStatus.OK));
    }

}
