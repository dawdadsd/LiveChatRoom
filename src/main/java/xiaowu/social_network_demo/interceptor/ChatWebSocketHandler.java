package xiaowu.social_network_demo.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.service.ChatMessageService;
import xiaowu.social_network_demo.service.ConnectionManager;
import xiaowu.social_network_demo.service.ConnectionStateService;
import xiaowu.social_network_demo.service.MessageRouter;
import xiaowu.social_network_demo.utils.Redis;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import xiaowu.social_network_demo.model.ConnectionState;


/**
 * WebSocket消息处理器
 *
 * 📖 这是整个实时通信系统的核心调度器
 * 负责连接管理、消息路由、异常处理等关键职责
 private final MessageRouter messageRouter;

 */
@Slf4j
@Component
@Data
public class ChatWebSocketHandler implements WebSocketHandler {
    // Jackson对象映射器，用于JSON序列化/反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConnectionManager connectionManager;

    @Resource
    private final MessageRouter messageRouter;

    @Resource
    private ConnectionStateService connectionStateService;

    @Resource
    private Redis redis;

    //
    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 连接建立成功后回调
     *
     * 📖 这是用户"进入聊天室"的关键时刻
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话属性中获取客户端IP（由拦截器设置）
        String clientIp = (String) session.getAttributes().get("clientIp");
        String sessionId = session.getId();
        String userAgent = (String) session.getAttributes().get("User-Agent");

        System.out.println("🎉 新用户连接 - SessionId: " + sessionId + ", IP: " + clientIp);

        // 将连接信息存入redis中
        try {
            // 处理连接状态管理
            ConnectionStateService.ConnectionResult connectionResult =
                    connectionStateService.handleNewConnection(sessionId, clientIp, userAgent);

            // 将新连接注册到连接管理器
            connectionManager.addConnection(sessionId, session, clientIp);

            // 根据是否为重连决定不同的处理逻辑
            if (connectionResult.isReconnection()) {
                handleReconnection(session, clientIp, connectionResult);
            } else {
                handleFirstConnection(session, clientIp);
            }

        } catch (Exception e) {
            log.error("❌ 连接建立处理异常 - SessionId: {}, Error: {}", sessionId, e.getMessage());
            // 即使Redis操作失败，也要保证基本的WebSocket功能
            handleFirstConnection(session, clientIp);
        }


        // 发送欢迎消息给刚连接的用户
        sendWelcomeMessage(session, clientIp);

