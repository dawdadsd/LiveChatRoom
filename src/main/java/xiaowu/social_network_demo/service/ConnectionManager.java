package xiaowu.social_network_demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket连接管理器
 * 面试官可能问的问题 ： 或者你在回答ConcurrentHashMap时候如何回答？
 * 示例:
 *      我在这个社交网络系统的时候，因为我们虽然用户不多，对于并发问题不多见，
 *      但是其实我们还是去做防止出现并发问题的Websocket连接
 */
@Slf4j
@Service
public class ConnectionManager {

    // 主存储：SessionId -> WebSocketSession
    // 提供了通过唯一ID快速查找会话的能力
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // 辅助索引：IP -> Set<SessionId>
    // 允许我们快速找到一个IP地址下的所有会话，用于IP间对话
    private final ConcurrentHashMap<String, Set<String>> ipToSessionIdsMap = new ConcurrentHashMap<>();

    // 辅助反向索引：SessionId -> IP
    // 在断开连接时，能通过SessionId快速找到IP，以便清理ipToSessionIdsMap
    private final ConcurrentHashMap<String, String> sessionIdToIpMap = new ConcurrentHashMap<>();

    /**
     * 添加一个新的连接
     * @param sessionId 会话ID
     * @param session WebSocket会话对象
     * @param clientIp 客户端IP
     */
    public void addConnection(String sessionId, WebSocketSession session, String clientIp) {
       //这里没必要加锁
        sessionMap.put(sessionId,session);
        sessionIdToIpMap.put(sessionId,clientIp);
        ipToSessionIdsMap.computeIfAbsent(clientIp,k-> ConcurrentHashMap.newKeySet()).add(sessionId);
        //为什么没必要加锁？
        //因为ConcurrentHashMap中的put 和 computeIfAbsent是原子性的，这样可以产生性能优化
        log.info("添加连接成功");
    }

    /**
     * 移除一个连接
     * @param sessionId 会话ID
     */
    public void removeConnection(String sessionId) {
        if (!sessionMap.containsKey(sessionId)) {
            return;
        }
        // 原子性地移除所有相关映射
        synchronized (this) {
            sessionMap.remove(sessionId);
            String clientIp = sessionIdToIpMap.remove(sessionId);
            if (clientIp != null) {
                Set<String> sessionIds = ipToSessionIdsMap.get(clientIp);
                if (sessionIds != null) {
                    sessionIds.remove(sessionId);
                    // 如果该IP下已无任何会话，则从map中移除该IP条目，防止内存泄漏
                    if (sessionIds.isEmpty()) {
                        ipToSessionIdsMap.remove(clientIp);
                    }
                }
            }
        }
        System.out.println("🗑️ 连接管理器: 移除连接 " + sessionId + ", Total Sessions: " + sessionMap.size());
    }
    /**
     * 根据SessionId获取会话
     * @param sessionId 会话ID
     * @return WebSocketSession, or null if not found
     */
    public WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    /**
     * 根据IP地址获取所有会话
     * @param ip 客户端IP
     * @return 该IP下的所有WebSocketSession集合
     */
    public Set<WebSocketSession> getSessionsByIp(String ip) {
        Set<String> sessionIds = ipToSessionIdsMap.getOrDefault(ip, Collections.emptySet());
        return sessionIds.stream()
                .map(sessionMap::get)
                .filter(java.util.Objects::nonNull) // 过滤掉可能因时序问题已移除的session
                .collect(Collectors.toSet());
    }

    /**
     * 获取所有活跃的会话
     * @return 所有WebSocketSession的集合
     */
    public Set<WebSocketSession> getAllSessions() {
        return Set.copyOf(sessionMap.values());
    }
}
