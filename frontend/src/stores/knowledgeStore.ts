import { defineStore } from 'pinia'
import { MemorinaAPI, handleAPIError } from '@/utils/memorin-sdk'
import type { CategoryData, KnowledgeData, TextQuestionData } from '@/utils/memorin-sdk'

// 简化的状态接口
interface KnowledgeState {
  // 导航状态
  currentView: 'categories' | 'knowledge' | 'create-category' | 'create-knowledge' | 'edit-category' | 'edit-knowledge'
  
  // 数据状态
  categories: CategoryData[]
  knowledgePoints: KnowledgeData[]
  textQuestions: TextQuestionData[]
  
  // 当前编辑项
  currentCategory: CategoryData | null
  currentKnowledge: KnowledgeData | null
  
  // 选择状态
  selectedCategoryIds: string[]
  selectedKnowledgeIds: string[]
  
  // UI状态
  loading: boolean
  error: string | null
  
  // 统计数据
  stats: {
    totalCategories: number
    totalKnowledge: number
    totalTextQuestions: number
  }
}

// SDK实例
const api = new MemorinaAPI()

export const useKnowledgeStore = defineStore('knowledge', {
  state: (): KnowledgeState => ({
    currentView: 'categories',
    
    categories: [],
    knowledgePoints: [],
    textQuestions: [],
    
    currentCategory: null,
    currentKnowledge: null,
    
    selectedCategoryIds: [],
    selectedKnowledgeIds: [],
    
    loading: false,
    error: null,
    
    stats: {
      totalCategories: 0,
      totalKnowledge: 0,
      totalTextQuestions: 0
    }
  }),
  
  getters: {
    // 获取总计数
    totalSelected(): number {
      return this.selectedCategoryIds.length + this.selectedKnowledgeIds.length
    },
    
    // 是否有选中项
    hasSelection(): boolean {
      return this.totalSelected > 0
    }
  },
  
  actions: {
    // ========== 通用方法 ==========
    
    setLoading(loading: boolean) {
      this.loading = loading
    },
    
    setError(error: string | null) {
      this.error = error
    },
    
    clearError() {
      this.error = null
    },
    
    // ========== 数据加载方法 ==========
    
    async loadCategories() {
      this.setLoading(true)
      this.clearError()
      try {
        const categories = await api.categories.getAll()
        // 确保数据格式正确，处理可能的对象包装
        this.categories = Array.isArray(categories) ? categories : (categories as any).data || []
        this.stats.totalCategories = this.categories.length
        console.log(`📂 加载了 ${this.categories.length} 个分类`, this.categories)
      } catch (error) {
        this.setError(handleAPIError(error))
        console.error('❌ 加载分类失败:', error)
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadKnowledge() {
      this.setLoading(true)
      this.clearError()
      try {
        const knowledge = await api.knowledge.getAll()
        // 确保数据格式正确
        this.knowledgePoints = Array.isArray(knowledge) ? knowledge : (knowledge as any).data || []
        this.stats.totalKnowledge = this.knowledgePoints.length
        console.log(`💡 加载了 ${this.knowledgePoints.length} 个知识点`, this.knowledgePoints)
      } catch (error) {
        this.setError(handleAPIError(error))
        console.error('❌ 加载知识点失败:', error)
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadTextQuestions() {
      this.setLoading(true)
      this.clearError()
      try {
        const textQuestions = await api.textQuestions.getAll()
        // 确保数据格式正确
        this.textQuestions = Array.isArray(textQuestions) ? textQuestions : (textQuestions as any).data || []
        this.stats.totalTextQuestions = this.textQuestions.length
        console.log(`📝 加载了 ${this.textQuestions.length} 个文本题`, this.textQuestions)
      } catch (error) {
        this.setError(handleAPIError(error))
        console.error('❌ 加载文本题失败:', error)
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadAllData() {
      await Promise.all([
        this.loadCategories(),
        this.loadKnowledge(),
        this.loadTextQuestions()
      ])
    },
    
    // ========== 分类管理方法 ==========
    
    async createCategory(data: { name: string; description?: string; icon?: string; color?: string }) {
      this.setLoading(true)
      this.clearError()
      try {
        const newCategory = await api.categories.create(data)
        this.categories.push(newCategory)
        this.stats.totalCategories++
        return newCategory
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },

    async updateCategory(id: string, data: { name: string; description?: string; icon?: string; color?: string }) {
      this.setLoading(true)
      this.clearError()
      try {
        const updatedCategory = await api.categories.update(id, data)
        const index = this.categories.findIndex(cat => cat.id === id)
        if (index !== -1) {
          this.categories[index] = updatedCategory
        }
        return updatedCategory
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },

    async deleteCategory(id: string) {
      this.setLoading(true)
      this.clearError()
      try {
        await api.categories.delete(id)
        const index = this.categories.findIndex(cat => cat.id === id)
        if (index !== -1) {
          this.categories.splice(index, 1)
          this.stats.totalCategories--
        }
        // 清除选择状态
        const selectionIndex = this.selectedCategoryIds.indexOf(id)
        if (selectionIndex !== -1) {
          this.selectedCategoryIds.splice(selectionIndex, 1)
        }
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },
    
    // ========== 知识点管理方法 ==========
    
    async createKnowledge(data: {
      question: string
      explanation: string
      categoryName: string
      difficulty?: number
      estimatedTime?: number
    }) {
      this.setLoading(true)
      this.clearError()
      try {
        const newKnowledge = await api.knowledge.create({
          question: data.question,
          explanation: data.explanation,
          type: 'text',
          categoryName: data.categoryName,
          difficulty: data.difficulty || 3,
          estimatedTime: data.estimatedTime || 15
        })
        this.knowledgePoints.push(newKnowledge)
        this.stats.totalKnowledge++
        return newKnowledge
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },

    async updateKnowledge(id: string, data: {
      question: string
      explanation: string
      type?: 'text' | 'choice' | 'code'
      categoryName?: string
      tags?: string[]
      difficulty?: number
      estimatedTime?: number
    }) {
      this.setLoading(true)
      this.clearError()
      try {
        // 使用真实的SDK更新方法
        const updatedKnowledge = await api.knowledge.update(id, {
          question: data.question,
          explanation: data.explanation,
          type: data.type || this.knowledgePoints.find(k => k.id === id)?.type || 'text',
          categoryName: data.categoryName || '',
          tags: data.tags || [],
          difficulty: data.difficulty || 3,
          estimatedTime: data.estimatedTime || 15
        })
        
        // 更新本地状态
        const index = this.knowledgePoints.findIndex(kp => kp.id === id)
        if (index !== -1) {
          this.knowledgePoints[index] = updatedKnowledge
        }
        return updatedKnowledge
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },

    async deleteKnowledge(id: string) {
      this.setLoading(true)
      this.clearError()
      try {
        // 使用真实的SDK删除方法
        await api.knowledge.delete(id)
        
        // 更新本地状态
        const index = this.knowledgePoints.findIndex(kp => kp.id === id)
        if (index !== -1) {
          this.knowledgePoints.splice(index, 1)
          this.stats.totalKnowledge--
        }
        
        // 清除选择状态
        const selectionIndex = this.selectedKnowledgeIds.indexOf(id)
        if (selectionIndex !== -1) {
          this.selectedKnowledgeIds.splice(selectionIndex, 1)
        }
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },
    
    // ========== 填空题管理方法 ==========
    
    async createFillQuestion(answer: string, alternatives?: string[]) {
      this.setLoading(true)
      this.clearError()
      try {
        const newQuestion = await api.textQuestions.createFillQuestion(answer, alternatives)
        this.textQuestions.push(newQuestion)
        this.stats.totalTextQuestions++
        return newQuestion
      } catch (error) {
        this.setError(handleAPIError(error))
        throw error
      } finally {
        this.setLoading(false)
      }
    },
    
    // ========== 视图导航方法 ==========
    
    setCurrentView(view: KnowledgeState['currentView']) {
      this.currentView = view
    },
    
    showCategories() {
      this.currentView = 'categories'
      this.currentCategory = null
      this.currentKnowledge = null
    },
    
    showCreateCategory() {
      this.currentView = 'create-category'
      this.currentCategory = null
    },
    
    showEditCategory(category: CategoryData) {
      this.currentView = 'edit-category'
      this.currentCategory = category
    },
    
    showCreateKnowledge() {
      this.currentView = 'create-knowledge'
      this.currentKnowledge = null
    },
    
    showEditKnowledge(knowledge: KnowledgeData) {
      this.currentView = 'edit-knowledge'
      this.currentKnowledge = knowledge
    },
    
    // ========== 选择管理方法 ==========
    
    toggleCategorySelection(categoryId: string) {
      const index = this.selectedCategoryIds.indexOf(categoryId)
      if (index > -1) {
        this.selectedCategoryIds.splice(index, 1)
      } else {
        this.selectedCategoryIds.push(categoryId)
      }
    },
    
    toggleKnowledgeSelection(knowledgeId: string) {
      const index = this.selectedKnowledgeIds.indexOf(knowledgeId)
      if (index > -1) {
        this.selectedKnowledgeIds.splice(index, 1)
      } else {
        this.selectedKnowledgeIds.push(knowledgeId)
      }
    },
    
    clearSelection() {
      this.selectedCategoryIds = []
      this.selectedKnowledgeIds = []
    },
    
    selectAllCategories() {
      this.selectedCategoryIds = this.categories.map(cat => cat.id)
    },
    
    selectAllKnowledge() {
      this.selectedKnowledgeIds = this.knowledgePoints.map(kp => kp.id)
    }
  }
}) 