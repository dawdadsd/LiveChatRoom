<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { authService } from '../services/authService'

const emit = defineEmits<{
  registerSuccess: []
  switchToLogin: []
}>()

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  userType: 'STUDENT' as 'TEACHER' | 'STUDENT',
  // 学生特有字段
  studentId: '',
  major: '',
  grade: undefined as number | undefined,
  className: '',
  college: '',
  enrollmentYear: undefined as number | undefined,
  // 教师特有字段
  employeeId: '',
  department: '',
  title: '',
  officeLocation: '',
  researchArea: ''
})

// 表单状态
const isLoading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const errors = reactive<Record<string, string>>({})
const registerError = ref<string | null>(null)
const registerSuccess = ref(false)

// 用户类型选项
const userTypeOptions = [
  { value: 'STUDENT', label: '学生', icon: '🎓', color: 'from-blue-500 to-indigo-500' },
  { value: 'TEACHER', label: '教师', icon: '👨‍🏫', color: 'from-purple-500 to-pink-500' }
]

// 计算属性
const isFormValid = computed(() => {
  return formData.username.trim() && 
         formData.password.trim() && 
         formData.confirmPassword.trim() &&
         formData.realName.trim() &&
         formData.email.trim() &&
         formData.password === formData.confirmPassword &&
         Object.keys(errors).length === 0
})

// 表单验证
const validateForm = () => {
  const newErrors: Record<string, string> = {}
  
  if (!formData.username.trim()) {
    newErrors.username = '用户名不能为空'
  } else if (!/^[a-zA-Z0-9_]{4,20}$/.test(formData.username)) {
    newErrors.username = '用户名只能包含字母、数字、下划线，长度4-20位'
  }
  
  if (!formData.password.trim()) {
    newErrors.password = '密码不能为空'
  } else if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,20}$/.test(formData.password)) {
    newErrors.password = '密码必须包含大小写字母和数字，长度8-20位'
  }
  
  if (formData.password !== formData.confirmPassword) {
    newErrors.confirmPassword = '两次输入的密码不一致'
  }
  
  if (!formData.realName.trim()) {
    newErrors.realName = '真实姓名不能为空'
  }
  
  if (!formData.email.trim()) {
    newErrors.email = '邮箱不能为空'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    newErrors.email = '邮箱格式不正确'
  }
  
  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    newErrors.phone = '手机号格式不正确'
  }
  
  Object.assign(errors, newErrors)
  return Object.keys(newErrors).length === 0
}

// 注册处理
const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  isLoading.value = true
  registerError.value = null

  try {
    const response = await authService.register(formData)
    
    if (response.success) {
      registerSuccess.value = true
      setTimeout(() => {
        emit('registerSuccess')
      }, 2000)
    } else {
      registerError.value = response.message || '注册失败，请重试'
    }
  } catch (error) {
    console.error('注册错误:', error)
    registerError.value = '网络错误，请检查连接后重试'
  } finally {
    isLoading.value = false
  }
}

