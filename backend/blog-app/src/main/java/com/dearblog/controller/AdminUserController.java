package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.request.PasswordUpdateRequest;
import com.dearblog.dto.request.UserUpdateRequest;
import com.dearblog.dto.response.UserVO;
import com.dearblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理 - 用户")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getMe() {
        return Result.ok(userService.getMe());
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestBody UserUpdateRequest req) {
        return Result.ok(userService.updateMe(req));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest req) {
        userService.updatePassword(req);
        return Result.ok();
    }
}
