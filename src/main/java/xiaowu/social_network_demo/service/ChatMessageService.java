package xiaowu.social_network_demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.repository.ChatMessageRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聊天消息服务层
 * 
 * 📖 负责消息的持久化和查询操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    // 创建一个大小为10个线程的线程池
    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    /**
     * 保存聊天消息到数据库
     * 
     * @param message 聊天消息对象
     * @return 保存成功返回true，失败返回false
     */
    @Transactional
    public boolean saveMessage(ChatMessage message) {
        try {
            ChatMessage savedMessage = chatMessageRepository.save(message);
            log.debug("💾 消息保存成功 - ID: {}, From: {}, Content: {}", 
                     savedMessage.getMessageId(), message.getFromIp(), message.getContent());
            return true;
        } catch (Exception e) {
            log.error("❌ 消息保存失败 - From: {}, Content: {}, Error: {}", 
                     message.getFromIp(), message.getContent(), e.getMessage());
            return false;
        }
    }

    /**
     * 异步保存消息（不阻塞主流程）
     */
    @Transactional
    public void saveMessageAsync(ChatMessage message) {

        try {
            //TODO 创建新的线程来保存消息
            chatMessageRepository.save(message);

            log.debug("💾 异步消息保存成功 - From: {}", message.getFromIp());
        } catch (Exception e) {
            log.error("❌ 异步消息保存失败 - From: {}, Error: {}", message.getFromIp(), e.getMessage());
        }
    }

    /**
     * 获取用户相关的消息历史
     */
    public List<ChatMessage> getMessagesByIp(String ip) {
        try {
            return chatMessageRepository.findMessagesByIp(ip);
        } catch (Exception e) {
            log.error("❌ 查询消息历史失败 - IP: {}, Error: {}", ip, e.getMessage());
            return List.of();
        }
    }

    /**
     * 获取两个用户之间的对话记录
     */
    public List<ChatMessage> getConversation(String ip1, String ip2) {
        try {
            return chatMessageRepository.findConversationBetweenIps(ip1, ip2);
        } catch (Exception e) {
            log.error("❌ 查询对话记录失败 - IP1: {}, IP2: {}, Error: {}", ip1, ip2, e.getMessage());
            return List.of();
        }
    }

    /**
     * 获取最近的广播消息
     */
    public List<ChatMessage> getRecentBroadcastMessages() {
        try {
            return chatMessageRepository.findRecentBroadcastMessages();
        } catch (Exception e) {
            log.error("❌ 查询广播消息失败 - Error: {}", e.getMessage());
            return List.of();
        }
    }
}