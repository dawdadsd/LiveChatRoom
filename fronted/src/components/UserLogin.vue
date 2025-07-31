<script setup lang="ts">
import { ref } from 'vue'

interface UserInfo {
  id: number
  name: string
  avatar: string
  isAnonymous: boolean
}

interface Props {
  isConnecting?: boolean
  connectionError?: string | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  userLogin: [userInfo: UserInfo]
}>()

const userName = ref('')
const selectedAvatar = ref('')
const isAnonymous = ref(false)
const step = ref(1) // 1: 选择身份, 2: 填写信息

// 预设头像
const avatarOptions = [
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=150&h=150&fit=crop&crop=face',
  'https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=150&h=150&fit=crop&crop=face'
]

// 匿名用户默认头像
const anonymousAvatars = [
  'https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=150&h=150&fit=crop&crop=face', // 猫
  'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=150&h=150&fit=crop&crop=face', // 狗
  'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=150&h=150&fit=crop&crop=face', // 鸟
  'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=150&h=150&fit=crop&crop=face'  // 熊猫
]

// 选择身份类型
const selectIdentityType = (anonymous: boolean) => {
  isAnonymous.value = anonymous
  if (anonymous) {
    // 匿名用户随机选择头像和名称
    const anonymousNames = ['摸鱼者', '划水怪', '神秘人', '路人甲', '匿名用户', '神秘客']
    userName.value = anonymousNames[Math.floor(Math.random() * anonymousNames.length)]
    selectedAvatar.value = anonymousAvatars[Math.floor(Math.random() * anonymousAvatars.length)]
  } else {
    userName.value = ''
    selectedAvatar.value = ''
  }
  step.value = 2
}

// 返回第一步
const goBack = () => {
  step.value = 1
  userName.value = ''
  selectedAvatar.value = ''
}

// 确认进入聊天室
const joinChatRoom = () => {
  if (!userName.value.trim() || !selectedAvatar.value) {
    return
  }

  const userInfo: UserInfo = {
    id: Date.now(),
    name: userName.value.trim(),
    avatar: selectedAvatar.value,
    isAnonymous: isAnonymous.value
  }

  emit('userLogin', userInfo)
}

