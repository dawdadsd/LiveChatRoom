package xiaowu.social_network_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.mapper.UserMapper;
import xiaowu.social_network_demo.mdoel.StudentUser;
import xiaowu.social_network_demo.mdoel.TeacherUser;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.service.StudentService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserMapper userMapper;

    @Override
    public List<UserInfoResponse> getClassmates(String studentUsername) {
        // 先获取当前学生的专业信息
        var currentStudent = (StudentUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", studentUsername)
        );
        
        if (currentStudent == null || currentStudent.getMajor() == null) {
            return List.of();
        }
        
        var classmates = userMapper.selectStudentsByMajor(currentStudent.getMajor());
        
        return classmates.stream()
                .filter(student -> !student.getUsername().equals(studentUsername)) // 排除自己
                .map(this::convertToUserInfoResponse)
                .toList();
    }

    @Override
    public List<UserInfoResponse> getMajorTeachers(String studentUsername) {
        // 先获取当前学生的专业信息
        var currentStudent = (StudentUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", studentUsername)
        );
        
        if (currentStudent == null || currentStudent.getMajor() == null) {
            return List.of();
        }
        
        // 这里假设教师的department字段对应学生的major字段
        // 实际项目中可能需要更复杂的映射关系
        var teachers = userMapper.selectTeachersByDepartment(currentStudent.getMajor());
        
        return teachers.stream()
                .map(this::convertToUserInfoResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateAcademicInfo(String username, UserInfoResponse updateRequest) {
        var student = (StudentUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (student == null) {
            return false;
        }

        // 更新学生学业信息
        student.setMajor(updateRequest.getMajor());
        student.setGrade(updateRequest.getGrade());
        student.setClassName(updateRequest.getClassName());
        student.setCollege(updateRequest.getCollege());
        student.setEnrollmentYear(updateRequest.getEnrollmentYear());
        student.setUpdatedAt(LocalDateTime.now());

        return userMapper.updateById(student) > 0;
    }

    @Override
    public Map<String, Object> getStudentStatistics(String username) {
        var student = (StudentUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (student == null) {
            return new HashMap<>();
        }

        var statistics = new HashMap<String, Object>();
        
        // 基本信息
        statistics.put("isGraduated", student.isGraduated());
        statistics.put("major", student.getMajor());
        statistics.put("grade", student.getGrade());
        statistics.put("college", student.getCollege());
        statistics.put("enrollmentYear", student.getEnrollmentYear());
        
        // 统计同专业学生数量
        if (student.getMajor() != null) {
            var classmateCount = userMapper.selectStudentsByMajor(student.getMajor()).size() - 1; // 排除自己
            statistics.put("classmateCount", classmateCount);
        }
        
        // 计算在校年数
        if (student.getEnrollmentYear() != null) {
            var currentYear = java.time.Year.now().getValue();
            var yearsInSchool = currentYear - student.getEnrollmentYear();
            statistics.put("yearsInSchool", yearsInSchool);
        }
        
        statistics.put("lastLoginTime", LocalDateTime.now());
        
        return statistics;
    }

    @Override
    public boolean checkGraduationStatus(String username) {
        var student = (StudentUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        return student != null && student.isGraduated();
    }

    /**
     * 转换用户实体为响应DTO
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
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
}
