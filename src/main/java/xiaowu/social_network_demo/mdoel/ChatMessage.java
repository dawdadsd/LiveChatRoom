package xiaowu.social_network_demo.mdoel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

/**
 * 聊天消息数据模型
 * <p>
 * 📖 统一的消息格式，支持不同类型的消息传输
 */
@Data
@Builder
public class ChatMessage {

    /**
     * 消息唯一标识
     */
    private String messageId;

    /**
     * 发送者IP地址
     */
    private String fromIp;

    /**
     * 发送者会话ID
     */
    private String fromSessionId;

    /**
     * 目标IP地址（点对点消息时使用）
     */
    private String targetIp;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long timestamp;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT,       // 普通文本消息
        SYSTEM,     // 系统消息
        ERROR,      // 错误消息
        HEARTBEAT   // 心跳消息（后续扩展用）
    }
}
