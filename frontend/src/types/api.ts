// 后端API响应类型定义

export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  timestamp?: number
}

export interface PagedResponse<T> {
  total: number
  data: T[]
}

// 后端原始数据类型
export interface BaseKnowledgeApi {
  id: string
  question: string
  explanation: string
  categoryId: string
  type: string
  tags: string[]
  difficulty: number
  estimatedTime: number
  status: string
  createdAt: string
  updatedAt: string
}

export interface TextQuestionApi {
  id: string
  knowledgeId: string
  textType: string
  answer: string
  validationMode: string
  alternativeAnswers: string[]
  caseSensitive: boolean
  regexPattern?: string
  baseKnowledge?: BaseKnowledgeApi
  createdAt: string
  updatedAt: string
}

export interface ChoiceOptionApi {
  optionKey: string
  optionText: string
  isCorrect: boolean
  explanation?: string
  sortOrder: number
}

export interface ChoiceQuestionApi {
  id: string
  knowledgeId: string
  choiceType: string
  points: number
  partialCredit: boolean
  randomOrder: boolean
  options: ChoiceOptionApi[]
  explanation?: string
  baseKnowledge?: BaseKnowledgeApi
  createdAt: string
  updatedAt: string
}

export interface CategoryApi {
  id: string
  name: string
  parentId?: string | null
  level: number
  path: string
  description?: string
  color?: string
  icon?: string
  sortOrder: number
  isActive: boolean
  knowledgeCount?: number
  createdAt: string
  updatedAt: string
} 