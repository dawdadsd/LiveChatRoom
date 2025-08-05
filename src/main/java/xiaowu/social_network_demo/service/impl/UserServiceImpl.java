package xiaowu.social_network_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.mapper.UserMapper;
import xiaowu.social_network_demo.mdoel.StudentUser;
import xiaowu.social_network_demo.mdoel.TeacherUser;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.service.UserService;

import java.time.LocalDateTime;

/**
 * 用户服务实现类 - 使用Java 17特性
 *
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoResponse getUserInfo(String username) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        var responseBuilder = UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        // 根据用户类型设置特定信息
        switch (user.getRole()) {
            case STUDENT -> {
                if (user instanceof StudentUser student) {
                    responseBuilder
                            .studentId(student.getStudentId())
                            .major(student.getMajor())
                            .grade(student.getGrade())
                            .className(student.getClassName())
                            .college(student.getCollege())
                            .enrollmentYear(student.getEnrollmentYear());
                }
            }
            case TEACHER -> {
                if (user instanceof TeacherUser teacher) {
                    responseBuilder
                            .employeeId(teacher.getEmployeeId())
                            .department(teacher.getDepartment())
                            .title(teacher.getTitle())
                            .officeLocation(teacher.getOfficeLocation())
                            .researchArea(teacher.getResearchArea())
                            .hireDate(teacher.getHireDate());
                }
            }
        }

        return responseBuilder.build();
    }

    @Override
    @Transactional
    public boolean updateUserInfo(String username, UserInfoResponse updateRequest) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            return false;
        }

        // 更新基本信息
        user.setRealName(updateRequest.getRealName());
        user.setEmail(updateRequest.getEmail());
        user.setPhone(updateRequest.getPhone());
        user.setUpdatedAt(LocalDateTime.now());

        // 根据用户类型更新特定信息
        switch (user.getRole()) {
            case STUDENT -> {
                if (user instanceof StudentUser student) {
                    student.setMajor(updateRequest.getMajor());
                    student.setGrade(updateRequest.getGrade());
                    student.setClassName(updateRequest.getClassName());
                    student.setCollege(updateRequest.getCollege());
                }
            }
            case TEACHER -> {
                if (user instanceof TeacherUser teacher) {
                    teacher.setDepartment(updateRequest.getDepartment());
                    teacher.setTitle(updateRequest.getTitle());
                    teacher.setOfficeLocation(updateRequest.getOfficeLocation());
                    teacher.setResearchArea(updateRequest.getResearchArea());
                }
            }
        }

        return userMapper.updateById(user) > 0;
    }
    @Override
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            return false;
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean updateAvatar(String username, String avatarUrl) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            return false;
        }

        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean toggleUserStatus(String username, boolean isActive) {
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            return false;
        }

        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }
}
