package xiaowu.social_network_demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaowu.social_network_demo.common.Result;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 公开API控制器 - 不需要认证的接口
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicController {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        var systemInfo = Map.<String,Object>of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "description", "社交网络演示系统"
        );
        
        return Result.success("系统运行正常",systemInfo);
    }

    /**
     * 系统信息接口
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        var systemInfo = Map.<String, Object>of(
                "applicationName", "Social Network Demo",
                "version", "1.0.0",
                "javaVersion", System.getProperty("java.version"),
                "springBootVersion", "3.5.4",
                "buildTime", LocalDateTime.now(),
                "features", new String[]{"JWT认证", "角色权限", "WebSocket聊天", "用户管理"}
        );

        return Result.success("获取系统信息成功", systemInfo);
    }

    /**
     * API文档信息
     */
    @GetMapping("/api-docs")
    public Result<Map<String, Object>> apiDocs() {
        var apiInfo = Map.<String, Object>of(
                "authEndpoints", Map.<String, Object>of(
                        "login", "POST /api/auth/login",
                        "register", "POST /api/auth/register/{student|teacher}",
                        "refresh", "POST /api/auth/refresh",
                        "logout", "POST /api/auth/logout"
                ),
                "userEndpoints", Map.<String, Object>of(
                        "profile", "GET /api/user/profile",
                        "updateProfile", "PUT /api/user/profile",
                        "changePassword", "PUT /api/user/password"
                ),
                "teacherEndpoints", Map.<String, Object>of(
                        "students", "GET /api/teacher/students",
                        "colleagues", "GET /api/teacher/colleagues"
                ),
                "studentEndpoints", Map.<String, Object>of(
                        "classmates", "GET /api/student/classmates",
                        "teachers", "GET /api/student/teachers"
                )
        );

        return Result.success("获取API文档成功", apiInfo);
    }
}
