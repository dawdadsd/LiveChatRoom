<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { authService, type LoginCredentials, type AuthUser } from '../services/authService'
import { validateLoginForm, debounce } from '../utils/validationUtils'

interface Props {
  isConnecting?: boolean
  connectionError?: string | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  userLogin: [user: AuthUser]
  switchToQuickLogin: []
  switchToRegister: []
}>()

// 表单数据
const formData = reactive<LoginCredentials>({
  userType: 'STUDENT',
  username: '',
  password: '',
  rememberMe: false
})

// 表单状态
const isLoading = ref(false)
const showPassword = ref(false)
const errors = reactive<Record<string, string>>({})
const loginError = ref<string | null>(null)

// 实时验证状态
const isValidating = ref(false)
const hasInteracted = reactive({
  username: false,
  password: false
})

// 计算属性
const isFormValid = computed(() => {
  return formData.userType &&
         formData.username.trim() &&
         formData.password.trim() &&
         Object.keys(errors).length === 0
})

const userTypeOptions = [
  { value: 'STUDENT', label: '学生', icon: '🎓', color: 'from-blue-500 to-indigo-500' },
  { value: 'TEACHER', label: '教师', icon: '👨‍🏫', color: 'from-purple-500 to-pink-500' }
]

// 防抖验证函数
const debouncedValidate = debounce(() => {
  validateForm()
}, 300)

// 表单验证
const validateForm = () => {
  const result = validateLoginForm(formData)
  
  // 清空之前的错误
  Object.keys(errors).forEach(key => {
    delete errors[key]
  })
  
  // 设置新的错误
  Object.assign(errors, result.errors)
  
  return result.isValid
}

// 字段失焦验证
const handleFieldBlur = (fieldName: keyof typeof hasInteracted) => {
  hasInteracted[fieldName] = true
  validateForm()
}

// 实时输入验证
const handleFieldInput = (fieldName: keyof typeof hasInteracted) => {
  if (hasInteracted[fieldName]) {
    debouncedValidate()
  }
}

// 登录处理
const handleLogin = async () => {
  // 标记所有字段为已交互
  Object.keys(hasInteracted).forEach(key => {
    hasInteracted[key as keyof typeof hasInteracted] = true
  })

  if (!validateForm()) {
    return
  }

  isLoading.value = true
  loginError.value = null

  try {
    const response = await authService.login(formData)
    
    if (response.success && response.user) {
      emit('userLogin', response.user)
    } else {
      loginError.value = response.message || '登录失败，请重试'
    }
  } catch (error) {
    console.error('登录错误:', error)
    loginError.value = '网络错误，请检查连接后重试'
  } finally {
    isLoading.value = false
  }
}

// 切换到快速登录
const switchToQuickLogin = () => {
  emit('switchToQuickLogin')
}

// 切换到注册
const switchToRegister = () => {
  emit('switchToRegister')
}

