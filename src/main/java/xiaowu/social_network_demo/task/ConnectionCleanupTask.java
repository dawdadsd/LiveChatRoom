package xiaowu.social_network_demo.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xiaowu.social_network_demo.utils.Redis;

/**
 * 连接清理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionCleanupTask {

    private final Redis redis;

    /**
     * 每10分钟清理一次过期连接
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void cleanupExpiredConnections() {
        try {
            log.debug(" 开始清理过期连接...");
            redis.cleanExpiredConnections();
            log.debug("✅ 过期连接清理完成");
        } catch (Exception e) {
            log.error("❌ 过期连接清理失败: {}", e.getMessage());
        }
    }
}