package xiaowu.social_network_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.dto.GroupDTO;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.mdoel.Group;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.service.GroupService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 群组控制器 - 使用Java 17特性
 *
 * 提供群组的增删改查以及成员管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 创建群组
     */
    @PostMapping
    public Result<GroupDTO> createGroup(@RequestBody GroupDTO request) {
        try {
            Group group = groupService.createGroup(request.getGroupName(), request.getClassGroupId());
            var dto = GroupDTO.builder()
                    .id(group.getId())
                    .groupName(group.getGroupName())
                    .classGroupId(request.getClassGroupId())
                    .memberCount(0)
                    .createdAt(group.getCreatedAt())
                    .updatedAt(group.getUpdatedAt())
                    .build();
            return Result.success("创建群组成功", dto);
        } catch (Exception e) {
            log.error("创建群组失败", e);
            return Result.error("创建群组失败");
        }
    }

    /**
     * 邀请用户加入群组
     */
    @PostMapping("/{groupId}/members/{userId}")
    public Result<String> inviteUser(@PathVariable Integer groupId, @PathVariable Integer userId) {
        try {
            groupService.inviteUserToGroup(groupId, userId);
            return Result.success("邀请用户成功");
        } catch (Exception e) {
            log.error("邀请用户进入群组失败", e);
            return Result.error("邀请用户失败");
        }
    }

    /**
     * 获取群组成员列表
     */
    @GetMapping("/{groupId}/members")
    public Result<List<UserInfoResponse>> listMembers(@PathVariable Integer groupId) {
        try {
            var members = groupService.getGroupMembers(groupId)
                    .stream()
                    .map(this::toUserInfoResponse)
                    .collect(Collectors.toList());
            return Result.success("获取群组成员成功", members);
        } catch (Exception e) {
            log.error("获取群组成员失败", e);
            return Result.error("获取群组成员失败");
        }
    }

    /**
     * 删除群组
     */
    @DeleteMapping("/{groupId}")
    public Result<String> deleteGroup(@PathVariable Integer groupId) {
        try {
            groupService.deleteGroup(groupId);
            return Result.success("删除群组成功");
        } catch (Exception e) {
            log.error("删除群组失败", e);
            return Result.error("删除群组失败");
        }
    }

    /**
     * 获取群组详情
     */
    @GetMapping("/{groupId}")
    public Result<GroupDTO> getGroup(@PathVariable Integer groupId) {
        try {
            Group group = groupService.getGroupId(groupId);
            var members = groupService.getGroupMembers(groupId);
            var dto = GroupDTO.builder()
                    .id(group.getId())
                    .groupName(group.getGroupName())
                    .classGroupId(null)
                    .memberCount(members.size())
                    .createdAt(group.getCreatedAt())
                    .updatedAt(group.getUpdatedAt())
                    .build();
            return Result.success("获取群组信息成功", dto);
        } catch (Exception e) {
            log.error("获取群组信息失败", e);
            return Result.error("获取群组信息失败");
        }
    }

    /**
     * 将用户实体转换为用户信息响应
     */
    private UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
