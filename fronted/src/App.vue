<script setup lang="ts">
import { ref, onUnmounted, onMounted } from 'vue'
import UserLogin from './components/UserLogin.vue'
import AuthLogin from './components/AuthLogin.vue'
import UserRegister from './components/UserRegister.vue'
import OnlineUsers from './components/OnlineUsers.vue'
import ChatRoom from './components/ChatRoom.vue'
import DemoUsers from './components/DemoUsers.vue'
import { wsService, type UserInfo, type User } from './services/websocket'
import { authService, type AuthUser } from './services/authService'

// 应用状态
const isLoggedIn = ref(false)
const currentUser = ref<UserInfo | null>(null)
const isConnecting = ref(false)
const connectionError = ref<string | null>(null)
const loginMode = ref<'auth' | 'quick' | 'register'>('auth') // 登录模式：认证登录、快速登录或注册

// 在线用户数据 - 现在从WebSocket获取
const onlineUsers = ref<User[]>([])

// 连接状态
const isConnected = ref(false)

// 认证用户登录
const handleAuthLogin = async (authUser: AuthUser) => {
  const userInfo: UserInfo = {
    id: authUser.id,
    name: authUser.name,
    avatar: authUser.avatar,
    isAnonymous: false,
    userType: authUser.userType,
    department: authUser.department,
    email: authUser.email
  }

  await handleUserLogin(userInfo)
}

// 用户登录（通用）
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

  // 清除认证状态
  authService.logout()

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
  loginMode.value = 'auth' // 重置为认证登录模式
}

// 切换登录模式
const switchToQuickLogin = () => {
  loginMode.value = 'quick'
}

const switchToAuthLogin = () => {
  loginMode.value = 'auth'
}

const switchToRegister = () => {
  loginMode.value = 'register'
}

// 注册成功处理
const handleRegisterSuccess = () => {
  loginMode.value = 'auth'
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

// 组件挂载时检查是否已登录
onMounted(() => {
  const savedUser = authService.getCurrentUser()
  if (savedUser) {
    // 如果有保存的用户信息，自动登录
    handleAuthLogin(savedUser)
  }
})

// 清理资源
onUnmounted(() => {
  wsService.disconnect()
})
</script>

<template>
  <div class="h-screen w-screen bg-stone-50 overflow-hidden">
    <!-- 登录页面 -->
    <AuthLogin
        v-if="!isLoggedIn && loginMode === 'auth'"
        :is-connecting="isConnecting"
        :connection-error="connectionError"
        @user-login="handleAuthLogin"
        @switch-to-quick-login="switchToQuickLogin"
        @switch-to-register="switchToRegister"
    />

    <UserLogin
        v-if="!isLoggedIn && loginMode === 'quick'"
        :is-connecting="isConnecting"
        :connection-error="connectionError"
        @user-login="handleUserLogin"
        @switch-to-auth-login="switchToAuthLogin"
    />

    <!-- 注册页面 -->
    <UserRegister
        v-if="!isLoggedIn && loginMode === 'register'"
        @register-success="handleRegisterSuccess"
        @switch-to-login="switchToAuthLogin"
    />

    <!-- 聊天室主界面 -->
    <div v-else class="flex h-full w-full">
      <!-- 左侧在线用户列表 -->
      <div class="w-80 bg-white border-r-2 border-slate-300 flex flex-col shadow-lg relative">
        <!-- 水墨装饰边框 -->
        <div class="absolute top-0 right-0 w-4 h-4 border-t-2 border-r-2 border-slate-800"></div>
        <div class="absolute bottom-0 right-0 w-4 h-4 border-b-2 border-r-2 border-slate-800"></div>

        <!-- 顶部标题栏 -->
        <div class="h-16 bg-slate-800 flex items-center justify-between px-6 border-b-2 border-slate-600 relative">
          <!-- 印章装饰 -->
          <div class="absolute top-2 right-2 w-3 h-3 bg-emerald-600"></div>

          <div class="flex items-center space-x-3">
            <div class="w-8 h-8 bg-slate-600 border border-slate-400 flex items-center justify-center">
              <span class="text-white text-lg">🐟</span>
            </div>
            <h1 class="text-white text-lg font-bold">摸鱼聊天室</h1>
          </div>
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-2">
              <div :class="['w-2 h-2', isConnected ? 'bg-emerald-400' : 'bg-red-400']"></div>
              <span class="text-slate-200 text-sm font-medium">
                {{ isConnected ? '已连接' : '未连接' }}
              </span>
            </div>
            <div class="text-slate-200 text-sm font-medium border-l border-slate-600 pl-4">
              在线 {{ onlineUsers.length }} 人
            </div>
          </div>
        </div>

        <!-- 当前用户信息 -->
        <div class="p-4 bg-slate-100 border-b-2 border-slate-300 relative">
          <!-- 水墨装饰点 -->
          <div class="absolute top-2 left-2 w-2 h-2 bg-slate-800"></div>

          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="relative">
                <img
                    :src="currentUser?.avatar"
                    :alt="currentUser?.name"
                    class="w-12 h-12 object-cover border-2 border-slate-600"
                >
                <!-- 印章效果 -->
                <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-emerald-600"></div>
              </div>
              <div>
                <div class="font-bold text-slate-800">{{ currentUser?.name }}</div>
                <div class="text-xs text-slate-600 border-l-2 border-slate-400 pl-2">
                  <span v-if="currentUser?.isAnonymous">匿名用户</span>
                  <span v-else-if="currentUser?.userType">
                    {{ currentUser.userType === 'teacher' ? '👨‍🏫 教师' : '🎓 学生' }}
                    <span v-if="currentUser.department" class="block">{{ currentUser.department }}</span>
                  </span>
                  <span v-else>实名用户</span>
                </div>
              </div>
            </div>
            <button
                @click="handleUserLeave"
                class="text-slate-500 hover:text-slate-800 transition-colors p-2 border border-slate-300 hover:border-slate-600"
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
      <div class="flex-1 flex flex-col bg-stone-50 relative">
        <!-- 水墨背景装饰 -->
        <div class="absolute inset-0 opacity-3">
          <div class="absolute top-20 right-20 w-32 h-32 bg-slate-800 rounded-full blur-3xl"></div>
          <div class="absolute bottom-40 left-40 w-24 h-24 bg-slate-600 rounded-full blur-2xl"></div>
        </div>

        <ChatRoom
            :online-users="onlineUsers"
            :current-user="currentUser"
            :is-connected="isConnected"
        />
      </div>
    </div>

    <!-- 演示用户信息组件 -->
    <DemoUsers v-if="!isLoggedIn" />
  </div>
</template>