        // 通知其他用户有新人加入（如果需要的话）
        broadcastUserJoinMessage(clientIp, sessionId);
    }

    /**
     * 接收并处理客户端消息
     *
     * 📖 这是消息流转的核心枢纽
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String sessionId = session.getId();
        String clientIp = (String) session.getAttributes().get("clientIp");

        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();

            System.out.println("📨 收到消息 - From: " + clientIp + " (" + sessionId + "), Content: " + payload);

            try {
                // 解析消息内容
                ChatMessage chatMessage = parseMessage(payload, clientIp, sessionId);



                chatMessageService.saveMessageAsync(chatMessage);


                // 路由消息到目标用户
                messageRouter.routeMessage(chatMessage);

            } catch (Exception e) {
                System.err.println("❌ 消息处理异常: " + e.getMessage());
                sendErrorMessage(session, "消息处理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 连接传输异常处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        String clientIp = (String) session.getAttributes().get("CLIENT_IP");

        System.err.println("🚨 传输异常 - SessionId: " + sessionId + ", IP: " + clientIp +
                ", Error: " + exception.getMessage());

        // 清理连接
        connectionManager.removeConnection(sessionId);
    }

    /**
     * 连接关闭后处理
     *
     * 📖 用户"离开聊天室"的清理工作
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = session.getId();
        String clientIp = (String) session.getAttributes().get("CLIENT_IP");

        System.out.println("👋 用户断开连接 - SessionId: " + sessionId + ", IP: " + clientIp +
                ", Reason: " + closeStatus.toString());

        // 从连接管理器中移除连接
        connectionManager.removeConnection(sessionId);

        // 从redis中删除连接信息
        redis.remove(sessionId);

        // 通知其他用户有人离开
        broadcastUserLeaveMessage(clientIp, sessionId);
    }

    /**
     * 是否支持部分消息处理
     */
    @Override
    public boolean supportsPartialMessages() {
        // 暂时不支持部分消息，后续可根据需要开启
        return false;
    }

    // ======================== 私有辅助方法 ========================

    /**
     * 解析客户端发送的消息
     *
     * 📖 将原始文本转换为结构化的聊天消息对象
     */
    private ChatMessage parseMessage(String payload, String fromIp, String sessionId) throws IOException {
        try {
            // 尝试解析为JSON格式的消息
            Map<String, Object> messageMap = objectMapper.readValue(payload, Map.class);

            return ChatMessage.builder()
                    .messageId(generateMessageId())
                    .fromIp(fromIp)
                    .SessionId(sessionId)
                    .messageType(getMessageType(messageMap))
                    .content(getMessageContent(messageMap))
                    .targetIp(getTargetIp(messageMap))
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (IOException e) {
            // 如果不是JSON格式，当作纯文本消息处理
            return ChatMessage.builder()
                    .messageId(generateMessageId())
                    .fromIp(fromIp)
                    .SessionId(sessionId)
                    .messageType(ChatMessage.MessageType.TEXT)
                    .content(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * 发送欢迎消息给新连接的用户
     */
    private void sendWelcomeMessage(WebSocketSession session, String clientIp) {
        ChatMessage welcomeMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content("欢迎进入聊天室！您的IP地址是: " + clientIp)
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessageToSession(session, welcomeMessage);
    }

    /**
     * 广播用户加入消息
     */
    private void broadcastUserJoinMessage(String joinedIp, String excludeSessionId) {
        ChatMessage joinMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content("用户 " + joinedIp + " 加入了聊天室")
                .timestamp(System.currentTimeMillis())
                .build();

        messageRouter.broadcastMessage(joinMessage, excludeSessionId);
    }

    /**
     * 广播用户离开消息
     */
    private void broadcastUserLeaveMessage(String leftIp, String excludeSessionId) {
        ChatMessage leaveMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content("用户 " + leftIp + " 离开了聊天室")
                .timestamp(System.currentTimeMillis())
                .build();

        messageRouter.broadcastMessage(leaveMessage, excludeSessionId);
    }

    /**
     * 向特定会话发送消息
     */
    private void sendMessageToSession(WebSocketSession session, ChatMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            System.err.println("❌ 发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送错误消息给客户端
     */
    private void sendErrorMessage(WebSocketSession session, String errorMsg) {
        ChatMessage errorMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.ERROR)
                .content(errorMsg)
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessageToSession(session, errorMessage);
    }

    // ======================== 工具方法 ========================

    private String generateMessageId() {
        return "msg_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }

    private ChatMessage.MessageType getMessageType(Map<String, Object> messageMap) {
        String type = (String) messageMap.getOrDefault("type", "TEXT");
        try {
            return ChatMessage.MessageType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatMessage.MessageType.TEXT;
        }
    }

    private String getMessageContent(Map<String, Object> messageMap) {
        return (String) messageMap.getOrDefault("content", "");
    }

    private String getTargetIp(Map<String, Object> messageMap){
        return (String) messageMap.getOrDefault("targetIp", "");
    }


   /* 首次连接*/
    private void handleFirstConnection(WebSocketSession session, String clientIp) {
        // 发送欢迎消息
        sendWelcomeMessage(session, clientIp);

        // 广播用户加入消息
        broadcastUserJoinMessage(clientIp, session.getId());

        System.out.println(" 首次连接处理完成 - IP: " + clientIp);
    }

   /* 处理重连*/
    private void handleReconnection(WebSocketSession session, String clientIp,
                                    ConnectionStateService.ConnectionResult connectionResult) {
        // 发送重连欢迎消息
        sendReconnectionMessage(session, clientIp, connectionResult.getReconnectCount());

        // 推送最近消息历史（可选）
        if (!connectionResult.getRecentMessages().isEmpty()) {
            sendRecentMessages(session, connectionResult.getRecentMessages());
        }

        // 不广播用户加入消息，避免重复通知
        System.out.println(" 重连处理完成 - IP: " + clientIp + ", 重连次数: " + connectionResult.getReconnectCount());
    }
    /**
     * 发送重连欢迎消息
     */
    private void sendReconnectionMessage(WebSocketSession session, String clientIp, int reconnectCount) {
        ChatMessage reconnectMessage = ChatMessage.builder()
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content(String.format("欢迎回来！您的IP地址是: %s (第%d次重连)", clientIp, reconnectCount + 1))
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessageToSession(session, reconnectMessage);
    }


    /**
     * 发送最近消息历史
     */
    private void sendRecentMessages(WebSocketSession session, List<ChatMessage> recentMessages) {
        // 发送历史消息提示
        ChatMessage historyTip = ChatMessage.builder()
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content(" 以下是您离线期间的消息历史:")
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessageToSession(session, historyTip);

        // 发送历史消息
        recentMessages.forEach(message -> {
            try {
                Thread.sleep(50); // 避免消息发送过快
                sendMessageToSession(session, message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
