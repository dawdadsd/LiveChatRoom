<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import MessageBubble from './MessageBubble.vue'
import MessageInput from './MessageInput.vue'
import { wsService, type User, type UserInfo, type FrontendMessage, type ChatMessage } from '../services/websocket'

interface Props {
  onlineUsers: User[]
  currentUser: UserInfo | null
  isConnected: boolean
}

const props = defineProps<Props>()

// 消息列表 - 现在从WebSocket获取
const messages = ref<FrontendMessage[]>([])

// 消息容器引用
const messagesContainer = ref<HTMLElement>()

// 处理WebSocket消息
const handleWebSocketMessage = (message: ChatMessage) => {
  const frontendMessage: FrontendMessage = {
    id: Date.now() + Math.random(), // 确保唯一性
    text: message.content,
    timestamp: new Date(message.timestamp).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isOwn: false, // 从WebSocket收到的消息都不是自己发的
    type: message.messageType === 'SYSTEM' ? 'system' : 'user'
  }

  // 如果是用户消息，需要从IP推断用户信息（这里简化处理）
  if (message.messageType === 'TEXT') {
    frontendMessage.userName = `用户${message.fromIp.split('.').pop()}`
    frontendMessage.avatar = `https://images.unsplash.com/photo-${1500000000000 + Math.floor(Math.random() * 100000000)}?w=150&h=150&fit=crop&crop=face`
    frontendMessage.isAnonymous = false
  }

  messages.value.push(frontendMessage)
  scrollToBottom()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = (text: string) => {
  if (!props.currentUser || !props.isConnected) return

  // 立即显示自己的消息
  const newMessage: FrontendMessage = {
    id: Date.now(),
    text,
    timestamp: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isOwn: true,
    avatar: props.currentUser.avatar,
    userName: props.currentUser.name,
    type: 'user',
    isAnonymous: props.currentUser.isAnonymous
  }
  messages.value.push(newMessage)
  scrollToBottom()

  // 通过WebSocket发送消息
  wsService.sendMessage(text)
}

onMounted(() => {
  scrollToBottom()

  // 设置WebSocket消息监听
  wsService.onMessage(handleWebSocketMessage)
})

onUnmounted(() => {
  // 清理监听器（如果需要的话）
})
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- 顶部标题栏 -->
    <div class="h-16 bg-white/90 backdrop-blur-sm border-b border-slate-200/60 flex items-center px-6 shadow-sm">
      <div class="flex items-center space-x-4">
        <div class="w-10 h-10 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-full flex items-center justify-center shadow-lg">
          <span class="text-white text-lg">🐟</span>
        </div>
        <div>
          <h2 class="text-lg font-semibold text-slate-800">摸鱼聊天室</h2>
          <p class="text-sm text-slate-500">{{ onlineUsers.length }} 位摸鱼同伴在线 · 工作之余，轻松畅聊</p>
        </div>
      </div>
    </div>

    <!-- 消息区域 -->
    <div
        ref="messagesContainer"
        class="flex-1 overflow-y-auto p-6 space-y-4 bg-gradient-to-b from-slate-50/50 to-slate-100/50"
        style="background-image: radial-gradient(circle at 25% 25%, rgba(139, 92, 246, 0.03) 0%, transparent 50%), radial-gradient(circle at 75% 75%, rgba(59, 130, 246, 0.03) 0%, transparent 50%)"
    >
      <MessageBubble
          v-for="message in messages"
          :key="message.id"
          :message="message"
      />

      <!-- 消息区域底部间距 -->
      <div class="h-4" />
    </div>

    <!-- 输入区域 -->
    <MessageInput :is-connected="isConnected" @send-message="sendMessage" />
  </div>
</template>
