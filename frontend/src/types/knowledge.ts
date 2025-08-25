// 知识管理模块的类型定义

export interface Category {
  id: string
  name: string
  parentId: string | null
  level: number
  description?: string
  color?: string
  icon?: string
  sortOrder: number
  knowledgeCount: number
  directKnowledgeCount: number  // 直接包含的知识点数量
  totalKnowledgeCount: number   // 包含子分类的总知识点数量
  children?: Category[]
  createdAt: string
  updatedAt: string
}

export interface Knowledge {
  id: string
  question: string
  answer: string
  alternativeAnswers: string[]
  explanation: string
  categoryId: string
  type: 'fill' | 'choice' | 'code'
  difficulty: number  // 1-5
  tags: string[]
  status: 'draft' | 'published' | 'archived'
  
  // 复习相关
  reviewCount: number
  correctCount: number
  lastReviewed: string | null
  nextReview: string
  
  // 填空题特有字段
  caseSensitive?: boolean
  exactMatch?: boolean
  regexPattern?: string
  
  createdAt: string
  updatedAt: string
}

export interface BreadcrumbItem {
  id: string | null
  name: string
  level: number
}

export interface KnowledgeFilters {
  categoryId?: string | null
  type?: string[]
  difficulty?: number[]
  status?: string[]
  tags?: string[]
  keyword?: string
}

export interface PaginationParams {
  page: number
  size: number
  total: number
}

// API请求类型
export interface CreateCategoryRequest {
  name: string
  parentId?: string | null
  description?: string
  color?: string
  icon?: string
  sortOrder?: number
}

export interface UpdateCategoryRequest {
  name?: string
  description?: string
  color?: string
  icon?: string
  sortOrder?: number
}

export interface CreateKnowledgeRequest {
  question: string
  answer: string
  alternativeAnswers?: string[]
  explanation?: string
  categoryId: string
  type: 'fill' | 'choice' | 'code'
  difficulty?: number
  tags?: string[]
  status?: 'draft' | 'published'
  
  // 填空题特有字段
  caseSensitive?: boolean
  exactMatch?: boolean
  regexPattern?: string
}

export interface UpdateKnowledgeRequest {
  question?: string
  answer?: string
  alternativeAnswers?: string[]
  explanation?: string
  categoryId?: string
  difficulty?: number
  tags?: string[]
  status?: 'draft' | 'published' | 'archived'
  
  // 填空题特有字段
  caseSensitive?: boolean
  exactMatch?: boolean
  regexPattern?: string
}

// 搜索建议类型
export interface SearchSuggestion {
  id: string
  text: string
  type: 'question' | 'answer' | 'tag'
  knowledgeId?: string
}

// 搜索结果类型
export interface SearchResult {
  content: Knowledge[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 验证结果类型
export interface ValidationResult {
  correct: boolean
  score: number
  feedback?: string
}

// 缓存项类型
export interface CachedLevel {
  categories: Category[]
  timestamp: number
  ttl: number
} 