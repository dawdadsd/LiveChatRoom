package xiaowu.social_network_demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception{
        String clientIp = getRealClientIp(request);
        attributes.put("clientIp", clientIp);
        System.out.println("WebSocket请求 : " + clientIp);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("握手完成");
    }
    private String getRealClientIp(ServerHttpRequest request) {
        if(request instanceof ServletServerHttpRequest)
        {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest)request).getServletRequest();
            String xForwarderFor = httpServletRequest.getHeader("X-Forwarded-For");
            if (xForwarderFor != null && !xForwarderFor.isEmpty() && !"unknow".equalsIgnoreCase(xForwarderFor))
            {
                return xForwarderFor.split(",")[0].trim();
            }
            String xRealIp = httpServletRequest.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                return xRealIp;
            }
            return httpServletRequest.getRemoteAddr();
        }
        return request.getRemoteAddress().getAddress().getHostAddress();
    }
}
