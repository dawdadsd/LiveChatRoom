package xiaowu.social_network_demo.dto;

import jakarta.persistence.Column;
import lombok.Data;
import xiaowu.social_network_demo.enums.UserRole;

/**
 * 登录请求DTO - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Data
public class LoginRequest {
    

    private String username;

    private String password;
    
    @Column()
    private UserRole userType;
    
    private boolean rememberMe = false;
}
