package xiaowu.social_network_demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.model.ConnectionState;
import xiaowu.social_network_demo.utils.Redis;

import java.util.List;
import java.util.Set;

/**
 * 连接状态管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionStateService {

    private final Redis redis;
    private final ChatMessageService chatMessageService;

    /**
     * 处理新连接建立
     */
    public ConnectionResult handleNewConnection(String sessionId, String clientIp, String userAgent) {
        try {
            // 检查是否有历史连接记录
            boolean hasHistory = redis.hasHistoryConnection(clientIp);
            
            // 创建新的连接状态
            ConnectionState connectionState = ConnectionState.builder()
                    .sessionId(sessionId)
                    .clientIp(clientIp)
                    .connectTime(System.currentTimeMillis())
                    .lastActiveTime(System.currentTimeMillis())
                    .status(ConnectionState.ConnectionStatus.ONLINE)
                    .userAgent(userAgent)
                    .reconnectCount(hasHistory ? getReconnectCount(clientIp) + 1 : 0)
                    .build();

            // 保存到Redis
            boolean saved = redis.saveConnectionState(connectionState);
            
            if (!saved) {
                log.warn("⚠️ 连接状态保存失败，但继续处理连接 - SessionId: {}", sessionId);
            }

            // 返回连接结果
            return ConnectionResult.builder()
                    .isReconnection(hasHistory)
                    .reconnectCount(connectionState.getReconnectCount())
                    .recentMessages(hasHistory ? getRecentMessages(clientIp) : List.of())
                    .build();

        } catch (Exception e) {
            log.error("❌ 处理新连接失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
            // 即使Redis操作失败，也返回默认结果，不影响WebSocket连接
            return ConnectionResult.builder()
                    .isReconnection(false)
                    .reconnectCount(0)
                    .recentMessages(List.of())
                    .build();
        }
    }

    /**
     * 处理连接断开
     */
    public void handleConnectionClosed(String sessionId) {
        try {
            // 更新状态为离线
            redis.updateConnectionStatus(sessionId, ConnectionState.ConnectionStatus.OFFLINE);
            
            log.debug(" 连接状态已更新为离线 - SessionId: {}", sessionId);
            
        } catch (Exception e) {
            log.error("❌ 处理连接断开失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 更新连接活跃时间
     */
    public void updateLastActiveTime(String sessionId) {
        try {
            ConnectionState connectionState = redis.getConnectionState(sessionId);
            if (connectionState != null) {
                connectionState.setLastActiveTime(System.currentTimeMillis());
                redis.saveConnectionState(connectionState);
            }
        } catch (Exception e) {
            log.error("❌ 更新活跃时间失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 获取重连次数
     */
    private int getReconnectCount(String clientIp) {
        try {
            Set<String> sessionIds = redis.getSessionIdsByIp(clientIp);
            return sessionIds.stream()
                    .mapToInt(sessionId -> {
                        ConnectionState state = redis.getConnectionState(sessionId);
                        return state != null ? state.getReconnectCount() : 0;
                    })
                    .max()
                    .orElse(0);
        } catch (Exception e) {
            log.error("❌ 获取重连次数失败 - IP: {}, Error: {}", clientIp, e.getMessage());
            return 0;
        }
    }

    /**
     * 获取最近消息
     */
    private List<ChatMessage> getRecentMessages(String clientIp) {
        try {
            // 获取最近10条相关消息
            List<ChatMessage> messages = chatMessageService.getMessagesByIp(clientIp);
            return messages.size() > 10 ? messages.subList(0, 10) : messages;
        } catch (Exception e) {
            log.error("❌ 获取最近消息失败 - IP: {}, Error: {}", clientIp, e.getMessage());
            return List.of();
        }
    }

    /**
     * 连接结果数据传输对象
     */
    @lombok.Data
    @lombok.Builder
    public static class ConnectionResult {
        private boolean isReconnection;
        private int reconnectCount;
        private List<ChatMessage> recentMessages;
    }
}