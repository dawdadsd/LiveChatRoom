package xiaowu.social_network_demo.mdoel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天消息数据模型
 * <p>
 * 📖 统一的消息格式，支持不同类型的消息传输
 */
@Data
@Entity
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 消息唯一标识
     */
   @Column(name = "messageId", nullable = true, length = 50)
    private String messageId;

    /**
     * 发送者IP地址
     */
    @Column(name = "fromIp", nullable = false, length = 50)
    private String fromIp;
    /**
     *  群组消息发送
     */
    @Column(name = "groupId",nullable = false)
    private Integer groupId;

    /**
     * 发送者会话ID
     */
    @Column(name = "SessionId", nullable = false, length = 50)
    private String SessionId;

    /**
     * 目标IP地址（点对点消息时使用）
     */
    @Column(name = "targetIp", nullable = true, length = 50)
    private String targetIp;

    /**
     * 消息类型
     */
    @Enumerated(EnumType.STRING)//存储枚举的名字
    @Column(name = "messageType", nullable = false, length = 50)
    private MessageType messageType;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT" )
    private String content;

    /**
     * 消息时间戳
     */
    @Column(name = " timestamp", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long timestamp;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT,       // 普通文本消息
        GROUP,
        SYSTEM,     // 系统消息
        ERROR,      // 错误消息
        HEARTBEAT,// 心跳消息（后续扩展用）
        picture,//图片
        file//文件
    }
}
