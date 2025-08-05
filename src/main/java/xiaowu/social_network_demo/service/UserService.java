package xiaowu.social_network_demo.service;

import xiaowu.social_network_demo.dto.UserInfoResponse;

/**
 * 用户服务接口 - 使用Java 17特性
 *
 * @author xiaowu
 * @since Java 17
 */
public interface UserService {

    /**
     * 根据用户名获取用户信息
     */
    UserInfoResponse getUserInfo(String username);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(String username, UserInfoResponse updateRequest);

    /**
     * 修改密码
     */
    boolean changePassword(String username, String oldPassword, String newPassword);

    /**
     * 更新头像
     */
    boolean updateAvatar(String username, String avatarUrl);

    /**
     * 启用/禁用用户
     */
    boolean toggleUserStatus(String username, boolean isActive);
}
