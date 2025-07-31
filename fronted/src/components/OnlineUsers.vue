<script setup lang="ts">
interface User {
  id: number
  name: string
  avatar: string
  status: 'online' | 'away' | 'busy'
  joinTime: string
  isAnonymous: boolean
}

interface Props {
  users: User[]
}

defineProps<Props>()

const getStatusColor = (status: string) => {
  switch (status) {
    case 'online':
      return 'bg-emerald-400'
    case 'away':
      return 'bg-amber-400'
    case 'busy':
      return 'bg-red-400'
    default:
      return 'bg-slate-400'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'online':
      return '在线'
    case 'away':
      return '离开'
    case 'busy':
      return '忙碌'
    default:
      return '离线'
  }
}
</script>

<template>
  <div class="flex-1 overflow-y-auto">
    <div class="p-4 border-b border-slate-200/60">
      <h3 class="text-sm font-semibold text-slate-700 mb-1">在线用户</h3>
      <p class="text-xs text-slate-500">共 {{ users.length }} 位摸鱼同伴</p>
    </div>

    <div class="p-2">
      <div
          v-for="user in users"
          :key="user.id"
          class="flex items-center p-3 hover:bg-slate-50/80 cursor-pointer rounded-xl transition-all duration-200 group mb-1"
      >
        <!-- 头像 -->
        <div class="relative flex-shrink-0">
          <img
              :src="user.avatar"
              :alt="user.name"
              class="w-12 h-12 rounded-full object-cover shadow-md group-hover:shadow-lg transition-shadow"
          >
          <!-- 在线状态指示器 -->
          <div
              class="absolute -bottom-1 -right-1 w-4 h-4 rounded-full border-2 border-white shadow-sm"
              :class="getStatusColor(user.status)"
              :title="getStatusText(user.status)"
          />
          <!-- 匿名标识 -->
          <div
              v-if="user.isAnonymous"
              class="absolute -top-1 -left-1 w-5 h-5 bg-purple-500 rounded-full flex items-center justify-center border-2 border-white shadow-sm"
              title="匿名用户"
          >
            <span class="text-white text-xs">🎭</span>
          </div>
        </div>

        <!-- 用户信息 -->
        <div class="ml-4 flex-1 min-w-0">
          <div class="flex items-center justify-between mb-1">
            <div class="flex items-center space-x-2">
              <h4 class="font-semibold text-slate-800 truncate text-sm">{{ user.name }}</h4>
              <span
                  v-if="user.isAnonymous"
                  class="px-2 py-0.5 bg-purple-100 text-purple-700 text-xs rounded-full font-medium"
              >
                匿名
              </span>
            </div>
          </div>
          <div class="flex items-center justify-between">
            <span
                class="text-xs font-medium"
                :class="{
                'text-emerald-600': user.status === 'online',
                'text-amber-600': user.status === 'away',
                'text-red-600': user.status === 'busy'
              }"
            >
              {{ getStatusText(user.status) }}
            </span>
            <span class="text-xs text-slate-500 bg-slate-100 px-2 py-1 rounded-lg">
              {{ user.joinTime }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="users.length === 0" class="p-8 text-center">
      <div class="text-6xl mb-4">🐟</div>
      <div class="text-slate-500 text-sm">聊天室空空如也</div>
      <div class="text-slate-400 text-xs mt-1">等待摸鱼同伴加入...</div>
    </div>

    <!-- 底部装饰 -->
    <div class="p-4 text-center">
      <div class="text-xs text-slate-400 mb-2">💡 摸鱼小贴士</div>
      <div class="text-xs text-slate-500 leading-relaxed">
        工作累了就来聊天放松一下吧~
      </div>
    </div>
  </div>
</template>
