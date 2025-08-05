package xiaowu.social_network_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.service.StudentService;

import java.util.List;

/**
 * 学生控制器 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 获取同专业同学列表
     */
    @GetMapping("/classmates")
    public Result<List<UserInfoResponse>> getClassmates(Authentication authentication) {
        try {
            var username = authentication.getName();
            var classmates = studentService.getClassmates(username);
            return Result.success("获取同学列表成功", classmates);
        } catch (Exception e) {
            log.error("获取同学列表失败", e);
            return Result.error("获取同学列表失败");
        }
    }

    /**
     * 获取本专业教师列表
     */
    @GetMapping("/teachers")
    public Result<List<UserInfoResponse>> getMajorTeachers(Authentication authentication) {
        try {
            var username = authentication.getName();
            var teachers = studentService.getMajorTeachers(username);
            return Result.success("获取教师列表成功", teachers);
        } catch (Exception e) {
            log.error("获取教师列表失败", e);
            return Result.error("获取教师列表失败");
        }
    }

    /**
     * 更新学生学业信息
     */
    @PutMapping("/profile/academic")
    public Result<String> updateAcademicInfo(@RequestBody UserInfoResponse updateRequest,
                                            Authentication authentication) {
        try {
            var username = authentication.getName();
            var success = studentService.updateAcademicInfo(username, updateRequest);
            
            if (success) {
                return Result.success("学业信息更新成功");
            } else {
                return Result.error("学业信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新学业信息失败", e);
            return Result.error("更新学业信息失败");
        }
    }

    /**
     * 获取学生统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getStudentStatistics(Authentication authentication) {
        try {
            var username = authentication.getName();
            var statistics = studentService.getStudentStatistics(username);
            return Result.success("获取统计信息成功", statistics);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }

    /**
     * 检查是否已毕业
     */
    @GetMapping("/graduation-status")
    public Result<Boolean> checkGraduationStatus(Authentication authentication) {
        try {
            var username = authentication.getName();
            var isGraduated = studentService.checkGraduationStatus(username);
            return Result.success("获取毕业状态成功", isGraduated);
        } catch (Exception e) {
            log.error("获取毕业状态失败", e);
            return Result.error("获取毕业状态失败");
        }
    }
}
