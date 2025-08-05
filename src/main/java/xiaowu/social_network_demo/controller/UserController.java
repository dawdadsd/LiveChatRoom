package xiaowu.social_network_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.service.UserService;

/**
 * 用户控制器 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserInfoResponse> getCurrentUserProfile(Authentication authentication) {
        try {
            var username = authentication.getName();
            var userInfo = userService.getUserInfo(username);
            
            return Result.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 更新用户基本信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody UserInfoResponse updateRequest, 
                                       Authentication authentication) {
        try {
            var username = authentication.getName();
            var success = userService.updateUserInfo(username, updateRequest);
            
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败");
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestParam String oldPassword,
                                        @RequestParam String newPassword,
                                        Authentication authentication) {
        try {
            var username = authentication.getName();
            var success = userService.changePassword(username, oldPassword, newPassword);
            
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("原密码错误");
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改密码失败");
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam String avatarUrl,
                                      Authentication authentication) {
        try {
            var username = authentication.getName();
            var success = userService.updateAvatar(username, avatarUrl);
            
            if (success) {
                return Result.success("头像更新成功", avatarUrl);
            } else {
                return Result.error("头像更新失败");
            }
        } catch (Exception e) {
            log.error("头像更新失败", e);
            return Result.error("头像更新失败");
        }
    }
}
