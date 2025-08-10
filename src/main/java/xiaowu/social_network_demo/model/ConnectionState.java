package xiaowu.social_network_demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket连接状态模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionState {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 客户端IP地址
     */
    private String clientIp;
    
    /**
     * 连接建立时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long connectTime;
    
    /**
     * 最后活跃时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long lastActiveTime;
    
    /**
     * 连接状态
     */
    private ConnectionStatus status;
    
    /**
     * 用户代理信息（可选）
     */
    private String userAgent;
    
    /**
     * 重连次数
     */
    private Integer reconnectCount;
    
    /**
     * 连接状态枚举
     */
    public enum ConnectionStatus {
        ONLINE,     // 在线
        OFFLINE,    // 离线
        RECONNECTING // 重连中
    }
}