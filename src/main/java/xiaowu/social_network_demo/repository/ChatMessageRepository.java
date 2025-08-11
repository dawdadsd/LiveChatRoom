package xiaowu.social_network_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xiaowu.social_network_demo.mdoel.ChatMessage;

import java.util.List;

/**
 * 聊天消息数据访问层
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    /**
     * 根据IP地址查询相关消息（发送或接收）
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.fromIp = :ip OR cm.targetIp = :ip ORDER BY cm.timestamp DESC")
    List<ChatMessage> findMessagesByIp(@Param("ip") String ip);
    
    /**
     * 查询两个IP之间的对话记录
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE (cm.fromIp = :ip1 AND cm.targetIp = :ip2) OR (cm.fromIp = :ip2 AND cm.targetIp = :ip1) ORDER BY cm.timestamp ASC")
    List<ChatMessage> findConversationBetweenIps(@Param("ip1") String ip1, @Param("ip2") String ip2);
    
    /**
     * 查询最近的广播消息
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.targetIp IS NULL ORDER BY cm.timestamp DESC")
    List<ChatMessage> findRecentBroadcastMessages();


    @Query("SELECT cm FROM ChatMessage cm WHERE cm.groupId = :groupId ORDER BY cm.timestamp ASC")
    List<ChatMessage> findGroupMessages(Integer groupId);
}