// 随机选择头像
const randomAvatar = () => {
  const options = isAnonymous.value ? anonymousAvatars : avatarOptions
  selectedAvatar.value = options[Math.floor(Math.random() * options.length)]
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-indigo-100 via-purple-50 to-pink-100">
    <div class="max-w-md w-full">
      <!-- 步骤1: 选择身份类型 -->
      <div v-if="step === 1" class="bg-white/90 backdrop-blur-sm rounded-3xl shadow-2xl p-8 border border-white/20">
        <!-- 标题 -->
        <div class="text-center mb-8">
          <div class="w-20 h-20 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg">
            <span class="text-3xl">🐟</span>
          </div>
          <h1 class="text-3xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent mb-2">
            摸鱼聊天室
          </h1>
          <p class="text-slate-600">选择你的身份，开始愉快摸鱼~</p>
        </div>

        <!-- 身份选择 -->
        <div class="space-y-4">
          <!-- 实名身份 -->
          <button
              @click="selectIdentityType(false)"
              class="w-full p-6 bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl hover:from-blue-100 hover:to-indigo-100 hover:border-blue-300 transition-all duration-300 group"
          >
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center text-white text-xl group-hover:scale-110 transition-transform">
                👤
              </div>
              <div class="text-left">
                <div class="font-semibold text-slate-800 text-lg">实名身份</div>
                <div class="text-slate-600 text-sm">使用真实姓名，展示真实的自己</div>
              </div>
            </div>
          </button>

          <!-- 匿名身份 -->
          <button
              @click="selectIdentityType(true)"
              class="w-full p-6 bg-gradient-to-r from-purple-50 to-pink-50 border-2 border-purple-200 rounded-2xl hover:from-purple-100 hover:to-pink-100 hover:border-purple-300 transition-all duration-300 group"
          >
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 bg-purple-500 rounded-full flex items-center justify-center text-white text-xl group-hover:scale-110 transition-transform">
                🎭
              </div>
              <div class="text-left">
                <div class="font-semibold text-slate-800 text-lg">匿名身份</div>
                <div class="text-slate-600 text-sm">神秘登场，自由畅聊无负担</div>
              </div>
            </div>
          </button>
        </div>
      </div>

      <!-- 步骤2: 填写用户信息 -->
      <div v-else class="bg-white/90 backdrop-blur-sm rounded-3xl shadow-2xl p-8 border border-white/20">
        <!-- 标题 -->
        <div class="text-center mb-6">
          <div class="flex items-center justify-center space-x-2 mb-4">
            <span class="text-2xl">{{ isAnonymous ? '🎭' : '👤' }}</span>
            <h2 class="text-2xl font-bold text-slate-800">
              {{ isAnonymous ? '匿名身份' : '实名身份' }}
            </h2>
          </div>
          <p class="text-slate-600">
            {{ isAnonymous ? '系统已为你生成神秘身份' : '请完善你的个人信息' }}
          </p>
        </div>

        <!-- 头像选择 -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-slate-700 mb-3">选择头像</label>
          <div class="flex items-center space-x-4 mb-4">
            <div class="w-16 h-16 rounded-full border-3 border-dashed border-slate-300 flex items-center justify-center overflow-hidden">
              <img
                  v-if="selectedAvatar"
                  :src="selectedAvatar"
                  alt="选中的头像"
                  class="w-full h-full object-cover"
              >
              <span v-else class="text-slate-400 text-sm">未选择</span>
            </div>
            <button
                @click="randomAvatar"
                class="px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-xl hover:from-indigo-600 hover:to-purple-600 transition-all shadow-lg text-sm font-medium"
            >
              🎲 随机头像
            </button>
          </div>

          <!-- 头像网格 -->
          <div class="grid grid-cols-4 gap-3">
            <button
                v-for="avatar in (isAnonymous ? anonymousAvatars : avatarOptions)"
                :key="avatar"
                @click="selectedAvatar = avatar"
                class="w-full aspect-square rounded-xl overflow-hidden border-2 transition-all hover:scale-105"
                :class="selectedAvatar === avatar ? 'border-indigo-500 ring-2 ring-indigo-200' : 'border-slate-200 hover:border-slate-300'"
            >
              <img :src="avatar" alt="头像选项" class="w-full h-full object-cover">
            </button>
          </div>
        </div>

        <!-- 姓名输入 -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-slate-700 mb-2">
            {{ isAnonymous ? '昵称' : '姓名' }}
          </label>
          <input
              v-model="userName"
              type="text"
              :placeholder="isAnonymous ? '你的神秘昵称...' : '请输入你的姓名...'"
              :disabled="isAnonymous"
              class="w-full px-4 py-3 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-lg disabled:bg-slate-100 disabled:text-slate-600"
          >
        </div>

        <!-- 按钮组 -->
        <div class="flex space-x-3">
          <button
              @click="goBack"
              class="flex-1 py-3 px-4 border border-slate-300 text-slate-700 rounded-xl hover:bg-slate-50 transition-all font-medium"
          >
            返回
          </button>
          <button
              @click="joinChatRoom"
              :disabled="!userName.trim() || !selectedAvatar || isConnecting"
              class="flex-1 py-3 px-4 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-xl hover:from-indigo-600 hover:to-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium shadow-lg"
          >
            <span v-if="isConnecting" class="flex items-center justify-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              连接中...
            </span>
            <span v-else>进入聊天室 🐟</span>
          </button>
        </div>

        <!-- 连接错误提示 -->
        <div v-if="connectionError" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
          <p class="text-red-600 text-sm">{{ connectionError }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
