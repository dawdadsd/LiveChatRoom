package xiaowu.social_network_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xiaowu.social_network_demo.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    
    private Integer id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 学生特有信息
    private String studentId;
    private String major;
    private Integer grade;
    private String className;
    private String college;
    private Integer enrollmentYear;
    
    // 教师特有信息
    private String employeeId;
    private String department;
    private String title;
    private String officeLocation;
    private String researchArea;
    private java.time.LocalDate hireDate;
}
