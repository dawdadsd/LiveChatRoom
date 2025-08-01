<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  isConnected?: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  sendMessage: [text: string]
}>()

const messageText = ref('')

const sendMessage = () => {
  const text = messageText.value.trim()
  if (text) {
    emit('sendMessage', text)
    messageText.value = ''
  }
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 自动调整文本框高度
const adjustHeight = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement
  textarea.style.height = 'auto'
  textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
}
</script>

<template>
  <div class="bg-white border-t-2 border-slate-300 p-6 relative">
    <!-- 水墨装饰边框 -->
    <div class="absolute top-0 left-4 w-8 h-1 bg-slate-800"></div>
    <div class="absolute top-0 right-4 w-8 h-1 bg-slate-800"></div>

    <div class="max-w-4xl mx-auto">
      <div class="flex items-end space-x-4">
        <!-- 功能按钮 -->
        <div class="flex space-x-2 pb-3">
          <button
              class="p-3 text-slate-500 hover:text-slate-800 hover:bg-slate-100 border border-slate-300 hover:border-slate-600 transition-all duration-200 group relative"
              title="上传文件"
          >
            <div class="absolute top-0 right-0 w-1.5 h-1.5 bg-slate-400 group-hover:bg-slate-600"></div>
            <svg class="w-5 h-5 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
            </svg>
          </button>
          <button
              class="p-3 text-slate-500 hover:text-emerald-800 hover:bg-emerald-100 border border-slate-300 hover:border-emerald-600 transition-all duration-200 group relative"
              title="表情"
          >
            <div class="absolute top-0 right-0 w-1.5 h-1.5 bg-emerald-400 group-hover:bg-emerald-600"></div>
            <svg class="w-5 h-5 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>
          <button
              class="p-3 text-slate-500 hover:text-slate-800 hover:bg-slate-100 border border-slate-300 hover:border-slate-600 transition-all duration-200 group relative"
              title="图片"
          >
            <div class="absolute top-0 right-0 w-1.5 h-1.5 bg-slate-400 group-hover:bg-slate-600"></div>
            <svg class="w-5 h-5 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </button>
        </div>

        <!-- 输入框 -->
        <div class="flex-1 relative">
          <div class="relative bg-slate-50 border-2 border-slate-300 focus-within:border-slate-800 transition-all">
            <!-- 水墨装饰线 -->
            <div class="absolute bottom-0 left-0 h-0.5 bg-slate-800 transition-all duration-300"
                 :class="messageText ? 'w-full' : 'w-0'"></div>

            <textarea
                v-model="messageText"
                placeholder="分享一下你的摸鱼心得..."
                class="w-full resize-none bg-transparent px-4 py-3 focus:outline-none placeholder-slate-500 text-slate-800 leading-relaxed font-medium"
                rows="1"
                style="min-height: 48px; max-height: 120px;"
                @keydown="handleKeydown"
                @input="adjustHeight"
            />
            <!-- 提示文字 -->
            <div class="absolute bottom-2 right-3 text-xs text-slate-500 select-none border-l border-slate-300 pl-2">
              按 Enter 发送，Shift+Enter 换行
            </div>
          </div>
        </div>

        <!-- 发送按钮 -->
        <div class="pb-3">
          <button
              @click="sendMessage"
              :disabled="!messageText.trim() || !isConnected"
              class="bg-slate-800 hover:bg-slate-900 disabled:bg-slate-400 disabled:cursor-not-allowed text-white px-6 py-3 border-2 border-slate-600 hover:border-slate-800 disabled:border-slate-300 transition-all duration-200 font-bold shadow-lg hover:shadow-xl disabled:shadow-none group relative overflow-hidden"
              :title="!isConnected ? '连接已断开' : ''"
          >
            <!-- 水墨扩散效果 -->
            <div class="absolute inset-0 bg-emerald-600 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"></div>

            <div class="flex items-center space-x-2 relative z-10">
              <span>发送</span>
              <svg class="w-5 h-5 group-hover:translate-x-0.5 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            </div>

            <!-- 印章装饰 -->
            <div class="absolute top-1 right-1 w-2 h-2 bg-emerald-400 group-disabled:bg-slate-300"></div>
          </button>
        </div>
      </div>

      <!-- 底部提示 -->
      <div class="mt-4 text-center border-t border-slate-200 pt-3 relative">
        <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-8 h-0.5 bg-slate-800"></div>
        <p class="text-xs text-slate-600 font-medium">
          <span v-if="!isConnected" class="text-red-700 border-l-2 border-red-600 pl-2">⚠️ 连接已断开</span>
          <span v-else class="border-l-2 border-emerald-600 pl-2">💡 温馨提示：理性摸鱼，工作与休息要平衡哦~</span>
        </p>
      </div>
    </div>
  </div>
</template>
