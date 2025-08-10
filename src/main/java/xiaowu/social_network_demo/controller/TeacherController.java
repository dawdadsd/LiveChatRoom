package xiaowu.social_network_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.dto.UserInfoResponse;
import xiaowu.social_network_demo.service.TeacherService;

import java.util.List;

/**
 * 教师控制器 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    /**
     * 获取所有学生列表（教师权限）
     */
    @GetMapping("/students")
    public Result<List<UserInfoResponse>> getAllStudents(Authentication authentication) {
        try {
            var students = teacherService.getAllStudents();
            return Result.success("获取学生列表成功", students);
        } catch (Exception e) {
            log.error("获取学生列表失败", e);
            return Result.error("获取学生列表失败");
        }
    }

    /**
     * 根据专业获取学生列表
     */
    @GetMapping("/students/major/{major}")
    public Result<List<UserInfoResponse>> getStudentsByMajor(@PathVariable String major,
                                                            Authentication authentication) {
        try {
            var students = teacherService.getStudentsByMajor(major);
            return Result.success("获取学生列表成功", students);
        } catch (Exception e) {
            log.error("根据专业获取学生列表失败", e);
            return Result.error("获取学生列表失败");
        }
    }

    /**
     * 获取同院系教师列表
     */
    @GetMapping("/colleagues")
    public Result<List<UserInfoResponse>> getColleagues(Authentication authentication) {
        try {
            var username = authentication.getName();
            var colleagues = teacherService.getColleagues(username);
            return Result.success("获取同事列表成功", colleagues);
        } catch (Exception e) {
            log.error("获取同事列表失败", e);
            return Result.error("获取同事列表失败");
        }
    }

    /**
     * 更新教师专业信息
     */
    @PutMapping("/profile/professional")
    public Result<String> updateProfessionalInfo(@RequestBody UserInfoResponse updateRequest,
                                                 Authentication authentication) {
        try {
            var username = authentication.getName();
            var success = teacherService.updateProfessionalInfo(username, updateRequest);
            
            if (success) {
                return Result.success("专业信息更新成功");
            } else {
                return Result.error("专业信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新专业信息失败", e);
            return Result.error("更新专业信息失败");
        }
    }

    /**
     * 获取教师统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getTeacherStatistics(Authentication authentication) {
        try {
            var username = authentication.getName();
            var statistics = teacherService.getTeacherStatistics(username);
            return Result.success("获取统计信息成功", statistics);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
}
