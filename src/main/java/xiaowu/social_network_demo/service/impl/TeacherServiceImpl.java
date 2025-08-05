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
import xiaowu.social_network_demo.service.TeacherService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师服务实现类 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final UserMapper userMapper;

    @Override
    public List<UserInfoResponse> getAllStudents() {
        var students = userMapper.selectAllStudents();
        
        return students.stream()
                .map(this::convertToUserInfoResponse)
                .toList();
    }

    @Override
    public List<UserInfoResponse> getStudentsByMajor(String major) {
        var students = userMapper.selectStudentsByMajor(major);
        
        return students.stream()
                .map(this::convertToUserInfoResponse)
                .toList();
    }

    @Override
    public List<UserInfoResponse> getColleagues(String teacherUsername) {
        // 先获取当前教师的院系信息
        var currentTeacher = (TeacherUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", teacherUsername)
        );
        
        if (currentTeacher == null || currentTeacher.getDepartment() == null) {
            return List.of();
        }
        
        var colleagues = userMapper.selectTeachersByDepartment(currentTeacher.getDepartment());
        
        return colleagues.stream()
                .filter(teacher -> !teacher.getUsername().equals(teacherUsername)) // 排除自己
                .map(this::convertToUserInfoResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateProfessionalInfo(String username, UserInfoResponse updateRequest) {
        var teacher = (TeacherUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (teacher == null) {
            return false;
        }

        // 更新教师专业信息
        teacher.setDepartment(updateRequest.getDepartment());
        teacher.setTitle(updateRequest.getTitle());
        teacher.setOfficeLocation(updateRequest.getOfficeLocation());
        teacher.setResearchArea(updateRequest.getResearchArea());
        teacher.setUpdatedAt(LocalDateTime.now());

        return userMapper.updateById(teacher) > 0;
    }

    @Override
    public Map<String, Object> getTeacherStatistics(String username) {
        var teacher = (TeacherUser) userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (teacher == null) {
            return new HashMap<>();
        }

        var statistics = new HashMap<String, Object>();
        
        // 基本信息
        statistics.put("workingYears", teacher.getWorkingYears());
        statistics.put("isSeniorTeacher", teacher.isSeniorTeacher());
        statistics.put("department", teacher.getDepartment());
        statistics.put("title", teacher.getTitle());
        
        // 统计同院系教师数量
        if (teacher.getDepartment() != null) {
            var colleagueCount = userMapper.selectTeachersByDepartment(teacher.getDepartment()).size() - 1; // 排除自己
            statistics.put("colleagueCount", colleagueCount);
        }
        
        // 可以添加更多统计信息，如指导学生数量等
        statistics.put("lastLoginTime", LocalDateTime.now());
        
        return statistics;
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
