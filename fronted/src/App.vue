<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import UserLogin from './components/UserLogin.vue'
import OnlineUsers from './components/OnlineUsers.vue'
import ChatRoom from './components/ChatRoom.vue'
import { wsService, type UserInfo, type User } from './services/websocket'

// 应用状态
const isLoggedIn = ref(false)
const currentUser = ref<UserInfo | null>(null)
const isConnecting = ref(false)
const connectionError = ref<string | null>(null)

// 在线用户数据 - 现在从WebSocket获取
const onlineUsers = ref<User[]>([])

// 连接状态
const isConnected = ref(false)

// 用户登录
const handleUserLogin = async (userInfo: UserInfo) => {
  try {
    isConnecting.value = true
    connectionError.value = null
    currentUser.value = userInfo

    // 连接WebSocket
    await wsService.connect(userInfo)

    // 添加当前用户到在线用户列表
    onlineUsers.value.push({
      id: userInfo.id,
      name: userInfo.name,
      avatar: userInfo.avatar,
      status: 'online' as const,
      joinTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      isAnonymous: userInfo.isAnonymous
    })

    isLoggedIn.value = true
    isConnected.value = true
  } catch (error) {
    console.error('连接失败:', error)
    connectionError.value = '连接服务器失败，请稍后重试'
    currentUser.value = null
  } finally {
    isConnecting.value = false
  }
}

// 用户离开
const handleUserLeave = () => {
  // 断开WebSocket连接
  wsService.disconnect()

  if (currentUser.value) {
    const userIndex = onlineUsers.value.findIndex(user => user.id === currentUser.value!.id)
    if (userIndex !== -1) {
      onlineUsers.value.splice(userIndex, 1)
    }
  }

  currentUser.value = null
  isLoggedIn.value = false
  isConnected.value = false
  onlineUsers.value = []
}

// 设置WebSocket连接状态监听
wsService.onConnection((connected) => {
  isConnected.value = connected
  if (!connected && isLoggedIn.value) {
    connectionError.value = '连接已断开，正在尝试重连...'
  } else if (connected) {
    connectionError.value = null
  }
})

// 清理资源
onUnmounted(() => {
  wsService.disconnect()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
    <!-- 登录页面 -->
    <UserLogin
        v-if="!isLoggedIn"
        :is-connecting="isConnecting"
        :connection-error="connectionError"
        @user-login="handleUserLogin"
    />

    <!-- 聊天室主界面 -->
    <div v-else class="flex h-screen">
      <!-- 左侧在线用户列表 -->
      <div class="w-80 bg-white/80 backdrop-blur-sm border-r border-slate-200/60 flex flex-col shadow-xl">
        <!-- 顶部标题栏 -->
        <div class="h-16 bg-gradient-to-r from-indigo-500 to-purple-600 flex items-center justify-between px-6 shadow-lg">
          <div class="flex items-center space-x-3">
            <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
              <span class="text-white text-lg">🐟</span>
            </div>
            <h1 class="text-white text-lg font-semibold">摸鱼聊天室</h1>
          </div>
          <div class="flex items-center space-x-2">
            <div class="flex items-center space-x-1">
              <div :class="['w-2 h-2 rounded-full', isConnected ? 'bg-green-400' : 'bg-red-400']"></div>
              <span class="text-white/90 text-sm font-medium">
                {{ isConnected ? '已连接' : '未连接' }}
              </span>
            </div>
            <div class="text-white/90 text-sm font-medium">
              在线 {{ onlineUsers.length }} 人
            </div>
          </div>
        </div>

        <!-- 当前用户信息 -->
        <div class="p-4 bg-gradient-to-r from-indigo-50 to-purple-50 border-b border-slate-200/60">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <img
                  :src="currentUser?.avatar"
                  :alt="currentUser?.name"
                  class="w-10 h-10 rounded-full object-cover border-2 border-white shadow-md"
              >
              <div>
                <div class="font-semibold text-slate-800">{{ currentUser?.name }}</div>
                <div class="text-xs text-slate-500">
                  {{ currentUser?.isAnonymous ? '匿名用户' : '实名用户' }}
                </div>
              </div>
            </div>
            <button
                @click="handleUserLeave"
                class="text-slate-400 hover:text-slate-600 transition-colors p-2 rounded-lg hover:bg-white/50"
                title="离开聊天室"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 在线用户列表 -->
        <OnlineUsers :users="onlineUsers" />
      </div>

      <!-- 右侧聊天室 -->
      <div class="flex-1 flex flex-col bg-white/50 backdrop-blur-sm">
        <ChatRoom
            :online-users="onlineUsers"
            :current-user="currentUser"
            :is-connected="isConnected"
        />
      </div>
    </div>
  </div>
</template>
