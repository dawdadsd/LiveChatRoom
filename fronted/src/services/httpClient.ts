// HTTP客户端 - 处理API请求和令牌刷新
import { API_CONFIG } from '../config/api'

export interface ApiResponse<T> {
  success: boolean
  code: number
  message: string
  data: T | null
  timestamp: string
}

class HttpClient {
  private baseURL: string
  private isRefreshing = false
  private failedQueue: Array<{
    resolve: (value: any) => void
    reject: (reason: any) => void
  }> = []

  constructor(baseURL: string = API_CONFIG.BASE_URL) {
    this.baseURL = baseURL
  }

  /**
   * 处理失败队列
   */
  private processQueue(error: any, token: string | null = null) {
    this.failedQueue.forEach(({ resolve, reject }) => {
      if (error) {
        reject(error)
      } else {
        resolve(token)
      }
    })
    
    this.failedQueue = []
  }

  /**
   * 发送HTTP请求
   */
  async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    const url = `${this.baseURL}${endpoint}`
    
    const defaultHeaders: Record<string, string> = {
      'Content-Type': 'application/json',
    }
    
    // 添加认证头
    const token = localStorage.getItem('authToken')
    if (token) {
      defaultHeaders['Authorization'] = `Bearer ${token}`
    }
    
    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    }

    try {
      const response = await fetch(url, config)
      
      // 如果是401错误且不是刷新令牌请求，尝试刷新令牌
      if (response.status === 401 && !endpoint.includes('/auth/refresh')) {
        return this.handleUnauthorized(endpoint, config)
      }
      
      const data = await response.json()
      return data
    } catch (error) {
      console.error('HTTP请求失败:', error)
      throw new Error('网络请求失败，请检查网络连接')
    }
  }

  /**
   * 处理401未授权错误
   */
  private async handleUnauthorized<T>(
    originalEndpoint: string,
    originalConfig: RequestInit
  ): Promise<ApiResponse<T>> {
    if (this.isRefreshing) {
      // 如果正在刷新令牌，将请求加入队列
      return new Promise((resolve, reject) => {
        this.failedQueue.push({ resolve, reject })
      }).then(() => {
        // 重新发送原始请求
        return this.request<T>(originalEndpoint, originalConfig)
      })
    }

    this.isRefreshing = true

    try {
      // 尝试刷新令牌
      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        throw new Error('没有刷新令牌')
      }

      const refreshResponse = await this.request<any>('/auth/refresh', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${refreshToken}`
        }
      })

      if (refreshResponse.success && refreshResponse.data) {
        // 更新令牌
        localStorage.setItem('authToken', refreshResponse.data.accessToken)
        localStorage.setItem('refreshToken', refreshResponse.data.refreshToken)

        this.processQueue(null, refreshResponse.data.accessToken)
        // 重新发送原始请求
        return this.request<T>(originalEndpoint, originalConfig)
      } else {
        // 刷新失败，清除认证信息
        this.processQueue(new Error('认证已过期，请重新登录'), null)
        this.clearAuthData()

        return {
          success: false,
          code: 401,
          message: '认证已过期，请重新登录',
          data: null,
          timestamp: new Date().toISOString()
        }
      }
    } catch (error) {
      this.processQueue(error, null)
      this.clearAuthData()
      throw error
    } finally {
      this.isRefreshing = false
    }
  }

  /**
   * 清除认证数据
   */
  private clearAuthData() {
    localStorage.removeItem('authToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('currentUser')
  }

  /**
   * GET请求
   */
  async get<T>(endpoint: string, params?: Record<string, any>): Promise<ApiResponse<T>> {
    let url = endpoint
    if (params) {
      const searchParams = new URLSearchParams()
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          searchParams.append(key, String(value))
        }
      })
      url += `?${searchParams.toString()}`
    }
    
    return this.request<T>(url, { method: 'GET' })
  }

  /**
   * POST请求
   */
  async post<T>(endpoint: string, data?: any): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  /**
   * PUT请求
   */
  async put<T>(endpoint: string, data?: any): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  /**
   * DELETE请求
   */
  async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: 'DELETE' })
  }
}

// 导出单例实例
export const httpClient = new HttpClient()
