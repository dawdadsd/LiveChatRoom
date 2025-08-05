package xiaowu.social_network_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xiaowu.social_network_demo.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 登录响应DTO - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // 访问令牌过期时间（秒）
    
    // 用户信息
    private Integer userId;
    private String username;
    private String realName;
    private String email;
    private UserRole role;
    private String avatarUrl;
    private LocalDateTime lastLoginTime;
    
    // 角色特定信息
    private String department; // 教师的院系或学生的专业
    private String additionalInfo; // 其他信息
}
