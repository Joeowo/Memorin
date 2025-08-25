import type { 
  Category, Knowledge, BreadcrumbItem, 
  KnowledgeFilters, PaginationParams,
  CreateCategoryRequest, UpdateCategoryRequest,
  CreateKnowledgeRequest, UpdateKnowledgeRequest,
  SearchResult
} from '@/types/knowledge'
import type {
  PagedResponse, TextQuestionApi, ChoiceQuestionApi, CategoryApi
} from '@/types/api'

// API客户端配置
const API_BASE = 'http://localhost:8082'

class KnowledgeService {
  private async request<T>(url: string, options?: RequestInit): Promise<T> {
    const response = await fetch(`${API_BASE}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
      ...options,
    })
    
    if (!response.ok) {
      throw new Error(`API请求失败: ${response.status} ${response.statusText}`)
    }
    
    const data = await response.json()
    // 处理后端返回的统一响应格式
    return data.data || data
  }

  // 转换后端分类数据为前端格式
  private convertCategoriesToFrontend(categories: CategoryApi[]): Category[] {
    return categories.map(cat => ({
      id: cat.id,
      name: cat.name,
      parentId: cat.parentId || null,
      level: cat.level,
      description: cat.description,
      color: cat.color,
      icon: cat.icon,
      sortOrder: cat.sortOrder,
      knowledgeCount: cat.knowledgeCount || 0,
      directKnowledgeCount: cat.knowledgeCount || 0,
      totalKnowledgeCount: cat.knowledgeCount || 0,
      createdAt: cat.createdAt,
      updatedAt: cat.updatedAt
    }))
  }

  // 分类管理相关API - 使用测试端点
  async getCategoryTree(): Promise<Category[]> {
    const result = await this.request<PagedResponse<CategoryApi>>('/api/test/categories/tree')
    return this.convertCategoriesToFrontend(result.data || [])
  }
  
  async getCategoriesByParent(parentId: string | null): Promise<Category[]> {
    let url = '/api/test/categories'
    if (parentId) {
      url += `?parentId=${parentId}`
    }
    const result = await this.request<PagedResponse<CategoryApi>>(url)
    return this.convertCategoriesToFrontend(result.data || [])
  }
  
  async getBreadcrumb(categoryId: string | null): Promise<BreadcrumbItem[]> {
    if (!categoryId) {
      return [{ id: null, name: '知识管理', level: -1 }]
    }
    
    // 构建面包屑路径 - 递归获取父级分类
    try {
      const category = await this.getCategoryById(categoryId)
      const breadcrumb: BreadcrumbItem[] = []
      
      let current: Category | null = category
      while (current) {
        breadcrumb.unshift({
          id: current.id,
          name: current.name,
          level: current.level
        })
        
        if (current.parentId) {
          current = await this.getCategoryById(current.parentId)
        } else {
          break
        }
      }
      
      // 添加根节点
      breadcrumb.unshift({ id: null, name: '知识管理', level: -1 })
      
      return breadcrumb
    } catch (error) {
      console.error('获取面包屑失败:', error)
      return [{ id: null, name: '知识管理', level: -1 }]
    }
  }
  
  async createCategory(data: CreateCategoryRequest): Promise<Category> {
    const result = await this.request<CategoryApi>('/api/test/categories', {
      method: 'POST',
      body: JSON.stringify(data)
    })
    return this.convertCategoriesToFrontend([result])[0]
  }
  
  async updateCategory(id: string, data: UpdateCategoryRequest): Promise<Category> {
    const result = await this.request<CategoryApi>(`/api/test/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    })
    return this.convertCategoriesToFrontend([result])[0]
  }
  
  async deleteCategory(id: string): Promise<void> {
    await this.request<void>(`/api/test/categories/${id}`, {
      method: 'DELETE'
    })
  }
  
  async getCategoryById(id: string): Promise<Category> {
    const result = await this.request<CategoryApi>(`/api/test/categories/${id}`)
    return this.convertCategoriesToFrontend([result])[0]
  }

  // 获取文本题（填空题）
  private async getTextQuestions(params: URLSearchParams): Promise<TextQuestionApi[]> {
    try {
      const result = await this.request<PagedResponse<TextQuestionApi>>(`/api/test/textquestions?${params}`)
      return result.data || []
    } catch {
      return []
    }
  }
  
  // 获取选择题
  private async getChoiceQuestions(params: URLSearchParams): Promise<ChoiceQuestionApi[]> {
    try {
      const result = await this.request<PagedResponse<ChoiceQuestionApi>>(`/api/test/choicequestions?${params}`)
      return result.data || []
    } catch {
      return []
    }
  }
  
  // 转换文本题为Knowledge格式
  private convertTextQuestionToKnowledge(textQuestion: TextQuestionApi): Knowledge {
    return {
      id: textQuestion.id || '',
      question: textQuestion.baseKnowledge?.question || '',
      answer: textQuestion.answer || '',
      alternativeAnswers: textQuestion.alternativeAnswers || [],
      explanation: textQuestion.baseKnowledge?.explanation || '',
      categoryId: textQuestion.knowledgeId || '',
      type: 'fill',
      difficulty: textQuestion.baseKnowledge?.difficulty || 3,
      tags: textQuestion.baseKnowledge?.tags || [],
      status: textQuestion.baseKnowledge?.status as 'draft' | 'published' | 'archived' || 'published',
      reviewCount: 0,
      correctCount: 0,
      lastReviewed: null,
      nextReview: new Date().toISOString(),
      caseSensitive: textQuestion.caseSensitive || false,
      exactMatch: true,
      createdAt: textQuestion.createdAt || new Date().toISOString(),
      updatedAt: textQuestion.updatedAt || new Date().toISOString()
    }
  }
  
  // 转换选择题为Knowledge格式
  private convertChoiceQuestionToKnowledge(choiceQuestion: ChoiceQuestionApi): Knowledge {
    return {
      id: choiceQuestion.id || '',
      question: choiceQuestion.baseKnowledge?.question || '',
      answer: this.extractCorrectAnswer(choiceQuestion.options || []),
      alternativeAnswers: [],
      explanation: choiceQuestion.baseKnowledge?.explanation || '',
      categoryId: choiceQuestion.knowledgeId || '',
      type: 'choice',
      difficulty: choiceQuestion.baseKnowledge?.difficulty || 3,
      tags: choiceQuestion.baseKnowledge?.tags || [],
      status: choiceQuestion.baseKnowledge?.status as 'draft' | 'published' | 'archived' || 'published',
      reviewCount: 0,
      correctCount: 0,
      lastReviewed: null,
      nextReview: new Date().toISOString(),
      createdAt: choiceQuestion.createdAt || new Date().toISOString(),
      updatedAt: choiceQuestion.updatedAt || new Date().toISOString()
    }
  }
  
  // 从选择题选项中提取正确答案
  private extractCorrectAnswer(options: ChoiceQuestionApi['options']): string {
    const correctOptions = options.filter(opt => opt.isCorrect)
    return correctOptions.map(opt => opt.optionText).join(', ')
  }

  // 知识点管理相关API - 组合不同类型的题目
  async getKnowledgeList(
    filters: KnowledgeFilters, 
    pagination: PaginationParams
  ): Promise<SearchResult> {
    // 由于后端分离了不同类型的题目，我们需要分别获取并组合
    const params = new URLSearchParams({
      page: pagination.page.toString(),
      size: pagination.size.toString(),
    })
    
    if (filters.categoryId) {
      params.append('knowledgeId', filters.categoryId) // 后端使用knowledgeId参数
    }
    
    try {
      // 并行获取不同类型的题目
      const [textQuestions, choiceQuestions] = await Promise.all([
        this.getTextQuestions(params),
        this.getChoiceQuestions(params)
      ])
      
      // 转换为统一的Knowledge格式
      const allKnowledge: Knowledge[] = [
        ...textQuestions.map(q => this.convertTextQuestionToKnowledge(q)),
        ...choiceQuestions.map(q => this.convertChoiceQuestionToKnowledge(q))
      ]
      
      return {
        content: allKnowledge.slice(pagination.page * pagination.size, (pagination.page + 1) * pagination.size),
        totalElements: allKnowledge.length,
        totalPages: Math.ceil(allKnowledge.length / pagination.size),
        size: pagination.size,
        number: pagination.page
      }
    } catch (error) {
      console.error('获取知识点列表失败:', error)
      return {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: pagination.size,
        number: pagination.page
      }
    }
  }

  async getKnowledgeById(id: string): Promise<Knowledge> {
    // 尝试从不同类型的题目中查找
    try {
      const textQuestion = await this.request<TextQuestionApi>(`/api/test/textquestions/${id}`)
      return this.convertTextQuestionToKnowledge(textQuestion)
    } catch {
      try {
        const choiceQuestion = await this.request<ChoiceQuestionApi>(`/api/test/choicequestions/${id}`)
        return this.convertChoiceQuestionToKnowledge(choiceQuestion)
      } catch {
        throw new Error('知识点不存在')
      }
    }
  }

  async createKnowledge(data: CreateKnowledgeRequest): Promise<Knowledge> {
    if (data.type === 'fill') {
      const result = await this.request<TextQuestionApi>('/api/test/textquestions', {
        method: 'POST',
        body: JSON.stringify({
          knowledgeId: data.categoryId,
          answer: data.answer,
          alternativeAnswers: data.alternativeAnswers || [],
          caseSensitive: data.caseSensitive || false,
          textType: 'FILL_BLANK'
        })
      })
      return this.convertTextQuestionToKnowledge(result)
    } else {
      throw new Error('暂不支持此题型')
    }
  }
  
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  async updateKnowledge(_id: string, _data: UpdateKnowledgeRequest): Promise<Knowledge> {
    // 根据题目类型调用不同的更新API
    throw new Error('更新功能待实现')
  }
  
  async deleteKnowledge(id: string): Promise<void> {
    // 尝试删除不同类型的题目
    try {
      await this.request<void>(`/api/test/textquestions/${id}`, {
        method: 'DELETE'
      })
    } catch {
      try {
        await this.request<void>(`/api/test/choicequestions/${id}`, {
          method: 'DELETE'
        })
      } catch {
        throw new Error('删除失败')
      }
    }
  }
  
  async batchDeleteKnowledge(ids: string[]): Promise<void> {
    // 批量删除需要分别处理不同类型的题目
    const promises = ids.map(id => this.deleteKnowledge(id))
    await Promise.allSettled(promises)
  }

  // 搜索相关API
  async searchKnowledge(
    keyword: string,
    filters: Omit<KnowledgeFilters, 'keyword'>,
    pagination: PaginationParams
  ): Promise<SearchResult> {
    // 在各类型题目中搜索
    try {
      const [textResults, choiceResults] = await Promise.all([
        this.request<PagedResponse<TextQuestionApi>>(`/api/test/textquestions/search/${keyword}`).catch(() => ({ total: 0, data: [] })),
        this.request<PagedResponse<ChoiceQuestionApi>>(`/api/test/choicequestions/search/${keyword}`).catch(() => ({ total: 0, data: [] }))
      ])
      
      const allKnowledge: Knowledge[] = [
        ...(textResults.data || []).map(q => this.convertTextQuestionToKnowledge(q)),
        ...(choiceResults.data || []).map(q => this.convertChoiceQuestionToKnowledge(q))
      ]
      
      return {
        content: allKnowledge.slice(pagination.page * pagination.size, (pagination.page + 1) * pagination.size),
        totalElements: allKnowledge.length,
        totalPages: Math.ceil(allKnowledge.length / pagination.size),
        size: pagination.size,
        number: pagination.page
      }
    } catch (error) {
      console.error('搜索失败:', error)
      return {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: pagination.size,
        number: pagination.page
      }
    }
  }
  
  async getSearchSuggestions(keyword: string): Promise<string[]> {
    if (keyword.length < 2) return []
    
    // 暂时返回模拟数据，后续可以实现真实的搜索建议
    return [
      `${keyword}相关概念`,
      `${keyword}原理`,
      `${keyword}应用`,
      `${keyword}实例`
    ].slice(0, 5)
  }
}

export const knowledgeService = new KnowledgeService() 