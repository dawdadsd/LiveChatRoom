// WebSocket服务类
export interface ChatMessage {
  messageId: string
  fromIp: string
  fromSessionId: string
  targetIp?: string
  messageType: 'TEXT' | 'SYSTEM' | 'ERROR' | 'HEARTBEAT'
  content: string
  timestamp: number
}

export interface User {
  id: number
  name: string
  avatar: string
  status: 'online' | 'away' | 'busy'
  joinTime: string
  isAnonymous: boolean
}

export interface UserInfo {
  id: number
  name: string
  avatar: string
  isAnonymous: boolean
}

export interface FrontendMessage {
  id: number
  text: string
  timestamp: string
  isOwn: boolean
  avatar?: string
  userName?: string
  type: 'user' | 'system' | 'join' | 'leave'
  isAnonymous?: boolean
}

export class WebSocketService {
  private ws: WebSocket | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectInterval = 3000
  private messageHandlers: ((message: ChatMessage) => void)[] = []
  private connectionHandlers: ((connected: boolean) => void)[] = []
  private currentUser: UserInfo | null = null

  constructor(private url: string) {}

  connect(user: UserInfo): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.currentUser = user
        this.ws = new WebSocket(this.url)

        this.ws.onopen = () => {
          console.log('WebSocket连接已建立')
          this.reconnectAttempts = 0
          this.notifyConnectionHandlers(true)
          resolve()
        }

        this.ws.onmessage = (event) => {
          try {
            const message: ChatMessage = JSON.parse(event.data)
            this.notifyMessageHandlers(message)
          } catch (error) {
            console.error('解析消息失败:', error)
          }
        }

        this.ws.onclose = () => {
          console.log('WebSocket连接已关闭')
          this.notifyConnectionHandlers(false)
          this.attemptReconnect()
        }

        this.ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          reject(error)
        }
      } catch (error) {
        reject(error)
      }
    })
  }

  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.currentUser = null
  }

  sendMessage(content: string, targetIp?: string) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.error('WebSocket未连接')
      return
    }

    const message = {
      messageType: 'TEXT',
      content,
      targetIp
    }

    this.ws.send(JSON.stringify(message))
  }

  onMessage(handler: (message: ChatMessage) => void) {
    this.messageHandlers.push(handler)
  }

  onConnection(handler: (connected: boolean) => void) {
    this.connectionHandlers.push(handler)
  }

  private notifyMessageHandlers(message: ChatMessage) {
    this.messageHandlers.forEach(handler => handler(message))
  }

  private notifyConnectionHandlers(connected: boolean) {
    this.connectionHandlers.forEach(handler => handler(connected))
  }

  private attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts && this.currentUser) {
      this.reconnectAttempts++
      console.log(`尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
      
      setTimeout(() => {
        this.connect(this.currentUser!)
      }, this.reconnectInterval)
    }
  }

  isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUser
  }
}

// 动态获取WebSocket连接地址
const getWebSocketUrl = (): string => {
  // 获取当前页面的主机名
  const hostname = window.location.hostname

  // 根据环境确定协议
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'

  // WebSocket服务端口
  const port = '8080'

  // 构建WebSocket URL
  const wsUrl = `${protocol}//${hostname}:${port}/chat`

  console.log('WebSocket连接地址:', wsUrl)
  return wsUrl
}

// 创建全局WebSocket服务实例
export const wsService = new WebSocketService(getWebSocketUrl())