// 组件挂载时恢复记住的用户信息
onMounted(() => {
  const remembered = authService.getRememberedUser()
  if (remembered) {
    formData.userType = remembered.userType
    formData.username = remembered.username
    formData.rememberMe = true
  }
})
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 bg-stone-50 relative">
    <!-- 水墨背景装饰 -->
    <div class="absolute inset-0 opacity-5">
      <div class="absolute top-10 left-10 w-32 h-32 bg-slate-800 rounded-full blur-3xl"></div>
      <div class="absolute bottom-20 right-20 w-40 h-40 bg-slate-600 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/3 w-24 h-24 bg-emerald-800 rounded-full blur-2xl"></div>
    </div>

    <div class="max-w-md w-full relative z-10">
      <div class="bg-white border-2 border-slate-200 shadow-lg p-8 relative">
        <!-- 水墨边框装饰 -->
        <div class="absolute -top-1 -left-1 w-4 h-4 border-t-2 border-l-2 border-slate-800"></div>
        <div class="absolute -top-1 -right-1 w-4 h-4 border-t-2 border-r-2 border-slate-800"></div>
        <div class="absolute -bottom-1 -left-1 w-4 h-4 border-b-2 border-l-2 border-slate-800"></div>
        <div class="absolute -bottom-1 -right-1 w-4 h-4 border-b-2 border-r-2 border-slate-800"></div>

        <!-- 标题 -->
        <div class="text-center mb-8">
          <div class="w-20 h-20 bg-slate-800 border-4 border-slate-600 flex items-center justify-center mx-auto mb-4 relative">
            <span class="text-3xl">🐟</span>
            <!-- 印章效果 -->
            <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-emerald-600"></div>
          </div>
          <h1 class="text-3xl font-bold text-slate-800 mb-2 relative">
            摸鱼聊天室
            <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-16 h-0.5 bg-slate-800"></div>
          </h1>
          <p class="text-slate-600 mt-4">请登录您的账户</p>
        </div>

        <!-- 用户类型选择 -->
        <div class="mb-6">
          <label class="block text-sm font-bold text-slate-800 mb-3 border-b border-slate-300 pb-1">用户类型</label>
          <div class="grid grid-cols-2 gap-4">
            <button
              v-for="option in userTypeOptions"
              :key="option.value"
              @click="formData.userType = option.value as 'teacher' | 'student'"
              class="p-4 border-2 transition-all duration-300 group relative"
              :class="formData.userType === option.value
                ? 'border-slate-800 bg-slate-100 shadow-inner'
                : 'border-slate-300 hover:border-slate-600 hover:bg-slate-50'"
            >
              <!-- 选中状态的水墨点缀 -->
              <div
                v-if="formData.userType === option.value"
                class="absolute top-1 right-1 w-2 h-2 bg-emerald-600"
              ></div>

              <div class="flex flex-col items-center space-y-2">
                <div
                  class="w-12 h-12 border-2 border-slate-600 flex items-center justify-center text-slate-800 text-lg transition-all group-hover:border-slate-800"
                  :class="formData.userType === option.value ? 'bg-slate-200' : 'bg-white'"
                >
                  {{ option.icon }}
                </div>
                <span class="font-medium text-slate-800">{{ option.label }}</span>
              </div>
            </button>
          </div>
          <p v-if="errors.userType" class="text-red-600 text-sm mt-2 border-l-2 border-red-600 pl-2">{{ errors.userType }}</p>
        </div>

        <!-- 用户名输入 -->
        <div class="mb-6">
          <label class="block text-sm font-bold text-slate-800 mb-2 border-b border-slate-300 pb-1">用户名</label>
          <div class="relative">
            <input
              v-model="formData.username"
              type="text"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 border-2 focus:outline-none transition-all text-lg bg-white"
              :class="errors.username ? 'border-red-400 bg-red-50' : 'border-slate-300 focus:border-slate-800'"
              @blur="handleFieldBlur('username')"
              @input="handleFieldInput('username')"
            >
            <!-- 水墨装饰线 -->
            <div class="absolute bottom-0 left-0 h-0.5 bg-slate-800 transition-all duration-300"
                 :class="formData.username ? 'w-full' : 'w-0'"></div>
          </div>
          <p v-if="errors.username && hasInteracted.username" class="text-red-600 text-sm mt-2 border-l-2 border-red-600 pl-2">
            {{ errors.username }}
          </p>
        </div>

        <!-- 密码输入 -->
        <div class="mb-6">
          <label class="block text-sm font-bold text-slate-800 mb-2 border-b border-slate-300 pb-1">密码</label>
          <div class="relative">
            <input
              v-model="formData.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              class="w-full px-4 py-3 pr-12 border-2 focus:outline-none transition-all text-lg bg-white"
              :class="errors.password ? 'border-red-400 bg-red-50' : 'border-slate-300 focus:border-slate-800'"
              @blur="handleFieldBlur('password')"
              @input="handleFieldInput('password')"
            >
            <!-- 水墨装饰线 -->
            <div class="absolute bottom-0 left-0 h-0.5 bg-slate-800 transition-all duration-300"
                 :class="formData.password ? 'w-full' : 'w-0'"></div>

            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 hover:text-slate-800 transition-colors p-1 border border-slate-300 hover:border-slate-600"
            >
              <svg v-if="showPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
              </svg>
            </button>
          </div>
          <p v-if="errors.password && hasInteracted.password" class="text-red-600 text-sm mt-2 border-l-2 border-red-600 pl-2">
            {{ errors.password }}
          </p>
        </div>

        <!-- 记住密码和忘记密码 -->
        <div class="flex items-center justify-between mb-6 py-2 border-t border-slate-200">
          <label class="flex items-center space-x-2 cursor-pointer group">
            <input
              v-model="formData.rememberMe"
              type="checkbox"
              class="w-4 h-4 text-slate-800 border-2 border-slate-400 focus:ring-slate-500 focus:ring-2"
            >
            <span class="text-sm text-slate-700 group-hover:text-slate-900">记住我</span>
          </label>
          <button
            type="button"
            class="text-sm text-slate-600 hover:text-slate-900 transition-colors border-b border-transparent hover:border-slate-600"
            @click="() => {}"
          >
            忘记密码？
          </button>
        </div>

        <!-- 登录按钮 -->
        <button
          @click="handleLogin"
          :disabled="!isFormValid || isLoading"
          class="w-full py-4 px-4 bg-slate-800 text-white border-2 border-slate-800 hover:bg-slate-900 hover:border-slate-900 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-bold text-lg relative overflow-hidden group"
        >
          <!-- 水墨扩散效果 -->
          <div class="absolute inset-0 bg-emerald-600 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"></div>

          <span v-if="isLoading" class="flex items-center justify-center relative z-10">
            <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            登录中...
          </span>
          <span v-else class="relative z-10">登录</span>
        </button>

        <!-- 错误提示 -->
        <div v-if="loginError || connectionError" class="mt-4 p-4 bg-red-50 border-l-4 border-red-600 relative">
          <div class="absolute top-2 right-2 w-2 h-2 bg-red-600"></div>
          <p class="text-red-700 text-sm font-medium">{{ loginError || connectionError }}</p>
        </div>

        <!-- 其他选项 -->
        <div class="mt-6 text-center border-t border-slate-200 pt-4 space-y-3">
          <button
            @click="switchToQuickLogin"
            class="block w-full text-sm text-slate-600 hover:text-slate-900 transition-colors group"
          >
            或者 <span class="text-emerald-700 hover:text-emerald-900 font-bold border-b border-transparent group-hover:border-emerald-700">快速进入聊天室</span>
          </button>

          <button
            @click="switchToRegister"
            class="block w-full text-sm text-slate-600 hover:text-slate-900 transition-colors group"
          >
            还没有账户？<span class="text-blue-700 hover:text-blue-900 font-bold border-b border-transparent group-hover:border-blue-700">立即注册</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
