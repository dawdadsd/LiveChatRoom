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
      return 'bg-emerald-50 text-emerald-800 border-2 border-emerald-400'
    case 'leave':
      return 'bg-slate-100 text-slate-800 border-2 border-slate-400'
    default:
      return 'bg-slate-50 text-slate-800 border-2 border-slate-300'
  }
}
</script>

<template>
  <!-- 系统消息 -->
  <div v-if="message.type !== 'user'" class="flex justify-center">
    <div
        class="px-4 py-3 text-sm max-w-md relative"
        :class="getSystemMessageStyle(message.type)"
    >
      <!-- 水墨装饰点 -->
      <div class="absolute top-1 right-1 w-1.5 h-1.5 bg-slate-600"></div>

      <div class="flex items-center justify-center space-x-2">
        <span v-if="message.type === 'join'">🎉</span>
        <span v-else-if="message.type === 'leave'">👋</span>
        <span v-else>ℹ️</span>
        <span class="font-bold">{{ message.text }}</span>
      </div>
      <div class="text-center text-xs mt-2 border-t border-current opacity-70 pt-2">{{ message.timestamp }}</div>
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
            class="w-12 h-12 object-cover border-2 border-slate-400 group-hover:border-slate-600 transition-all"
        >
        <!-- 印章效果 -->
        <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-emerald-600"></div>

        <!-- 匿名标识 -->
        <div
            v-if="message.isAnonymous"
            class="absolute -top-1 -left-1 w-5 h-5 bg-emerald-700 border-2 border-white flex items-center justify-center"
            title="匿名用户"
        >
          <span class="text-white text-xs">🎭</span>
        </div>
      </div>

      <div class="ml-3 flex flex-col">
        <!-- 用户名 -->
        <div class="flex items-center space-x-2 mb-2">
          <span class="text-xs font-bold text-slate-700 border-b border-slate-300 pb-0.5">{{ message.userName }}</span>
          <span
              v-if="message.isAnonymous"
              class="px-2 py-0.5 bg-emerald-100 text-emerald-800 text-xs border border-emerald-300 font-bold"
          >
            匿名
          </span>
        </div>
        <!-- 消息内容 -->
        <div class="bg-white px-4 py-3 border-2 border-slate-200 relative">
          <!-- 水墨装饰点 -->
          <div class="absolute top-1 right-1 w-1.5 h-1.5 bg-slate-400"></div>
          <p class="text-slate-800 leading-relaxed">{{ message.text }}</p>
        </div>
        <span class="text-xs text-slate-500 mt-2 ml-3 border-l-2 border-slate-300 pl-2">{{ message.timestamp }}</span>
      </div>
    </div>

    <!-- 自己的消息 -->
    <div v-else class="flex items-start max-w-xs lg:max-w-md xl:max-w-lg group">
      <div class="flex flex-col items-end">
        <!-- 用户名 -->
        <div class="flex items-center space-x-2 mb-2">
          <span
              v-if="message.isAnonymous"
              class="px-2 py-0.5 bg-emerald-100 text-emerald-800 text-xs border border-emerald-300 font-bold"
          >
            匿名
          </span>
          <span class="text-xs font-bold text-slate-700 border-b border-slate-300 pb-0.5">我</span>
        </div>
        <!-- 消息内容 -->
        <div class="bg-slate-800 text-white px-4 py-3 border-2 border-slate-600 relative overflow-hidden">
          <!-- 水墨扩散效果 -->
          <div class="absolute inset-0 bg-emerald-600 opacity-10"></div>
          <p class="leading-relaxed relative z-10">{{ message.text }}</p>
          <!-- 印章装饰 -->
          <div class="absolute top-1 right-1 w-2 h-2 bg-emerald-400"></div>
        </div>
        <span class="text-xs text-slate-500 mt-2 mr-3 border-r-2 border-slate-300 pr-2">{{ message.timestamp }}</span>
      </div>
    </div>
  </div>
</template>
