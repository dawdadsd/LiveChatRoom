package xiaowu.social_network_demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xiaowu.social_network_demo.filter.JwtAuthenticationFilter;

/**
 * Spring Security配置类 - 使用Java 17特性
 *
 * @author xiaowu
 * @since Java 17
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers("/api/auth/**", "/api/public/**", "/chat/**", "/error").permitAll()
                        // 教师专用接口
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        // 学生专用接口
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        // 需要认证的接口
                        .requestMatchers("/api/user/**").hasAnyRole("STUDENT", "TEACHER")
                        .anyRequest().authenticated()
                )
                // 禁用session，使用JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 异常处理
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                {
                                    "success": false,
                                    "code": 401,
                                    "message": "未认证，请先登录",
                                    "data": null,
                                    "timestamp": "%s"
                                }
                                """.formatted(java.time.LocalDateTime.now()));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                {
                                    "success": false,
                                    "code": 403,
                                    "message": "权限不足",
                                    "data": null,
                                    "timestamp": "%s"
                                }
                                """.formatted(java.time.LocalDateTime.now()));
                        })
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}