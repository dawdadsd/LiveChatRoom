package xiaowu.social_network_demo.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.*;
import xiaowu.social_network_demo.enums.UserRole;
/**
 * 注册请求DTO - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Data
public class RegisterRequest {
    
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    private String username;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$",
             message = "密码必须包含大小写字母和数字，长度8-20位")
    private String password;
    private String confirmPassword;

    private String realName;
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotNull(message = "用户类型不能为空")
    private UserRole userType;
    
    // 学生特有字段
    private String studentId; // 学号
    private String major; // 专业
    private Integer grade; // 年级
    private String className; // 班级
    private String college; // 学院
    private Integer enrollmentYear; // 入学年份
    
    // 教师特有字段
    private String employeeId; // 工号
    private String department; // 院系
    private String title; // 职称
    private String officeLocation; // 办公室位置
    private String researchArea; // 研究方向
}
