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
  <div class="bg-white/90 backdrop-blur-sm border-t border-slate-200/60 p-6">
    <div class="max-w-4xl mx-auto">
      <div class="flex items-end space-x-4">
        <!-- 功能按钮 -->
        <div class="flex space-x-2 pb-3">
          <button
              class="p-3 text-slate-400 hover:text-indigo-500 hover:bg-indigo-50 rounded-xl transition-all duration-200 group"
              title="上传文件"
          >
            <svg class="w-6 h-6 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
            </svg>
          </button>
          <button
              class="p-3 text-slate-400 hover:text-purple-500 hover:bg-purple-50 rounded-xl transition-all duration-200 group"
              title="表情"
          >
            <svg class="w-6 h-6 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>
          <button
              class="p-3 text-slate-400 hover:text-green-500 hover:bg-green-50 rounded-xl transition-all duration-200 group"
              title="图片"
          >
            <svg class="w-6 h-6 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </button>
        </div>

        <!-- 输入框 -->
        <div class="flex-1 relative">
          <div class="relative bg-slate-50 rounded-2xl border border-slate-200 focus-within:border-indigo-300 focus-within:ring-2 focus-within:ring-indigo-100 transition-all">
            <textarea
                v-model="messageText"
                placeholder="分享一下你的摸鱼心得..."
                class="w-full resize-none bg-transparent px-4 py-3 focus:outline-none placeholder-slate-400 text-slate-700 leading-relaxed"
                rows="1"
                style="min-height: 48px; max-height: 120px;"
                @keydown="handleKeydown"
                @input="adjustHeight"
            />
            <!-- 提示文字 -->
            <div class="absolute bottom-2 right-3 text-xs text-slate-400 select-none">
              按 Enter 发送，Shift+Enter 换行
            </div>
          </div>
        </div>

        <!-- 发送按钮 -->
        <div class="pb-3">
          <button
              @click="sendMessage"
              :disabled="!messageText.trim() || !isConnected"
              class="bg-gradient-to-r from-indigo-500 to-purple-600 hover:from-indigo-600 hover:to-purple-700 disabled:from-slate-300 disabled:to-slate-400 disabled:cursor-not-allowed text-white px-6 py-3 rounded-2xl transition-all duration-200 font-medium shadow-lg hover:shadow-xl disabled:shadow-none group"
              :title="!isConnected ? '连接已断开' : ''"
          >
            <div class="flex items-center space-x-2">
              <span>发送</span>
              <svg class="w-5 h-5 group-hover:translate-x-0.5 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            </div>
          </button>
        </div>
      </div>

      <!-- 底部提示 -->
      <div class="mt-3 text-center">
        <p class="text-xs text-slate-400">
          <span v-if="!isConnected" class="text-red-500">⚠️ 连接已断开</span>
          <span v-else>💡 温馨提示：理性摸鱼，工作与休息要平衡哦~</span>
        </p>
      </div>
    </div>
  </div>
</template>
