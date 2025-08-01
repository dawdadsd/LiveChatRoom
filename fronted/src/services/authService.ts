// 认证服务 - 模拟登录验证
export interface LoginCredentials {
  userType: 'teacher' | 'student'
  username: string
  password: string
  rememberMe?: boolean
}

export interface AuthUser {
  id: number
  username: string
  email: string
  userType: 'teacher' | 'student'
  name: string
  avatar: string
  department?: string // 教师的院系或学生的专业
  isAnonymous: false // 认证用户不是匿名的
}

export interface LoginResponse {
  success: boolean
  user?: AuthUser
  message?: string
  token?: string // 模拟JWT token
}

// 模拟用户数据库
const mockUsers: AuthUser[] = [
  // 教师用户
  {
    id: 1001,
    username: 'teacher001',
    email: 'zhang.wei@university.edu',
    userType: 'teacher',
    name: '张伟教授',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    department: '计算机科学与技术学院',
    isAnonymous: false
  },
  {
    id: 1002,
    username: 'teacher002',
    email: 'li.ming@university.edu',
    userType: 'teacher',
    name: '李明副教授',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    department: '软件工程学院',
    isAnonymous: false
  },
  // 学生用户
  {
    id: 2001,
    username: 'student001',
    email: 'wang.xiaoming@student.edu',
    userType: 'student',
    name: '王小明',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face',
    department: '计算机科学与技术',
    isAnonymous: false
  },
  {
    id: 2002,
    username: 'student002',
    email: 'liu.xiaohong@student.edu',
    userType: 'student',
    name: '刘小红',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
    department: '软件工程',
    isAnonymous: false
  }
]

// 模拟密码数据（实际项目中不应该这样存储）
const mockPasswords: Record<string, string> = {
  'teacher001': 'teacher123',
  'teacher002': 'teacher456',
  'student001': 'student123',
  'student002': 'student456'
}

class AuthService {
  private currentUser: AuthUser | null = null
  private authToken: string | null = null

  /**
   * 模拟登录验证
   */
  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    const { userType, username, password } = credentials

    // 查找用户
    const user = mockUsers.find(u => 
      u.username === username && u.userType === userType
    )

    if (!user) {
      return {
        success: false,
        message: '用户名不存在或用户类型不匹配'
      }
    }

    // 验证密码
    if (mockPasswords[username] !== password) {
      return {
        success: false,
        message: '密码错误'
      }
    }

    // 生成模拟token
    const token = this.generateMockToken(user)
    
    // 保存登录状态
    this.currentUser = user
    this.authToken = token

    // 如果选择记住密码，保存到localStorage
    if (credentials.rememberMe) {
      localStorage.setItem('rememberedUser', JSON.stringify({
        userType,
        username
      }))
    }

    // 保存token到localStorage
    localStorage.setItem('authToken', token)
    localStorage.setItem('currentUser', JSON.stringify(user))

    return {
      success: true,
      user,
      token,
      message: '登录成功'
    }
  }

  /**
   * 登出
   */
  logout(): void {
    this.currentUser = null
    this.authToken = null
    localStorage.removeItem('authToken')
    localStorage.removeItem('currentUser')
  }

  /**
   * 获取当前用户
   */
  getCurrentUser(): AuthUser | null {
    if (this.currentUser) {
      return this.currentUser
    }

    // 尝试从localStorage恢复
    const savedUser = localStorage.getItem('currentUser')
    const savedToken = localStorage.getItem('authToken')
    
    if (savedUser && savedToken) {
      this.currentUser = JSON.parse(savedUser)
      this.authToken = savedToken
      return this.currentUser
    }

    return null
  }

  /**
   * 检查是否已登录
   */
  isAuthenticated(): boolean {
    return this.getCurrentUser() !== null
  }

  /**
   * 获取记住的用户信息
   */
  getRememberedUser(): { userType: string; username: string } | null {
    const remembered = localStorage.getItem('rememberedUser')
    return remembered ? JSON.parse(remembered) : null
  }

  /**
   * 清除记住的用户信息
   */
  clearRememberedUser(): void {
    localStorage.removeItem('rememberedUser')
  }

  /**
   * 生成模拟JWT token
   */
  private generateMockToken(user: AuthUser): string {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))
    const payload = btoa(JSON.stringify({
      sub: user.id,
      username: user.username,
      userType: user.userType,
      exp: Date.now() + 24 * 60 * 60 * 1000 // 24小时过期
    }))
    const signature = btoa(`mock-signature-${user.id}`)
    
    return `${header}.${payload}.${signature}`
  }

  /**
   * 获取所有模拟用户（用于开发测试）
   */
  getMockUsers(): AuthUser[] {
    return mockUsers
  }

  /**
   * 获取用户类型的显示名称
   */
  getUserTypeDisplayName(userType: 'teacher' | 'student'): string {
    return userType === 'teacher' ? '教师' : '学生'
  }
}

// 导出单例实例
export const authService = new AuthService()
