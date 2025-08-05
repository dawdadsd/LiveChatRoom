// API配置
export const API_CONFIG = {
  // 后端API基础URL
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  
  // 请求超时时间（毫秒）
  TIMEOUT: 10000,
  
  // 令牌刷新阈值（秒）
  TOKEN_REFRESH_THRESHOLD: 300, // 5分钟
  
  // 重试配置
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000,
}

// WebSocket配置
export const WS_CONFIG = {
  // WebSocket服务器URL
  URL: import.meta.env.VITE_WS_URL || 'ws://localhost:8080/chat',
  
  // 重连配置
  RECONNECT_ATTEMPTS: 5,
  RECONNECT_DELAY: 3000,
}

// 开发环境配置
export const DEV_CONFIG = {
  // 是否启用API日志
  ENABLE_API_LOGGING: import.meta.env.DEV,
  
  // 是否启用模拟数据
  ENABLE_MOCK_DATA: false,
  
  // 是否自动测试连接
  AUTO_TEST_CONNECTION: import.meta.env.DEV,
}
