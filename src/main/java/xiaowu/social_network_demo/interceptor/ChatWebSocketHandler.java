package xiaowu.social_network_demo.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.service.ChatMessageService;
import xiaowu.social_network_demo.service.ConnectionManager;
import xiaowu.social_network_demo.service.MessageRouter;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * WebSocket消息处理器
 * 📖 这是整个实时通信系统的核心调度器
 * 负责连接管理、消息路由、异常处理等关键职责
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private static final String ATTR_CLIENT_IP = "clientIp";
    private static final String ATTR_CONNECTION_TIME = "connectionTime";
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_SESSION_ID = "sessionId";
    //消息处理常量
    @Value("${message.max_message_size}")
    private int MAX_MESSAGE_SIZE;
    @Value("${message.send_timeout_seconds}")
    private int SEND_TIMEOUT_SECONDS;
    @Value("${message.ping_interval_seconds}")
    private int PING_INTERVAL_SECONDS;
    private final ConnectionManager connectionManager;
    private final MessageRouter messageRouter;
    // Jackson对象映射器，用于JSON序列化/反序列化
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    /**
     * 连接建立成功后回调
     *
     * 📖 这是用户"进入聊天室"的关键时刻
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        //feature: update websocket connection to now quire;
        String clientIp = getClientIp(session);
        //添加验证连接参数逻辑
        if(!StringUtils.hasText(clientIp) || "UNKNOWN".equals(clientIp))
        {
            log.warn("无效的客户端IP，拒绝连接:sessionId={}", sessionId);
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid client IP"));
            return;
        }
        try {
            connectionManager.addConnection(sessionId, session, clientIp);
            session.getAttributes().put(ATTR_CONNECTION_TIME, Instant.now());
            session.getAttributes().put(ATTR_SESSION_ID,sessionId);
            log.info("ws连接成功 : sessionId = {} ip = {}", sessionId, clientIp);
            // 发送欢迎消息给刚连接的用户
            sendWelcomeMessage(session, clientIp);
            // 通知其他用户有新人加入
            broadcastUserJoinMessage(clientIp, sessionId);
        }catch (Exception e)
        {
            log.error("连接建立失败 : sessionId = {},error = {}",sessionId,e.getMessage(),e);
            session.close(CloseStatus.SERVER_ERROR.withReason("Connection setup failed"));
        }
    }

    /**
     * 接收并处理客户端消息
     *
     * 📖 这是消息流转的核心枢纽
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if(!(message instanceof TextMessage))
        {
            log.warn("不支持的消息类型 :{}",message.getClass().getSimpleName());
            return;
        }
        String sessionId = session.getId();
        String clientIp = getClientIp(session);
        String payload = ((TextMessage) message).getPayload();
            if(payload.length() > MAX_MESSAGE_SIZE)
            {
                log.warn("消息过大 : sessionId = {},size = {}",sessionId,payload.length());

                return;
            }
            log.debug("ws.recv sessionId = {} ip = {} payloadLength = {}",sessionId,clientIp,payload.length());
            try {
                processMessageAsync(sessionId,clientIp,payload);
            } catch (Exception e) {
                log.error("消息处理异常 : sessionId = {},error={}",sessionId,e.getMessage());
                sendErrorMessage(session, "消息处理失败");
        }
    }
    /**
     * 连接传输异常处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        String clientIp = getClientIp(session);
       log.error("WebSocket传输异常 : sessionId = {},clientIp = {},error = {}",sessionId,clientIp,exception.getMessage());
        // 清理连接
        cleanupConnection(sessionId,clientIp,"传输异常:" +exception.getMessage() );
    }

    /**
     * 连接关闭后处理
     *
     * 用户"离开聊天室"的清理工作
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = session.getId();
        String clientIp = (String) session.getAttributes().get("CLIENT_IP");
       log.info("WebSoket连接关闭 : sessionId = {},clientIp = {},status ={}",sessionId,clientIp,closeStatus);
       cleanupConnection(sessionId,clientIp,"连接关闭 :" +closeStatus );
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
     * 清理连接资源
     */
    private void cleanupConnection(String sessionId ,String clientIp,String reason){
        try{
            connectionManager.removeConnection(sessionId);
            if(StringUtils.hasText(clientIp) && !"UNKNOWN".equals(clientIp)){
                broadcastUserLeaveMessage(clientIp,reason);
            }
        }catch (Exception e){
            log.error("连接资源清理失败 : sessionId = {},error = {}",sessionId,e.getMessage(),e);
        }
    }

    /**
     * 异步处理消息和入库
     */
    private void processMessageAsync(String sessionId,String clientIp,String payload){
        CompletableFuture.runAsync(()-> {
                    try {
                        ChatMessage chatMessage = parseMessage(payload, clientIp, sessionId);
                        chatMessageService.saveMessageAsync(chatMessage);
                        messageRouter.routeMessage(chatMessage);
                    } catch (Exception e) {
                        log.error("异步消息处理失败 : sessionId = {},error={}", sessionId, e.getMessage(), e);
                    }
                }
                );
    }
    /**
     * 发送错误消息给客户端
     */
    private void sendErrorMessage(WebSocketSession session,String error)
    {
        ChatMessage errorMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.ERROR)
                .content("错误 :" +error)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        sendMessageSafely(session,errorMessage);
    }

    /**
     * 安全的发送消息到会话中
     */
    private void sendMessageSafely(WebSocketSession session,ChatMessage message)
    {
        try{
            //ConcurrentWebSocketSessionDecorator是线程安全的会话装饰器
            WebSocketSession decoratedSession = new ConcurrentWebSocketSessionDecorator(
                    session,SEND_TIMEOUT_SECONDS * 1000,10 * 1024 *1024
            );
            String jsonMessage = objectMapper.writeValueAsString(message);
            decoratedSession.sendMessage(new TextMessage(jsonMessage));
        }catch (Exception e)
        {
            log.error("发送消息失败 : sessionId = {},error = {}",session.getId(),e.getMessage(),e);
        }
    }
    /**
     * 获取当前会话的消息
     * @param session 当前会话
     * @return 如果当前会话存在则返回当前会话IP，如果不存在，则返回UNKNOWN
     */
    private static String getClientIp(WebSocketSession session){
        Object v = session.getAttributes().get(ATTR_CLIENT_IP);
        return v != null ? v.toString() : "UNKNOWN";
    }

    /**
     * 解析客户端发送的消息
     *
     * 将原始文本转换为结构化的聊天消息对象
     */
    private ChatMessage parseMessage(String payload, String clientIp, String sessionId) throws JsonProcessingException {
        try {
            // 尝试解析为JSON格式的消息
            InboundMessage in = objectMapper.readValue(payload, InboundMessage.class);
            return ChatMessage.builder()
                    .messageId(generateMessageId())
                    .SessionId(sessionId)
                    .fromIp(clientIp)
                    .messageType(in.toMessageType())
                    .content(in.content())
                    .targetIp(in.targetIp)
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

        } catch (JsonProcessingException e) {
            log.debug("消息解析失败，作为纯文本处理:{}",e.getMessage());
            // 如果不是JSON格式，当作纯文本消息处理
            return ChatMessage.builder()
                    .messageId(generateMessageId())
                    .SessionId(sessionId)
                    .fromIp(clientIp)
                    .messageType(ChatMessage.MessageType.TEXT)
                    .content(payload)
                    .timestamp(Instant.now().toEpochMilli())
                    .build();
        }
    }
    public record InboundMessage(String type , String content,String targetIp){
        public ChatMessage.MessageType toMessageType(){
            if(!StringUtils.hasText(type))
            {
                return ChatMessage.MessageType.TEXT;
            }
            try{
                return ChatMessage.MessageType.valueOf(type.toUpperCase());
            }catch (IllegalArgumentException e){
                log.debug("未知的消息类型 :{},默认使用TEXT类型",type);
                return ChatMessage.MessageType.TEXT;
            }
        }
    }
    /**
     * 发送欢迎消息给新连接的用户
     */
    private void sendWelcomeMessage(WebSocketSession session, String clientIp) {
        ChatMessage welcomeMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.SYSTEM)
                //TODO:需要在connectionManager中添加方法查询在线用户
                .content("连接成功，欢迎,当前在线用户"+clientIp)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        sendMessageSafely(session, welcomeMessage);
    }

    /**
     * 广播用户加入消息
     */
    private void broadcastUserJoinMessage(String joinedIp, String excludeSessionId) {
        ChatMessage joinMessage = ChatMessage.builder()
                .messageId(generateMessageId())
                .messageType(ChatMessage.MessageType.SYSTEM)
                .content("用户 " + joinedIp + " 加入了聊天室")
                .timestamp(Instant.now().toEpochMilli())
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
                .timestamp(Instant.now().toEpochMilli())
                .build();
        messageRouter.broadcastMessage(leaveMessage, excludeSessionId);
    }

    // ======================== 工具方法 ========================

    private String generateMessageId() {
       return UUID.randomUUID().toString().replace("-","");
    }

}
