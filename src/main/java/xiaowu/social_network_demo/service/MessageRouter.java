package xiaowu.social_network_demo.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xiaowu.social_network_demo.mdoel.ChatMessage;

import java.io.IOException;

/**
 * 消息路由器
 *
 * 📖 负责将消息路由到正确的目的地。
 * 它是业务逻辑和底层WebSocket连接之间的桥梁。
 */
@Service
@Slf4j
@AllArgsConstructor
public class MessageRouter {
    private ConnectionManager connectionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 主路由方法
     *
     * 📖 根据消息内容决定是广播还是点对点发送
     * @param message 聊天消息对象
     */
    public void routeMessage(ChatMessage message) {
        String targetIp = message.getTargetIp();
        if (targetIp != null && !targetIp.isBlank()) {
            // 点对点消息
            sendToIp(targetIp, message);
        } else {
            // 广播消息
            broadcastMessage(message, message.getSessionId());
        }
    }
    /**
     * 广播消息给所有连接的用户（可选择排除某个会话）
     *
     * @param message 聊天消息
     * @param excludeSessionId 要排除的会话ID（通常是发送者自己）
     */
    public void broadcastMessage(ChatMessage message, String excludeSessionId) {
        log.info("将消息对象序列化为JSON字符串");
        String messageJson = serializeMessage(message);
        if (messageJson == null) return;
        TextMessage textMessage = new TextMessage(messageJson);
       log.info("消息内容 : {}" , textMessage );
        connectionManager.getAllSessions().forEach(session -> {
            // 排除发送者自己
            if (!session.getId().equals(excludeSessionId)) {
                sendMessage(session, textMessage);
            }
        });
    }

    /**
     * 发送消息到指定IP的所有会话
     *
     * @param targetIp 目标IP地址
     * @param message 聊天消息
     */
    public void sendToIp(String targetIp, ChatMessage message) {
        String messageJson = serializeMessage(message);
        if (messageJson == null) return;
        TextMessage textMessage = new TextMessage(messageJson);
        log.info("实现消息的点对点发送");
        connectionManager.getSessionsByIp(targetIp).forEach(session -> {
            sendMessage(session, textMessage);
        });
    }

    /**
     * 核心发送逻辑
     *
     * @param session 目标会话
     * @param message 已经封装好的TextMessage
     */
    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            // 检查会话是否仍然打开
            if (session.isOpen()) {
                // 同步发送消息。对于高吞吐量场景，可考虑使用异步发送或队列
                synchronized(session) { // 对session加锁，防止多个线程同时写一个session
                    session.sendMessage(message);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ 发送消息异常: " + e.getMessage() + " to session " + session.getId());
            // 可以在这里触发连接清理逻辑
            connectionManager.removeConnection(session.getId());
        }
    }

    /**
     * 将ChatMessage对象序列化为JSON字符串
     * @param message 消息对象
     * @return JSON字符串, or null if serialization fails
     */
    public String serializeMessage(ChatMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.warn("将消息初始化失败");
            throw new IllegalArgumentException("消息初始化失败");
        }
    }
}
