/**
 * Memorin API SDK - 简化版演示
 * 可以直接在前端项目中使用的TypeScript SDK
 */

// ===== 类型定义 =====
interface APIResponse<T> {
  success: boolean
  data: T
  message: string
  timestamp: string
}

interface CategoryData {
  id: string
  name: string
  description?: string
  color: string
  icon: string
  level: number
  parentId?: string
}

interface KnowledgeData {
  id: string
  question: string
  explanation: string
  type: 'text' | 'choice' | 'code'
  categoryId: string
  tags: string[]
  difficulty: number
  estimatedTime: number
  status: 'draft' | 'published'
}

interface TextQuestionData {
  id: string
  knowledgeId: string
  textType: 'fill' | 'essay'
  answer: string
  validationMode: 'exact' | 'contains' | 'regex' | 'manual'
  alternativeAnswers?: string[]
  caseSensitive: boolean
}

// ===== 错误处理 =====
class MemorinaAPIError extends Error {
  constructor(
    message: string,
    public status: number,
    public response?: unknown
  ) {
    super(message)
    this.name = 'MemorinaAPIError'
  }
}

// ===== 核心API客户端 =====
class MemorinaAPIClient {
  private baseURL: string

  constructor(baseURL?: string) {
    // 在开发环境下使用空字符串，让Vite代理处理
    // 在生产环境下可以传入完整的API地址
    this.baseURL = baseURL || (import.meta.env.DEV ? '' : 'http://localhost:8082')
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${this.baseURL}${endpoint}`
    
    const defaultHeaders = {
      'Content-Type': 'application/json',
      ...options.headers
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers: defaultHeaders
      })

      const data: APIResponse<T> = await response.json()

      if (!response.ok || !data.success) {
        throw new MemorinaAPIError(
          data.message || `HTTP ${response.status}`,
          response.status,
          data
        )
      }

      return data.data
    } catch (error) {
      if (error instanceof MemorinaAPIError) {
        throw error
      }
      throw new MemorinaAPIError(
        `网络请求失败: ${(error as Error).message}`,
        0,
        { error: (error as Error).message }
      )
    }
  }

  async get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' })
  }

  async post<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async put<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' })
  }

  async healthCheck(): Promise<boolean> {
    try {
      // /actuator/health 返回的是 {status: "UP", components: ...}，不是标准的API响应格式
      const response = await fetch(`${this.baseURL}/actuator/health`, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      if (!response.ok) {
        return false
      }
      const data = await response.json()
      return data.status === 'UP'
    } catch (error) {
      console.error('健康检查失败:', error)
      return false
    }
  }
}

// ===== 智能分类管理器 =====
class CategoryManager {
  private client: MemorinaAPIClient
  private cache: CategoryData[] = []
  private cacheTime = 0

  constructor(client: MemorinaAPIClient) {
    this.client = client
  }

  async getAll(): Promise<CategoryData[]> {
    const now = Date.now()
    if (this.cache.length > 0 && now - this.cacheTime < 60000) {
      return this.cache
    }

    this.cache = await this.client.get<CategoryData[]>('/api/test/categories')
    this.cacheTime = now
    return this.cache
  }

  async findByName(name: string): Promise<CategoryData | null> {
    const categories = await this.getAll()
    return categories.find(cat => cat.name === name) || null
  }

  async create(input: {
    name: string
    description?: string
    icon?: string
    color?: string
    parentName?: string
  }): Promise<CategoryData> {
    // 自动生成默认值
    const categoryData = {
      name: input.name,
      description: input.description || `${input.name}相关知识点`,
      icon: input.icon || this.generateIcon(input.name),
      color: input.color || this.generateColor(input.name),
      parentId: undefined as string | undefined
    }

    // 解析父分类
    if (input.parentName) {
      const parent = await this.findByName(input.parentName)
      if (!parent) {
        throw new MemorinaAPIError(`父分类 "${input.parentName}" 不存在`, 400)
      }
      categoryData.parentId = parent.id
    }

    const result = await this.client.post<CategoryData>('/api/test/categories', categoryData)
    this.cache = [] // 清除缓存
    return result
  }

  async update(id: string, input: {
    name: string
    description?: string
    icon?: string
    color?: string
  }): Promise<CategoryData> {
    // 确保所有必填字段都有值
    const updateData = {
      name: input.name,
      description: input.description || `${input.name}相关知识点`,
      icon: input.icon || '📁',
      color: input.color || '#667eea'
    }

    const result = await this.client.put<CategoryData>(`/api/test/categories/${id}`, updateData)
    this.cache = [] // 清除缓存
    return result
  }

  async delete(id: string): Promise<void> {
    await this.client.delete(`/api/test/categories/${id}`)
    this.cache = [] // 清除缓存
  }

  private generateIcon(name: string): string {
    const iconMap: Record<string, string> = {
      '数学': '📐', '编程': '💻', '语言': '🗣️', '科学': '🔬',
      '历史': '📚', '艺术': '🎨', '物理': '⚛️', '化学': '🧪',
      '生物': '🧬', '地理': '🌍', '音乐': '🎵', '体育': '⚽'
    }
    
    for (const [key, icon] of Object.entries(iconMap)) {
      if (name.includes(key)) return icon
    }
    return '📋'
  }

  private generateColor(name: string): string {
    const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
    const hash = name.split('').reduce((a, b) => a + b.charCodeAt(0), 0)
    return colors[hash % colors.length]
  }
}

// ===== 智能知识点管理器 =====
class KnowledgeManager {
  private client: MemorinaAPIClient
  private categoryManager: CategoryManager

  constructor(client: MemorinaAPIClient, categoryManager: CategoryManager) {
    this.client = client
    this.categoryManager = categoryManager
  }

  async create(input: {
    question: string
    explanation: string
    type: 'text' | 'choice' | 'code'
    categoryName: string
    tags?: string[]
    difficulty?: number
    estimatedTime?: number
    status?: 'draft' | 'published'
  }): Promise<KnowledgeData> {
    // 自动解析分类ID
    const category = await this.categoryManager.findByName(input.categoryName)
    if (!category) {
      throw new MemorinaAPIError(`分类 "${input.categoryName}" 不存在`, 400)
    }

    const knowledgeData = {
      question: input.question,
      explanation: input.explanation,
      categoryId: category.id,
      type: input.type,
      tags: input.tags || [],
      difficulty: input.difficulty || 3,
      estimatedTime: input.estimatedTime || 15,
      status: input.status || 'published'
    }

    return this.client.post<KnowledgeData>('/api/test/knowledge', knowledgeData)
  }

  async getByType(type: 'text' | 'choice' | 'code'): Promise<KnowledgeData[]> {
    try {
      const response = await this.client.get<{data: KnowledgeData[], total: number}>('/api/test/knowledge')
      const allKnowledge = response.data || response // 处理不同的响应格式
      const filteredKnowledge = allKnowledge.filter(k => k.type === type)
      console.log(`Found ${filteredKnowledge.length} knowledge points of type "${type}"`)
      return filteredKnowledge
    } catch (error) {
      console.error(`Error getting knowledge by type "${type}":`, error)
      throw error
    }
  }

  async getAll(): Promise<KnowledgeData[]> {
    const response = await this.client.get<{data: KnowledgeData[], total: number}>('/api/test/knowledge')
    return response.data || response
  }
}

// ===== 超简化文本题管理器 =====
class TextQuestionManager {
  private client: MemorinaAPIClient
  private knowledgeManager: KnowledgeManager
  private categoryManager: CategoryManager

  constructor(client: MemorinaAPIClient, knowledgeManager: KnowledgeManager, categoryManager: CategoryManager) {
    this.client = client
    this.knowledgeManager = knowledgeManager
    this.categoryManager = categoryManager
  }

  async create(input: {
    categoryName?: string
    knowledgeQuestion?: string
    knowledgeId?: string
    answer: string
    alternatives?: string[]
    caseSensitive?: boolean
    type?: 'fill' | 'essay'
    validation?: 'exact' | 'contains' | 'fuzzy'
  }): Promise<TextQuestionData> {
    let knowledgeId: string

    console.log('Creating text question with input:', input)

    // 智能解析知识点ID
    if (input.knowledgeId) {
      knowledgeId = input.knowledgeId
      console.log('Using provided knowledge ID:', knowledgeId)
    } else if (input.categoryName && input.knowledgeQuestion) {
      // 自动创建知识点
      console.log('Creating new knowledge point...')
      const knowledge = await this.knowledgeManager.create({
        question: input.knowledgeQuestion,
        explanation: `关于"${input.answer}"的知识点`,
        type: 'text',
        categoryName: input.categoryName
      })
      knowledgeId = knowledge.id
      console.log('Created knowledge with ID:', knowledgeId)
    } else {
      // 查找已有的text类型知识点
      console.log('Looking for existing text knowledge points...')
      const textKnowledge = await this.knowledgeManager.getByType('text')
      if (textKnowledge.length === 0) {
        throw new MemorinaAPIError('没有找到可用的文本类型知识点，请提供categoryName和knowledgeQuestion', 400)
      }
      knowledgeId = textKnowledge[0].id
      console.log('Using existing knowledge ID:', knowledgeId)
    }

    // 映射简化参数到API格式
    const validationModeMap = {
      'exact': 'exact',
      'contains': 'contains',
      'fuzzy': 'contains'
    } as const

    const requestData = {
      knowledgeId,
      textType: input.type || 'fill',
      answer: input.answer,
      validationMode: validationModeMap[input.validation || 'exact'],
      alternativeAnswers: input.alternatives || [],
      caseSensitive: input.caseSensitive || false
    }

    console.log('Creating text question with request data:', requestData)
    
    try {
      const result = await this.client.post<TextQuestionData>('/api/test/textquestions', requestData)
      console.log('Text question created successfully:', result)
      return result
    } catch (error) {
      console.error('Failed to create text question:', error)
      throw error
    }
  }

  // 🎯 快速创建填空题
  async createFillQuestion(answer: string, alternatives?: string[]): Promise<TextQuestionData> {
    try {
      // 首先尝试获取已有的text类型知识点
      let textKnowledge = await this.knowledgeManager.getByType('text')
      
      if (textKnowledge.length === 0) {
        // 如果没有知识点，尝试获取一个分类来创建知识点
        const categories = await this.categoryManager.getAll()
        
        if (categories.length === 0) {
          throw new MemorinaAPIError('没有找到任何分类，无法创建知识点和填空题', 400)
        }
        
        // 创建一个通用的填空题知识点
        const knowledge = await this.knowledgeManager.create({
          question: `关于"${answer}"的填空题`,
          explanation: `这是一个关于"${answer}"的填空题知识点`,
          type: 'text',
          categoryName: categories[0].name
        })
        
        // 等待一下确保知识点创建完成
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 重新获取知识点列表，确保新创建的知识点存在
        textKnowledge = await this.knowledgeManager.getByType('text')
        
        if (textKnowledge.length === 0) {
          // 如果还是没有，直接使用刚创建的知识点ID
    return this.create({
            knowledgeId: knowledge.id,
      answer,
      alternatives,
      type: 'fill'
    })
        }
      }
      
      // 使用现有的或新创建的知识点
      return this.create({
        knowledgeId: textKnowledge[0].id,
        answer,
        alternatives,
        type: 'fill'
      })
      
    } catch (error) {
      console.error('创建填空题失败:', error)
      throw error
    }
  }

  // 🎯 快速创建问答题
  async createEssayQuestion(categoryName: string, question: string, answer: string): Promise<TextQuestionData> {
    return this.create({
      categoryName,
      knowledgeQuestion: question,
      answer,
      type: 'essay',
      validation: 'contains'
    })
  }

  async getAll(): Promise<TextQuestionData[]> {
    return this.client.get<TextQuestionData[]>('/api/test/textquestions')
  }
}

// ===== 主SDK类 =====
export class MemorinaAPI {
  private client: MemorinaAPIClient
  public categories: CategoryManager
  public knowledge: KnowledgeManager
  public textQuestions: TextQuestionManager

  constructor(baseURL?: string) {
    this.client = new MemorinaAPIClient(baseURL)
    this.categories = new CategoryManager(this.client)
    this.knowledge = new KnowledgeManager(this.client, this.categories)
    this.textQuestions = new TextQuestionManager(this.client, this.knowledge, this.categories)
  }

  async healthCheck(): Promise<boolean> {
    return this.client.healthCheck()
  }

  async initDemoData(): Promise<void> {
    try {
      // 创建演示分类
      await this.client.post('/api/test/categories/demo')
      
      // 等待一下确保分类创建完成
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 获取创建的分类
      const categories = await this.categories.getAll()
      if (categories.length > 0) {
        // 创建演示知识点
        const knowledge = await this.knowledge.create({
          question: "什么是函数的极限？",
          explanation: "函数的极限是当自变量趋近某个值时，函数值趋近的值。",
          type: "text",
          categoryName: categories[0].name
        })
        
        // 等待知识点创建完成
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 创建演示文本题（使用刚创建的知识点ID）
        await this.textQuestions.create({
          knowledgeId: knowledge.id,
          answer: "极限",
          type: "fill",
          validation: "exact"
        })
      }
    } catch (error) {
      console.warn('演示数据初始化失败，可能已存在:', (error as Error).message)
    }
  }
}

// ===== 使用示例 =====
export async function demonstrateUsage() {
  const api = new MemorinaAPI()

  // 检查服务状态
  const isHealthy = await api.healthCheck()
  if (!isHealthy) {
    console.error('API服务不可用')
    return
  }

  try {
    // 🎯 创建分类（智能默认值）
    const category = await api.categories.create({
      name: "前端开发",
      description: "前端技术相关知识点"
      // icon和color会自动生成
    })
    console.log('✅ 分类创建成功:', category)

    // 🎯 创建知识点（使用分类名称）
    const knowledge = await api.knowledge.create({
      question: "什么是Vue3的Composition API？",
      explanation: "Composition API是Vue3提供的新API，允许更灵活地组织组件逻辑",
      type: "text",
      categoryName: "前端开发",  // 🎯 直接使用分类名称
      tags: ["Vue3", "JavaScript"],
      difficulty: 4
    })
    console.log('✅ 知识点创建成功:', knowledge)

    // 🎯 创建文本题（一行代码）
    const fillQuestion = await api.textQuestions.createFillQuestion(
      "Composition API",
      ["composition api", "组合式API"]
    )
    console.log('✅ 填空题创建成功:', fillQuestion)

    // 🎯 创建完整的问答题（自动处理所有依赖）
    const essayQuestion = await api.textQuestions.createEssayQuestion(
      "前端开发",
      "React和Vue有什么区别？",
      "React使用JSX和函数式编程理念，Vue使用模板语法和渐进式框架设计"
    )
    console.log('✅ 问答题创建成功:', essayQuestion)

  } catch (error) {
    if (error instanceof MemorinaAPIError) {
      console.error('❌ API错误:', error.message, '(状态码:', error.status, ')')
    } else {
      console.error('❌ 未知错误:', (error as Error).message)
    }
  }
}

// ===== 错误处理工具 =====
export function handleAPIError(error: unknown): string {
  if (error instanceof MemorinaAPIError) {
    switch (error.status) {
      case 400:
        return `请求参数错误: ${error.message}`
      case 404:
        return `资源不存在: ${error.message}`
      case 500:
        return `服务器错误，请稍后重试`
      default:
        return error.message
    }
  }
  return '未知错误，请检查网络连接'
}

// 默认导出
export const memorinAPI = new MemorinaAPI()

// 导出类型
export type {
  CategoryData,
  KnowledgeData,
  TextQuestionData,
  MemorinaAPIError
} 