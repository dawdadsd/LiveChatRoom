<script setup lang="ts">
interface Message {
  id: number
  text: string
  timestamp: string
  isOwn: boolean
  avatar?: string
  userName?: string
  type: 'user' | 'system' | 'join' | 'leave'
  isAnonymous?: boolean
}

interface Props {
  message: Message
}

defineProps<Props>()

const getSystemMessageStyle = (type: string) => {
  switch (type) {
    case 'join':
      return 'bg-emerald-50 text-emerald-700 border-emerald-200'
    case 'leave':
      return 'bg-amber-50 text-amber-700 border-amber-200'
    default:
      return 'bg-blue-50 text-blue-700 border-blue-200'
  }
}
</script>

<template>
  <!-- 系统消息 -->
  <div v-if="message.type !== 'user'" class="flex justify-center">
    <div
        class="px-4 py-3 rounded-2xl text-sm border max-w-md backdrop-blur-sm"
        :class="getSystemMessageStyle(message.type)"
    >
      <div class="flex items-center justify-center space-x-2">
        <span v-if="message.type === 'join'">🎉</span>
        <span v-else-if="message.type === 'leave'">👋</span>
        <span v-else>ℹ️</span>
        <span class="font-medium">{{ message.text }}</span>
      </div>
      <div class="text-center text-xs opacity-70 mt-2">{{ message.timestamp }}</div>
    </div>
  </div>

  <!-- 用户消息 -->
  <div v-else class="flex" :class="{ 'justify-end': message.isOwn, 'justify-start': !message.isOwn }">
    <!-- 他人消息 -->
    <div v-if="!message.isOwn" class="flex items-start max-w-xs lg:max-w-md xl:max-w-lg group">
      <div class="relative flex-shrink-0">
        <img
            :src="message.avatar"
            :alt="message.userName || '用户'"
            class="w-10 h-10 rounded-full object-cover shadow-md border-2 border-white group-hover:shadow-lg transition-shadow"
        >
        <!-- 匿名标识 -->
        <div
            v-if="message.isAnonymous"
            class="absolute -top-1 -right-1 w-5 h-5 bg-purple-500 rounded-full flex items-center justify-center border-2 border-white shadow-sm"
            title="匿名用户"
        >
          <span class="text-white text-xs">🎭</span>
        </div>
      </div>

      <div class="ml-3 flex flex-col">
        <!-- 用户名 -->
        <div class="flex items-center space-x-2 mb-2">
          <span class="text-xs font-semibold text-slate-600">{{ message.userName }}</span>
          <span
              v-if="message.isAnonymous"
              class="px-1.5 py-0.5 bg-purple-100 text-purple-600 text-xs rounded-full font-medium"
          >
            匿名
          </span>
        </div>
        <!-- 消息内容 -->
        <div class="bg-white px-4 py-3 rounded-2xl rounded-tl-md shadow-sm border border-slate-200/60 backdrop-blur-sm">
          <p class="text-slate-800 leading-relaxed">{{ message.text }}</p>
        </div>
        <span class="text-xs text-slate-400 mt-2 ml-3">{{ message.timestamp }}</span>
      </div>
    </div>

    <!-- 自己的消息 -->
    <div v-else class="flex items-start max-w-xs lg:max-w-md xl:max-w-lg group">
      <div class="flex flex-col items-end">
        <!-- 用户名 -->
        <div class="flex items-center space-x-2 mb-2">
          <span
              v-if="message.isAnonymous"
              class="px-1.5 py-0.5 bg-purple-100 text-purple-600 text-xs rounded-full font-medium"
          >
            匿名
          </span>
          <span class="text-xs font-semibold text-slate-600">我</span>
        </div>
        <!-- 消息内容 -->
        <div class="bg-gradient-to-r from-indigo-500 to-purple-600 text-white px-4 py-3 rounded-2xl rounded-tr-md shadow-lg">
          <p class="leading-relaxed">{{ message.text }}</p>
        </div>
        <span class="text-xs text-slate-400 mt-2 mr-3">{{ message.timestamp }}</span>
      </div>
    </div>
  </div>
</template>
