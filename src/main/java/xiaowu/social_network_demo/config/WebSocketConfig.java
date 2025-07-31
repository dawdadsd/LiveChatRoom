package xiaowu.social_network_demo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import xiaowu.social_network_demo.interceptor.ChatWebSocketHandler;
import xiaowu.social_network_demo.interceptor.WebSocketInterceptor;

@Configuration
@EnableWebSocket
@Data
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final WebSocketInterceptor webSocketInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                // 注册WebSocket处理器到指定端点
                .addHandler(chatWebSocketHandler, "/chat")
                // 添加拦截器（用于IP识别和连接前置处理）
                .addInterceptors(webSocketInterceptor)
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://192.168.*.*:*",  // 支持整个192.168网段
                        "http://10.*.*.*:*",     // 支持10网段
                        "http://172.16.*.*:*",   // 支持172.16网段
                        "https://localhost:*",
                        "https://127.0.0.1:*",
                        "https://192.168.*.*:*",
                        "https://10.*.*.*:*",
                        "https://172.16.*.*:*"
                );
    }
}