package xiaowu.social_network_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS跨域配置
 * 
 * 📖 解决前后端分离开发中的跨域问题
 * 支持局域网内多设备访问
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源 - 支持局域网访问
                .allowedOriginPatterns(
                    "http://localhost:*",
                    "http://127.0.0.1:*",
                    "http://192.168.*.*:*",  // 支持192.168网段
                    "http://10.*.*.*:*",     // 支持10网段
                    "http://172.16.*.*:*",   // 支持172.16-172.31网段
                    "https://localhost:*",
                    "https://127.0.0.1:*",
                    "https://192.168.*.*:*",
                    "https://10.*.*.*:*",
                    "https://172.16.*.*:*"
                )
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证
                .allowCredentials(true)
                // 预检请求的缓存时间（秒）
                .maxAge(3600);
    }
}
