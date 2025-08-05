package xiaowu.social_network_demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaowu.social_network_demo.dto.LoginResponse;
import xiaowu.social_network_demo.dto.RegisterRequest;
import xiaowu.social_network_demo.enums.UserRole;
import xiaowu.social_network_demo.mapper.UserMapper;
import xiaowu.social_network_demo.mdoel.StudentUser;
import xiaowu.social_network_demo.mdoel.TeacherUser;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.utils.JwtUtil;

import java.time.LocalDateTime;

/**
 * 认证服务类 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 生成登录响应
     */
    public LoginResponse generateLoginResponse(String username, UserRole userType, boolean rememberMe) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成令牌
        var accessToken = jwtUtil.createAccessToken(
                user.getId().toString(),
                user.getUsername(),
                user.getRole()
        );
        
        var refreshToken = jwtUtil.createRefreshToken(user.getId().toString());

        // 构建响应
        var responseBuilder = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1小时
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .lastLoginTime(LocalDateTime.now());

        // 根据用户类型设置特定信息
        switch (user.getRole()) {
            case STUDENT -> {
                if (user instanceof StudentUser student) {
                    responseBuilder.department(student.getMajor())
                            .additionalInfo(student.getFullStudentInfo());
                }
            }
            case TEACHER -> {
                if (user instanceof TeacherUser teacher) {
                    responseBuilder.department(teacher.getDepartment())
                            .additionalInfo(teacher.getFullTeacherInfo());
                }
            }
        }

        return responseBuilder.build();
    }

    /**
     * 学生注册
     */
    @Transactional
    public boolean registerStudent(RegisterRequest request) {
        // 检查用户名和邮箱是否已存在
        if (isUsernameOrEmailExists(request.getUsername(), request.getEmail())) {
            return false;
        }

        var student = new StudentUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRealName(),
                request.getEmail()
        );

        // 设置学生特有信息
        student.setPhone(request.getPhone());
        student.setStudentId(request.getStudentId());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setClassName(request.getClassName());
        student.setCollege(request.getCollege());
        student.setEnrollmentYear(request.getEnrollmentYear());
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        student.setIsActive(true);

        return userMapper.insert(student) > 0;
    }

    /**
     * 教师注册
     */
    @Transactional
    public boolean registerTeacher(RegisterRequest request) {
        // 检查用户名和邮箱是否已存在
        if (isUsernameOrEmailExists(request.getUsername(), request.getEmail())) {
            return false;
        }

        var teacher = new TeacherUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRealName(),
                request.getEmail()
        );

        // 设置教师特有信息
        teacher.setPhone(request.getPhone());
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setDepartment(request.getDepartment());
        teacher.setTitle(request.getTitle());
        teacher.setOfficeLocation(request.getOfficeLocation());
        teacher.setResearchArea(request.getResearchArea());
        teacher.setHireDate(java.time.LocalDate.now());
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        teacher.setIsActive(true);

        return userMapper.insert(teacher) > 0;
    }

    /**
     * 刷新令牌
     */
    public LoginResponse refreshTokens(String userId) {
        var user = userMapper.selectById(Integer.valueOf(userId));
        
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("用户不存在或已被禁用");
        }

        return generateLoginResponse(user.getUsername(), user.getRole(), false);
    }

    /**
     * 检查用户名或邮箱是否已存在
     */
    private boolean isUsernameOrEmailExists(String username, String email) {
        var count = userMapper.selectCount(
                new QueryWrapper<User>()
                        .eq("username", username)
                        .or()
                        .eq("email", email)
        );
        return count > 0;
    }
}
