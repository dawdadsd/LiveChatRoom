<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { authService, type AuthUser } from '../services/authService'
import { httpClient } from '../services/httpClient'

const emit = defineEmits<{
  close: []
}>()

// 组件状态
const isLoading = ref(false)
const isEditing = ref(false)
const updateSuccess = ref(false)
const updateError = ref<string | null>(null)

// 用户信息
const userInfo = reactive<AuthUser>({
  id: 0,
  username: '',
  email: '',
  userType: 'STUDENT',
  name: '',
  avatar: '',
  department: '',
  isAnonymous: false
})

// 编辑表单数据
const editForm = reactive({
  realName: '',
  email: '',
  phone: '',
  department: '',
  // 其他可编辑字段...
})

// 加载用户信息
const loadUserProfile = async () => {
  isLoading.value = true
  try {
    const user = await authService.getUserProfile()
    if (user) {
      Object.assign(userInfo, user)
      // 初始化编辑表单
      editForm.realName = user.name
      editForm.email = user.email
      editForm.department = user.department || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
  updateError.value = null
  updateSuccess.value = false
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 重置表单数据
  editForm.realName = userInfo.name
  editForm.email = userInfo.email
  editForm.department = userInfo.department || ''
}

// 保存更改
const saveChanges = async () => {
  isLoading.value = true
  updateError.value = null
  
  try {
    const response = await httpClient.put('/user/profile', {
      realName: editForm.realName,
      email: editForm.email,
      department: editForm.department
    })
    
    if (response.success) {
      updateSuccess.value = true
      isEditing.value = false
      // 重新加载用户信息
      await loadUserProfile()
      
      setTimeout(() => {
        updateSuccess.value = false
      }, 3000)
    } else {
      updateError.value = response.message || '更新失败'
    }
  } catch (error) {
    updateError.value = '网络错误，请稍后重试'
  } finally {
    isLoading.value = false
  }
}

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserProfile()
})
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- 头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-2xl font-bold text-gray-800">个人信息</h2>
        <button
          @click="emit('close')"
          class="text-gray-500 hover:text-gray-700 text-2xl"
        >
          ×
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="isLoading" class="p-8 text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        <p class="mt-4 text-gray-600">加载中...</p>
      </div>

      <!-- 用户信息内容 -->
      <div v-else class="p-6">
        <!-- 成功提示 -->
        <div v-if="updateSuccess" class="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded">
          信息更新成功！
        </div>

        <!-- 错误提示 -->
        <div v-if="updateError" class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
          {{ updateError }}
        </div>

        <!-- 用户头像和基本信息 -->
        <div class="flex items-center space-x-6 mb-8">
          <div class="relative">
            <img
              :src="userInfo.avatar"
              :alt="userInfo.name"
              class="w-24 h-24 rounded-full object-cover border-4 border-gray-200"
            >
            <div class="absolute bottom-0 right-0 w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center">
              <span class="text-white text-xs">
                {{ userInfo.userType === 'TEACHER' ? '👨‍🏫' : '🎓' }}
              </span>
            </div>
          </div>
          
          <div>
            <h3 class="text-xl font-semibold text-gray-800">{{ userInfo.name }}</h3>
            <p class="text-gray-600">@{{ userInfo.username }}</p>
            <p class="text-sm text-blue-600 font-medium">
              {{ userInfo.userType === 'TEACHER' ? '教师' : '学生' }}
            </p>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="space-y-6">
          <!-- 基本信息 -->
          <div>
            <h4 class="text-lg font-semibold text-gray-800 mb-4 border-b border-gray-200 pb-2">基本信息</h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">真实姓名</label>
                <input
                  v-if="isEditing"
                  v-model="editForm.realName"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                <p v-else class="text-gray-900">{{ userInfo.name }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                <input
                  v-if="isEditing"
                  v-model="editForm.email"
                  type="email"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                <p v-else class="text-gray-900">{{ userInfo.email }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  {{ userInfo.userType === 'TEACHER' ? '院系' : '专业' }}
                </label>
                <input
                  v-if="isEditing"
                  v-model="editForm.department"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                <p v-else class="text-gray-900">{{ userInfo.department || '未设置' }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">用户类型</label>
                <p class="text-gray-900">{{ userInfo.userType === 'TEACHER' ? '教师' : '学生' }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex justify-end space-x-4 mt-8 pt-6 border-t border-gray-200">
          <template v-if="isEditing">
            <button
              @click="cancelEdit"
              class="px-4 py-2 text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition-colors"
            >
              取消
            </button>
            <button
              @click="saveChanges"
              :disabled="isLoading"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
              {{ isLoading ? '保存中...' : '保存' }}
            </button>
          </template>
          <template v-else>
            <button
              @click="startEdit"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              编辑信息
            </button>
            <button
              @click="emit('close')"
              class="px-4 py-2 text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition-colors"
            >
              关闭
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
