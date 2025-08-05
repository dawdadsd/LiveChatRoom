<script setup lang="ts">
import { ref } from 'vue'

const showDemo = ref(false)

// 示例用户数据（仅用于演示）
const demoUsers = {
  teachers: [
    {
      id: 1,
      name: '张伟教授',
      username: 'teacher001',
      department: '计算机科学与技术学院',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
      email: 'zhang.wei@university.edu'
    },
    {
      id: 2,
      name: '李明副教授',
      username: 'teacher002',
      department: '软件工程学院',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
      email: 'li.ming@university.edu'
    }
  ],
  students: [
    {
      id: 1,
      name: '王小明',
      username: 'student001',
      department: '计算机科学与技术',
      avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face',
      email: 'wang.xiaoming@student.edu'
    },
    {
      id: 2,
      name: '刘小红',
      username: 'student002',
      department: '软件工程',
      avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
      email: 'liu.xiaohong@student.edu'
    }
  ]
}

const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    // 可以添加复制成功的提示
  })
}
</script>

<template>
  <!-- 演示用户信息按钮 -->
  <div class="fixed bottom-4 right-4 z-50">
    <button
      @click="showDemo = !showDemo"
      class="bg-slate-800 text-white p-3 border-2 border-slate-600 hover:bg-slate-900 hover:border-slate-800 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 relative"
      title="查看使用指南"
    >
      <!-- 印章装饰 -->
      <div class="absolute -top-1 -right-1 w-2 h-2 bg-emerald-600"></div>
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    </button>
  </div>

  <!-- 演示用户信息面板 -->
  <div
    v-if="showDemo"
    class="fixed inset-0 bg-black/60 z-40 flex items-center justify-center p-4"
    @click="showDemo = false"
  >
    <div
      class="bg-white border-4 border-slate-300 shadow-2xl max-w-4xl w-full max-h-[80vh] overflow-y-auto relative"
      @click.stop
    >
      <!-- 水墨边框装饰 -->
      <div class="absolute -top-2 -left-2 w-6 h-6 border-t-4 border-l-4 border-slate-800"></div>
      <div class="absolute -top-2 -right-2 w-6 h-6 border-t-4 border-r-4 border-slate-800"></div>
      <div class="absolute -bottom-2 -left-2 w-6 h-6 border-b-4 border-l-4 border-slate-800"></div>
      <div class="absolute -bottom-2 -right-2 w-6 h-6 border-b-4 border-r-4 border-slate-800"></div>

      <!-- 标题 -->
      <div class="sticky top-0 bg-slate-800 text-white p-6 border-b-4 border-slate-600 relative">
        <!-- 印章装饰 -->
        <div class="absolute top-2 right-2 w-4 h-4 bg-emerald-600"></div>

        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold">使用指南</h2>
            <p class="text-slate-200 mt-1 border-l-2 border-slate-600 pl-2">了解如何注册和使用系统</p>
          </div>
          <button
            @click="showDemo = false"
            class="text-slate-300 hover:text-white transition-colors p-2 border border-slate-600 hover:border-slate-400"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <div class="p-6">
        <!-- 注册提示 -->
        <div class="mb-8 p-6 bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl">
          <h3 class="text-xl font-semibold text-slate-800 mb-4 flex items-center">
            <span class="text-2xl mr-2">🚀</span>
            开始使用
          </h3>
          <div class="space-y-4">
            <p class="text-slate-700">
              欢迎使用社交网络演示系统！现在您需要先注册一个账户才能使用完整功能。
            </p>
            <div class="grid gap-4 md:grid-cols-2">
              <div class="bg-white p-4 rounded-lg border border-blue-200">
                <h4 class="font-semibold text-slate-800 mb-2 flex items-center">
                  <span class="text-lg mr-2">👨‍🏫</span>
                  教师注册
                </h4>
                <p class="text-sm text-slate-600 mb-3">
                  注册教师账户，管理学生信息，查看同事列表
                </p>
                <ul class="text-xs text-slate-500 space-y-1">
                  <li>• 需要填写工号、院系、职称等信息</li>
                  <li>• 可以查看所有学生列表</li>
                  <li>• 可以查看同院系教师</li>
                </ul>
              </div>

              <div class="bg-white p-4 rounded-lg border border-blue-200">
                <h4 class="font-semibold text-slate-800 mb-2 flex items-center">
                  <span class="text-lg mr-2">🎓</span>
                  学生注册
                </h4>
                <p class="text-sm text-slate-600 mb-3">
                  注册学生账户，查看同学信息，联系任课教师
                </p>
                <ul class="text-xs text-slate-500 space-y-1">
                  <li>• 需要填写学号、专业、年级等信息</li>
                  <li>• 可以查看同专业同学</li>
                  <li>• 可以查看本专业教师</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <!-- 示例用户信息 -->
        <div class="mb-8">
          <h3 class="text-xl font-semibold text-slate-800 mb-4 flex items-center">
            <span class="text-2xl mr-2">👨‍🏫</span>
            教师示例
          </h3>
          <div class="grid gap-4 md:grid-cols-2">
            <div
              v-for="teacher in demoUsers.teachers"
              :key="teacher.id"
              class="bg-gradient-to-r from-purple-50 to-pink-50 border border-purple-200 rounded-xl p-4"
            >
              <div class="flex items-center space-x-3 mb-3">
                <img
                  :src="teacher.avatar"
                  :alt="teacher.name"
                  class="w-12 h-12 rounded-full object-cover border-2 border-white shadow-md"
                >
                <div>
                  <h4 class="font-semibold text-slate-800">{{ teacher.name }}</h4>
                  <p class="text-sm text-slate-600">{{ teacher.department }}</p>
                </div>
              </div>
              <div class="space-y-2 text-sm">
                <div class="flex items-center justify-between">
                  <span class="text-slate-600">建议用户名:</span>
                  <button
                    @click="copyToClipboard(teacher.username)"
                    class="font-mono bg-white px-2 py-1 rounded border hover:bg-slate-50 transition-colors"
                    title="点击复制"
                  >
                    {{ teacher.username }}
                  </button>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-slate-600">邮箱:</span>
                  <span class="text-xs text-slate-500">{{ teacher.email }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 学生示例 -->
        <div>
          <h3 class="text-xl font-semibold text-slate-800 mb-4 flex items-center">
            <span class="text-2xl mr-2">🎓</span>
            学生示例
          </h3>
          <div class="grid gap-4 md:grid-cols-2">
            <div
              v-for="student in demoUsers.students"
              :key="student.id"
              class="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl p-4"
            >
              <div class="flex items-center space-x-3 mb-3">
                <img
                  :src="student.avatar"
                  :alt="student.name"
                  class="w-12 h-12 rounded-full object-cover border-2 border-white shadow-md"
                >
                <div>
                  <h4 class="font-semibold text-slate-800">{{ student.name }}</h4>
                  <p class="text-sm text-slate-600">{{ student.department }}</p>
                </div>
              </div>
              <div class="space-y-2 text-sm">
                <div class="flex items-center justify-between">
                  <span class="text-slate-600">建议用户名:</span>
                  <button
                    @click="copyToClipboard(student.username)"
                    class="font-mono bg-white px-2 py-1 rounded border hover:bg-slate-50 transition-colors"
                    title="点击复制"
                  >
                    {{ student.username }}
                  </button>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-slate-600">邮箱:</span>
                  <span class="text-xs text-slate-500">{{ student.email }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 使用说明 -->
        <div class="mt-8 p-4 bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 rounded-xl">
          <h4 class="font-semibold text-amber-800 mb-2 flex items-center">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            使用说明
          </h4>
          <ul class="text-sm text-amber-700 space-y-1">
            <li>• 点击"立即注册"创建新账户</li>
            <li>• 选择对应的用户类型（教师/学生）</li>
            <li>• 填写完整的注册信息</li>
            <li>• 注册成功后使用账户信息登录</li>
            <li>• 登录成功后可以使用完整的社交功能</li>
            <li>• 也可以选择"快速进入聊天室"进行匿名聊天</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
