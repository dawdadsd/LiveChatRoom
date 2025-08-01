// 表单验证工具类

export interface ValidationRule {
  required?: boolean
  minLength?: number
  maxLength?: number
  pattern?: RegExp
  custom?: (value: string) => boolean
  message: string
}

export interface ValidationResult {
  isValid: boolean
  message?: string
}

export interface FormValidationResult {
  isValid: boolean
  errors: Record<string, string>
}

/**
 * 验证单个字段
 */
export function validateField(value: string, rules: ValidationRule[]): ValidationResult {
  for (const rule of rules) {
    // 必填验证
    if (rule.required && (!value || value.trim() === '')) {
      return { isValid: false, message: rule.message }
    }

    // 如果值为空且不是必填，跳过其他验证
    if (!value || value.trim() === '') {
      continue
    }

    // 最小长度验证
    if (rule.minLength && value.length < rule.minLength) {
      return { isValid: false, message: rule.message }
    }

    // 最大长度验证
    if (rule.maxLength && value.length > rule.maxLength) {
      return { isValid: false, message: rule.message }
    }

    // 正则表达式验证
    if (rule.pattern && !rule.pattern.test(value)) {
      return { isValid: false, message: rule.message }
    }

    // 自定义验证
    if (rule.custom && !rule.custom(value)) {
      return { isValid: false, message: rule.message }
    }
  }

  return { isValid: true }
}

/**
 * 预定义的验证规则
 */
export const ValidationRules = {
  // 用户名验证规则
  username: [
    { required: true, message: '请输入用户名' },
    { minLength: 3, message: '用户名至少3个字符' },
    { maxLength: 20, message: '用户名不能超过20个字符' },
    { 
      pattern: /^[a-zA-Z0-9_]+$/, 
      message: '用户名只能包含字母、数字和下划线' 
    }
  ] as ValidationRule[],

  // 邮箱验证规则
  email: [
    { required: true, message: '请输入邮箱地址' },
    { 
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, 
      message: '请输入有效的邮箱地址' 
    }
  ] as ValidationRule[],

  // 密码验证规则
  password: [
    { required: true, message: '请输入密码' },
    { minLength: 6, message: '密码至少6个字符' },
    { maxLength: 50, message: '密码不能超过50个字符' }
  ] as ValidationRule[],

  // 强密码验证规则
  strongPassword: [
    { required: true, message: '请输入密码' },
    { minLength: 8, message: '密码至少8个字符' },
    { maxLength: 50, message: '密码不能超过50个字符' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      message: '密码必须包含大小写字母和数字'
    }
  ] as ValidationRule[],

  // 姓名验证规则
  name: [
    { required: true, message: '请输入姓名' },
    { minLength: 2, message: '姓名至少2个字符' },
    { maxLength: 10, message: '姓名不能超过10个字符' },
    {
      pattern: /^[\u4e00-\u9fa5a-zA-Z\s]+$/,
      message: '姓名只能包含中文、英文和空格'
    }
  ] as ValidationRule[]
}

/**
 * 验证登录表单
 */
export function validateLoginForm(formData: {
  userType: string
  username: string
  password: string
}): FormValidationResult {
  const errors: Record<string, string> = {}

  // 验证用户类型
  if (!formData.userType) {
    errors.userType = '请选择用户类型'
  }

  // 验证用户名
  const usernameResult = validateField(formData.username, ValidationRules.username)
  if (!usernameResult.isValid) {
    errors.username = usernameResult.message!
  }

  // 验证密码
  const passwordResult = validateField(formData.password, ValidationRules.password)
  if (!passwordResult.isValid) {
    errors.password = passwordResult.message!
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  }
}

/**
 * 验证邮箱格式
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证用户名格式
 */
export function isValidUsername(username: string): boolean {
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  return usernameRegex.test(username)
}

/**
 * 验证密码强度
 */
export function getPasswordStrength(password: string): {
  score: number // 0-4
  level: 'weak' | 'fair' | 'good' | 'strong'
  message: string
} {
  let score = 0
  
  if (password.length >= 8) score++
  if (/[a-z]/.test(password)) score++
  if (/[A-Z]/.test(password)) score++
  if (/\d/.test(password)) score++
  if (/[^a-zA-Z0-9]/.test(password)) score++

  const levels = [
    { level: 'weak' as const, message: '密码强度：弱' },
    { level: 'weak' as const, message: '密码强度：弱' },
    { level: 'fair' as const, message: '密码强度：一般' },
    { level: 'good' as const, message: '密码强度：良好' },
    { level: 'strong' as const, message: '密码强度：强' }
  ]

  return {
    score,
    ...levels[score]
  }
}

/**
 * 防抖函数 - 用于实时验证
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null
  
  return (...args: Parameters<T>) => {
    if (timeout) {
      clearTimeout(timeout)
    }
    
    timeout = setTimeout(() => {
      func(...args)
    }, wait)
  }
}
