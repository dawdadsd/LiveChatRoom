// 导入HTTP客户端
import { httpClient } from './httpClient'

// 认证服务 - 与后端API对接
export interface LoginCredentials {
  userType: 'TEACHER' | 'STUDENT'
  username: string
  password: string
  rememberMe?: boolean
}

export interface AuthUser {
  id: number
  username: string
  email: string
  userType: 'TEACHER' | 'STUDENT'
  name: string
  avatar: string
  department?: string // 教师的院系或学生的专业
  isAnonymous: false // 认证用户不是匿名的
}

export interface LoginResponse {
  success: boolean
  user?: AuthUser
  message?: string
  token?: string
}

// 后端API响应格式
export interface ApiResponse<T> {
  success: boolean
  code: number
  message: string
  data: T | null
  timestamp: string
}

// 后端登录响应数据
export interface BackendLoginData {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  realName: string
  email: string
  role: 'TEACHER' | 'STUDENT'
  avatarUrl: string
  department?: string
  additionalInfo?: string
}
class AuthService {
  private currentUser: AuthUser | null = null
  private authToken: string | null = null
  private refreshToken: string | null = null
  /**
   * 用户登录 - 调用后端API
   */
  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    try {
      const response = await httpClient.post<BackendLoginData>('/auth/login', {
        username: credentials.username,
        password: credentials.password,
        userType: credentials.userType,
        rememberMe: credentials.rememberMe || false
      })

      if (response.success && response.data) {
        const loginData = response.data

        // 转换后端数据为前端格式
        const user: AuthUser = {
          id: loginData.userId,
          username: loginData.username,
          email: loginData.email,
          userType: loginData.role,
          name: loginData.realName,
          avatar: loginData.avatarUrl || this.getDefaultAvatar(loginData.role),
          department: loginData.department || '',
          isAnonymous: false
        }

        // 保存认证信息
        this.currentUser = user
        this.authToken = loginData.accessToken
        this.refreshToken = loginData.refreshToken

        // 保存到localStorage
        localStorage.setItem('authToken', loginData.accessToken)
        localStorage.setItem('refreshToken', loginData.refreshToken)
        localStorage.setItem('currentUser', JSON.stringify(user))

        // 如果选择记住密码，保存用户名和类型
        if (credentials.rememberMe) {
          localStorage.setItem('rememberedUser', JSON.stringify({
            userType: credentials.userType,
            username: credentials.username
          }))
        }

        return {
          success: true,
          user,
          token: loginData.accessToken,
          message: '登录成功'
        }
      } else {
        return {
          success: false,
          message: response.message || '登录失败'
        }
      }
    } catch (error) {
      console.error('登录请求失败:', error)
      return {
        success: false,
        message: error instanceof Error ? error.message : '网络错误，请稍后重试'
      }
    }
  }

  /**
   * 用户注册
   */
  async register(userData: {
    username: string
    password: string
    confirmPassword: string
    realName: string
    email: string
    phone?: string
    userType: 'TEACHER' | 'STUDENT'
    // 学生特有字段
    studentId?: string
    major?: string
    grade?: number
    className?: string
    college?: string
    enrollmentYear?: number
    // 教师特有字段
    employeeId?: string
    department?: string
    title?: string
    officeLocation?: string
    researchArea?: string
  }): Promise<{ success: boolean; message: string }> {
    try {
      const endpoint = userData.userType === 'TEACHER' ? '/auth/register/teacher' : '/auth/register/student'

      const response = await httpClient.post<string>(endpoint, userData)

      return {
        success: response.success,
        message: response.message
      }
    } catch (error) {
      console.error('注册请求失败:', error)
      return {
        success: false,
        message: error instanceof Error ? error.message : '注册失败，请稍后重试'
      }
    }
  }

  /**
   * 登出 - 调用后端API
   */
  async logout(): Promise<void> {
    try {
      if (this.authToken) {
        await httpClient.post<string>('/auth/logout')
      }
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地状态
      this.currentUser = null
      this.authToken = null
      this.refreshToken = null
      localStorage.removeItem('authToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('currentUser')
    }
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
      this.refreshToken = localStorage.getItem('refreshToken')
      return this.currentUser
    }

    return null
  }

  /**
   * 检查是否已登录
   */
  isAuthenticated(): boolean {
    const user = this.getCurrentUser()
    const token = localStorage.getItem('authToken')
    return user !== null && token !== null
  }

  /**
   * 刷新令牌
   */
  async refreshAccessToken(): Promise<boolean> {
    try {
      // 优先使用实例变量，如果没有则从localStorage获取
      const refreshToken = this.refreshToken || localStorage.getItem('refreshToken')
      if (!refreshToken) {
        return false
      }

      const response = await httpClient.request<BackendLoginData>('/auth/refresh', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${refreshToken}`
        }
      })

      if (response.success && response.data) {
        const loginData = response.data

        // 更新令牌
        this.authToken = loginData.accessToken
        this.refreshToken = loginData.refreshToken
        localStorage.setItem('authToken', loginData.accessToken)
        localStorage.setItem('refreshToken', loginData.refreshToken)

        return true
      }

      return false
    } catch (error) {
      console.error('刷新令牌失败:', error)
      return false
    }
  }

  /**
   * 获取用户信息 - 调用后端API
   */
  async getUserProfile(): Promise<AuthUser | null> {
    try {
      const response = await httpClient.get<any>('/user/profile')

      if (response.success && response.data) {
        const userData = response.data

        const user: AuthUser = {
          id: userData.id,
          username: userData.username,
          email: userData.email,
          userType: userData.role,
          name: userData.realName,
          avatar: userData.avatarUrl || this.getDefaultAvatar(userData.role),
          department: userData.department || userData.major || '',
          isAnonymous: false
        }

        // 更新本地用户信息
        this.currentUser = user
        localStorage.setItem('currentUser', JSON.stringify(user))

        return user
      }

      return null
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  /**
   * 获取记住的用户信息
   */
  getRememberedUser(): { userType: 'TEACHER' | 'STUDENT'; username: string } | null {
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
   * 获取默认头像
   */
  private getDefaultAvatar(userType: 'TEACHER' | 'STUDENT'): string {
    return userType === 'TEACHER'
      ? 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face'
      : 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face'
  }
  /**
   * 获取用户类型的显示名称
   */
  getUserTypeDisplayName(userType: 'TEACHER' | 'STUDENT'): string {
    return userType === 'TEACHER' ? '教师' : '学生'
  }

  /**
   * 检查令牌是否即将过期并自动刷新
   */
  async checkAndRefreshToken(): Promise<boolean> {
    const token = localStorage.getItem('authToken')
    if (!token) {
      return false
    }

    try {
      // 解析JWT令牌检查过期时间
      const payload = JSON.parse(atob(token.split('.')[1]))
      const currentTime = Date.now() / 1000
      const expirationTime = payload.exp

      // 如果令牌在5分钟内过期，尝试刷新
      if (expirationTime - currentTime < 300) {
        return await this.refreshAccessToken()
      }

      return true
    } catch (error) {
      console.error('检查令牌失败:', error)
      return false
    }
  }
}

// 导出单例实例
export const authService = new AuthService()
