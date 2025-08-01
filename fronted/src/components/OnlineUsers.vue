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
  <div class="flex-1 overflow-y-auto bg-slate-50">
    <div class="p-4 border-b-2 border-slate-300 bg-slate-100 relative">
      <!-- 水墨装饰点 -->
      <div class="absolute top-2 right-2 w-2 h-2 bg-slate-600"></div>
      <h3 class="text-sm font-bold text-slate-800 mb-1 border-b border-slate-400 pb-1">在线用户</h3>
      <p class="text-xs text-slate-600 mt-2">共 {{ users.length }} 位摸鱼同伴</p>
    </div>

    <div class="p-3">
      <div
          v-for="user in users"
          :key="user.id"
          class="flex items-center p-3 bg-white border border-slate-200 hover:border-slate-400 hover:bg-slate-50 transition-all duration-200 group mb-2 relative"
      >
        <!-- 水墨装饰点 -->
        <div class="absolute top-1 right-1 w-1.5 h-1.5 bg-slate-400 group-hover:bg-slate-600"></div>

        <!-- 头像 -->
        <div class="relative flex-shrink-0">
          <img
              :src="user.avatar"
              :alt="user.name"
              class="w-12 h-12 object-cover border-2 border-slate-400 group-hover:border-slate-600 transition-all"
          >
          <!-- 印章效果 -->
          <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-emerald-600"></div>

          <!-- 在线状态指示器 -->
          <div
              class="absolute top-0 right-0 w-3 h-3 border border-white"
              :class="getStatusColor(user.status)"
              :title="getStatusText(user.status)"
          />
          <!-- 匿名标识 -->
          <div
              v-if="user.isAnonymous"
              class="absolute -top-1 -left-1 w-5 h-5 bg-emerald-700 border-2 border-white flex items-center justify-center"
              title="匿名用户"
          >
            <span class="text-white text-xs">🎭</span>
          </div>
        </div>

        <!-- 用户信息 -->
        <div class="ml-4 flex-1 min-w-0">
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center space-x-2">
              <h4 class="font-bold text-slate-800 truncate text-sm">{{ user.name }}</h4>
              <span
                  v-if="user.isAnonymous"
                  class="px-2 py-0.5 bg-emerald-100 text-emerald-800 text-xs border border-emerald-300 font-bold"
              >
                匿名
              </span>
            </div>
          </div>
          <div class="flex items-center justify-between border-l-2 border-slate-300 pl-2">
            <span
                class="text-xs font-bold"
                :class="{
                'text-emerald-700': user.status === 'online',
                'text-amber-700': user.status === 'away',
                'text-red-700': user.status === 'busy'
              }"
            >
              {{ getStatusText(user.status) }}
            </span>
            <span class="text-xs text-slate-600 bg-slate-100 px-2 py-1 border border-slate-300">
              {{ user.joinTime }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="users.length === 0" class="p-8 text-center bg-white border-2 border-slate-200 m-3 relative">
      <!-- 水墨边框装饰 -->
      <div class="absolute -top-1 -left-1 w-4 h-4 border-t-2 border-l-2 border-slate-800"></div>
      <div class="absolute -top-1 -right-1 w-4 h-4 border-t-2 border-r-2 border-slate-800"></div>
      <div class="absolute -bottom-1 -left-1 w-4 h-4 border-b-2 border-l-2 border-slate-800"></div>
      <div class="absolute -bottom-1 -right-1 w-4 h-4 border-b-2 border-r-2 border-slate-800"></div>

      <div class="text-6xl mb-4">🐟</div>
      <div class="text-slate-700 text-sm font-bold border-b border-slate-300 pb-2 mb-2">聊天室空空如也</div>
      <div class="text-slate-600 text-xs">等待摸鱼同伴加入...</div>
    </div>

    <!-- 底部装饰 -->
    <div class="p-4 text-center bg-slate-100 border-t-2 border-slate-300 relative">
      <div class="absolute top-2 left-2 w-2 h-2 bg-slate-600"></div>
      <div class="text-xs text-slate-600 mb-2 font-bold">💡 摸鱼小贴士</div>
      <div class="text-xs text-slate-700 leading-relaxed border-l-2 border-slate-400 pl-2">
        工作累了就来聊天放松一下吧~
      </div>
    </div>
  </div>
</template>
