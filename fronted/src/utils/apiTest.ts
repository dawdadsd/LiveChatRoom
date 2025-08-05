// API测试工具
import { httpClient } from '../services/httpClient'

export class ApiTester {
  /**
   * 测试后端连接
   */
  static async testConnection(): Promise<{ success: boolean; message: string }> {
    try {
      // 尝试访问一个公开的端点
      const response = await fetch('http://localhost:8080/api/public/health', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      if (response.ok) {
        return {
          success: true,
          message: '后端连接正常'
        }
      } else {
        return {
          success: false,
          message: `后端响应错误: ${response.status}`
        }
      }
    } catch (error) {
      return {
        success: false,
        message: `连接失败: ${error instanceof Error ? error.message : '未知错误'}`
      }
    }
  }

  /**
   * 测试登录API
   */
  static async testLogin(): Promise<{ success: boolean; message: string }> {
    try {
      const testCredentials = {
        username: 'test_user',
        password: 'test_password',
        userType: 'STUDENT',
        rememberMe: false
      }

      const response = await httpClient.post('/auth/login', testCredentials)
      
      return {
        success: response.success,
        message: response.message || '登录API测试完成'
      }
    } catch (error) {
      return {
        success: false,
        message: `登录API测试失败: ${error instanceof Error ? error.message : '未知错误'}`
      }
    }
  }

  /**
   * 测试所有API端点
   */
  static async testAllEndpoints(): Promise<Array<{ endpoint: string; success: boolean; message: string }>> {
    const endpoints = [
      { name: '后端连接', test: () => this.testConnection() },
      { name: '登录API', test: () => this.testLogin() }
    ]

    const results = []
    
    for (const endpoint of endpoints) {
      try {
        const result = await endpoint.test()
        results.push({
          endpoint: endpoint.name,
          success: result.success,
          message: result.message
        })
      } catch (error) {
        results.push({
          endpoint: endpoint.name,
          success: false,
          message: error instanceof Error ? error.message : '测试失败'
        })
      }
    }

    return results
  }
}

// 开发环境下自动测试连接
if (import.meta.env.DEV) {
  console.log('🔧 开发模式：正在测试后端连接...')
  ApiTester.testConnection().then(result => {
    if (result.success) {
      console.log('✅ 后端连接正常')
    } else {
      console.warn('⚠️ 后端连接异常:', result.message)
    }
  })
}
