package xiaowu.social_network_demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xiaowu.social_network_demo.model.ConnectionState;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Redis {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Redis Key前缀常量
    private static final String CONNECTION_PREFIX = "websocket:connection:";
    private static final String USER_IP_PREFIX = "websocket:user_ip:";
    private static final String SESSION_PREFIX = "websocket:session:";
    
    // 默认过期时间（30分钟）
    private static final long DEFAULT_EXPIRE_MINUTES = 30;

    // ======================== 基础操作方法 ========================
    
    public void set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("❌ Redis SET操作失败 - Key: {}, Error: {}", key, e.getMessage());
        }
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("❌ Redis SET操作失败 - Key: {}, Error: {}", key, e.getMessage());
        }
    }

    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("❌ Redis GET操作失败 - Key: {}, Error: {}", key, e.getMessage());
            return null;
        }
    }

    public void remove(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("❌ Redis DELETE操作失败 - Key: {}, Error: {}", key, e.getMessage());
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("❌ Redis EXISTS操作失败 - Key: {}, Error: {}", key, e.getMessage());
            return false;
        }
    }

    // ======================== 连接状态管理方法 ========================

    /**
     * 保存连接状态到Redis
     */
    public boolean saveConnectionState(ConnectionState connectionState) {
        try {
            String key = CONNECTION_PREFIX + connectionState.getSessionId();
            String value = objectMapper.writeValueAsString(connectionState);
            
            // 设置过期时间
            set(key, value, DEFAULT_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 同时维护IP到SessionId的映射
            String ipKey = USER_IP_PREFIX + connectionState.getClientIp();
            stringRedisTemplate.opsForSet().add(ipKey, connectionState.getSessionId());
            stringRedisTemplate.expire(ipKey, DEFAULT_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.debug(" 连接状态已保存 - SessionId: {}, IP: {}", 
                     connectionState.getSessionId(), connectionState.getClientIp());
            return true;
            
        } catch (JsonProcessingException e) {
            log.error("❌ 连接状态序列化失败 - SessionId: {}, Error: {}", 
                     connectionState.getSessionId(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ 保存连接状态失败 - SessionId: {}, Error: {}", 
                     connectionState.getSessionId(), e.getMessage());
            return false;
        }
    }

    /**
     * 获取连接状态
     */
    public ConnectionState getConnectionState(String sessionId) {
        try {
            String key = CONNECTION_PREFIX + sessionId;
            String value = get(key);
            
            if (value == null) {
                return null;
            }
            
            return objectMapper.readValue(value, ConnectionState.class);
            
        } catch (JsonProcessingException e) {
            log.error("❌ 连接状态反序列化失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("❌ 获取连接状态失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
            return null;
        }
    }

    /**
     * 根据IP获取该IP下的所有连接状态
     */
    public Set<String> getSessionIdsByIp(String clientIp) {
        try {
            String ipKey = USER_IP_PREFIX + clientIp;
            return stringRedisTemplate.opsForSet().members(ipKey);
        } catch (Exception e) {
            log.error("❌ 根据IP获取SessionId失败 - IP: {}, Error: {}", clientIp, e.getMessage());
            return Set.of();
        }
    }

    /**
     * 检查用户是否有历史连接记录
     */
    public boolean hasHistoryConnection(String clientIp) {
        try {
            String ipKey = USER_IP_PREFIX + clientIp;
            Long count = stringRedisTemplate.opsForSet().size(ipKey);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("❌ 检查历史连接失败 - IP: {}, Error: {}", clientIp, e.getMessage());
            return false;
        }
    }

    /**
     * 更新连接状态
     */
    public boolean updateConnectionStatus(String sessionId, ConnectionState.ConnectionStatus status) {
        try {
            ConnectionState connectionState = getConnectionState(sessionId);
            if (connectionState == null) {
                return false;
            }
            
            connectionState.setStatus(status);
            connectionState.setLastActiveTime(System.currentTimeMillis());
            
            return saveConnectionState(connectionState);
            
        } catch (Exception e) {
            log.error("❌ 更新连接状态失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 移除连接状态
     */
    public void removeConnectionState(String sessionId) {
        try {
            // 先获取连接状态以便清理IP映射
            ConnectionState connectionState = getConnectionState(sessionId);
            
            // 删除连接状态
            String key = CONNECTION_PREFIX + sessionId;
            remove(key);
            
            // 清理IP映射
            if (connectionState != null) {
                String ipKey = USER_IP_PREFIX + connectionState.getClientIp();
                stringRedisTemplate.opsForSet().remove(ipKey, sessionId);
            }
            
            log.debug("️ 连接状态已移除 - SessionId: {}", sessionId);
            
        } catch (Exception e) {
            log.error("❌ 移除连接状态失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 延长连接状态过期时间
     */
    public void extendConnectionExpire(String sessionId) {
        try {
            String key = CONNECTION_PREFIX + sessionId;
            stringRedisTemplate.expire(key, DEFAULT_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("❌ 延长连接过期时间失败 - SessionId: {}, Error: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 清理过期的连接记录
     */
    public void cleanExpiredConnections() {
        try {
            Set<String> keys = stringRedisTemplate.keys(CONNECTION_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    if (!exists(key)) {
                        // Key已过期，清理相关数据
                        String sessionId = key.replace(CONNECTION_PREFIX, "");
                        removeConnectionState(sessionId);
                    }
                }
            }
            log.debug(" 过期连接清理完成");
        } catch (Exception e) {
            log.error("❌ 清理过期连接失败 - Error: {}", e.getMessage());
        }
    }
}