// 切换到登录
const switchToLogin = () => {
  emit('switchToLogin')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 bg-stone-50">
    <div class="max-w-2xl w-full">
      <!-- 注册成功提示 -->
      <div v-if="registerSuccess" class="text-center">
        <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          <strong>注册成功！</strong> 即将跳转到登录页面...
        </div>
      </div>

      <!-- 注册表单 -->
      <div v-else class="bg-white rounded-lg shadow-lg p-8">
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-slate-800 mb-2">用户注册</h2>
          <p class="text-slate-600">创建您的账户</p>
        </div>

        <!-- 错误提示 -->
        <div v-if="registerError" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
          {{ registerError }}
        </div>

        <form @submit.prevent="handleRegister" class="space-y-6">
          <!-- 用户类型选择 -->
          <div>
            <label class="block text-sm font-bold text-slate-800 mb-3">用户类型</label>
            <div class="grid grid-cols-2 gap-4">
              <button
                v-for="option in userTypeOptions"
                :key="option.value"
                type="button"
                @click="formData.userType = option.value"
                class="p-4 border-2 rounded-lg transition-all hover:shadow-md group"
                :class="formData.userType === option.value ? 'border-slate-800 bg-slate-50' : 'border-slate-300 hover:border-slate-400'"
              >
                <div class="flex flex-col items-center space-y-2">
                  <div class="text-2xl">{{ option.icon }}</div>
                  <span class="font-medium text-slate-800">{{ option.label }}</span>
                </div>
              </button>
            </div>
          </div>

          <!-- 基本信息 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">用户名</label>
              <input
                v-model="formData.username"
                type="text"
                placeholder="请输入用户名"
                class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all"
                :class="errors.username ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
              >
              <p v-if="errors.username" class="text-red-600 text-sm mt-1">{{ errors.username }}</p>
            </div>

            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">真实姓名</label>
              <input
                v-model="formData.realName"
                type="text"
                placeholder="请输入真实姓名"
                class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all"
                :class="errors.realName ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
              >
              <p v-if="errors.realName" class="text-red-600 text-sm mt-1">{{ errors.realName }}</p>
            </div>
          </div>

          <!-- 密码 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">密码</label>
              <div class="relative">
                <input
                  v-model="formData.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请输入密码"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all pr-12"
                  :class="errors.password ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
                >
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 hover:text-slate-700"
                >
                  {{ showPassword ? '🙈' : '👁️' }}
                </button>
              </div>
              <p v-if="errors.password" class="text-red-600 text-sm mt-1">{{ errors.password }}</p>
            </div>

            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">确认密码</label>
              <div class="relative">
                <input
                  v-model="formData.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="请再次输入密码"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all pr-12"
                  :class="errors.confirmPassword ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
                >
                <button
                  type="button"
                  @click="showConfirmPassword = !showConfirmPassword"
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 hover:text-slate-700"
                >
                  {{ showConfirmPassword ? '🙈' : '👁️' }}
                </button>
              </div>
              <p v-if="errors.confirmPassword" class="text-red-600 text-sm mt-1">{{ errors.confirmPassword }}</p>
            </div>
          </div>

          <!-- 联系信息 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">邮箱</label>
              <input
                v-model="formData.email"
                type="email"
                placeholder="请输入邮箱"
                class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all"
                :class="errors.email ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
              >
              <p v-if="errors.email" class="text-red-600 text-sm mt-1">{{ errors.email }}</p>
            </div>

            <div>
              <label class="block text-sm font-bold text-slate-800 mb-2">手机号（可选）</label>
              <input
                v-model="formData.phone"
                type="tel"
                placeholder="请输入手机号"
                class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all"
                :class="errors.phone ? 'border-red-400' : 'border-slate-300 focus:border-slate-800'"
              >
              <p v-if="errors.phone" class="text-red-600 text-sm mt-1">{{ errors.phone }}</p>
            </div>
          </div>

          <!-- 角色特定字段 -->
          <div v-if="formData.userType === 'STUDENT'" class="space-y-4">
            <h3 class="text-lg font-semibold text-slate-800 border-b border-slate-300 pb-2">学生信息</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">学号</label>
                <input
                  v-model="formData.studentId"
                  type="text"
                  placeholder="请输入学号"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">专业</label>
                <input
                  v-model="formData.major"
                  type="text"
                  placeholder="请输入专业"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">年级</label>
                <input
                  v-model.number="formData.grade"
                  type="number"
                  placeholder="请输入年级"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">班级</label>
                <input
                  v-model="formData.className"
                  type="text"
                  placeholder="请输入班级"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
            </div>
          </div>

          <div v-if="formData.userType === 'TEACHER'" class="space-y-4">
            <h3 class="text-lg font-semibold text-slate-800 border-b border-slate-300 pb-2">教师信息</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">工号</label>
                <input
                  v-model="formData.employeeId"
                  type="text"
                  placeholder="请输入工号"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">院系</label>
                <input
                  v-model="formData.department"
                  type="text"
                  placeholder="请输入院系"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">职称</label>
                <input
                  v-model="formData.title"
                  type="text"
                  placeholder="请输入职称"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
              <div>
                <label class="block text-sm font-bold text-slate-800 mb-2">办公室位置</label>
                <input
                  v-model="formData.officeLocation"
                  type="text"
                  placeholder="请输入办公室位置"
                  class="w-full px-4 py-3 border-2 rounded-lg focus:outline-none transition-all border-slate-300 focus:border-slate-800"
                >
              </div>
            </div>
          </div>

          <!-- 提交按钮 -->
          <div class="flex space-x-4">
            <button
              type="submit"
              :disabled="!isFormValid || isLoading"
              class="flex-1 bg-slate-800 text-white py-3 px-6 rounded-lg font-semibold transition-all hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="isLoading">注册中...</span>
              <span v-else>注册</span>
            </button>
            
            <button
              type="button"
              @click="switchToLogin"
              class="flex-1 bg-gray-200 text-slate-800 py-3 px-6 rounded-lg font-semibold transition-all hover:bg-gray-300"
            >
              返回登录
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
