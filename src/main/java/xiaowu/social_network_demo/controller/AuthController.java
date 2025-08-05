package xiaowu.social_network_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.dto.LoginRequest;
import xiaowu.social_network_demo.dto.LoginResponse;
import xiaowu.social_network_demo.dto.RegisterRequest;
import xiaowu.social_network_demo.service.AuthService;
import xiaowu.social_network_demo.utils.JwtUtil;

/**
 * 认证控制器 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("用户登录尝试: {}, 类型: {}", request.getUsername(), request.getUserType());
            
            // Spring Security 认证
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), 
                    request.getPassword()
            );
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // 生成登录响应
            var loginResponse = authService.generateLoginResponse(
                    request.getUsername(), 
                    request.getUserType(),
                    request.isRememberMe()
            );
            
            log.info("用户 {} 登录成功", request.getUsername());
            return Result.success("登录成功", loginResponse);
            
        } catch (AuthenticationException e) {
            log.warn("用户 {} 登录失败: {}", request.getUsername(), e.getMessage());
            return Result.unauthorized("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录过程中发生错误", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 学生注册
     */
    @PostMapping("/register/student")
    public Result<String> registerStudent(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("学生注册尝试: {}", request.getUsername());
            
            // 验证密码确认
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return Result.badRequest("两次输入的密码不一致");
            }
            
            var result = authService.registerStudent(request);
            
            if (result) {
                log.info("学生 {} 注册成功", request.getUsername());
                return Result.success("注册成功，请登录");
            } else {
                return Result.error("注册失败，用户名或邮箱已存在");
            }
            
        } catch (Exception e) {
            log.error("学生注册过程中发生错误", e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 教师注册
     */
    @PostMapping("/register/teacher")
    public Result<String> registerTeacher(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("教师注册尝试: {}", request.getUsername());
            
            // 验证密码确认
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return Result.badRequest("两次输入的密码不一致");
            }
            
            var result = authService.registerTeacher(request);
            
            if (result) {
                log.info("教师 {} 注册成功", request.getUsername());
                return Result.success("注册成功，请登录");
            } else {
                return Result.error("注册失败，用户名或邮箱已存在");
            }
            
        } catch (Exception e) {
            log.error("教师注册过程中发生错误", e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            var refreshToken = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (refreshToken == null || !jwtUtil.isRefreshToken(refreshToken)) {
                return Result.unauthorized("无效的刷新令牌");
            }
            
            if (!jwtUtil.validateToken(refreshToken)) {
                return Result.unauthorized("刷新令牌已过期");
            }
            
            var userId = jwtUtil.getUserIdFromToken(refreshToken);
            var newTokens = authService.refreshTokens(userId);
            
            log.info("用户 {} 刷新令牌成功", userId);
            return Result.success("令牌刷新成功", newTokens);
            
        } catch (Exception e) {
            log.error("刷新令牌过程中发生错误", e);
            return Result.unauthorized("令牌刷新失败");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            var token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token != null) {
                // 这里可以实现令牌黑名单机制
                // tokenBlacklistService.addToBlacklist(token);
                log.info("用户登出成功");
            }
            
            return Result.success("登出成功");
            
        } catch (Exception e) {
            log.error("登出过程中发生错误", e);
            return Result.error("登出失败");
        }
    }
}
