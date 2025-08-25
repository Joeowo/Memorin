// Debug script for knowledge management module
import { memorinAPI } from '@/utils/memorin-sdk'

async function debugKnowledgeModule() {
  console.log('🔍 开始调试知识管理模块...')
  
  try {
    // 1. 检查服务状态
    console.log('📡 检查服务状态...')
    const isHealthy = await memorinAPI.healthCheck()
    console.log('✅ 服务健康检查:', isHealthy)
    
    if (!isHealthy) {
      console.error('❌ 服务不可用，请检查后端服务是否运行')
      return
    }
    
    // 2. 测试分类API
    console.log('📂 测试分类API...')
    try {
      const categories = await memorinAPI.categories.getAll()
      console.log('✅ 分类API响应:', categories)
      console.log(`📊 分类数量: ${Array.isArray(categories) ? categories.length : '无法确定'}`)
      
      if (Array.isArray(categories) && categories.length > 0) {
        console.log('📋 第一个分类:', categories[0])
      }
    } catch (error) {
      console.error('❌ 分类API错误:', error)
    }
    
    // 3. 测试知识点API
    console.log('💡 测试知识点API...')
    try {
      const knowledge = await memorinAPI.knowledge.getAll()
      console.log('✅ 知识点API响应:', knowledge)
      console.log(`📊 知识点数量: ${Array.isArray(knowledge) ? knowledge.length : '无法确定'}`)
    } catch (error) {
      console.error('❌ 知识点API错误:', error)
    }
    
    // 4. 测试文本题API
    console.log('📝 测试文本题API...')
    try {
      const textQuestions = await memorinAPI.textQuestions.getAll()
      console.log('✅ 文本题API响应:', textQuestions)
      console.log(`📊 文本题数量: ${Array.isArray(textQuestions) ? textQuestions.length : '无法确定'}`)
    } catch (error) {
      console.error('❌ 文本题API错误:', error)
    }
    
    // 5. 测试手动API调用
    console.log('🔧 测试手动API调用...')
    try {
      const response = await fetch('/api/test/categories')
      const data = await response.json()
      console.log('✅ 手动API调用结果:', data)
    } catch (error) {
      console.error('❌ 手动API调用错误:', error)
    }
    
    console.log('🎯 调试完成！')
    
  } catch (error) {
    console.error('💥 调试过程中发生错误:', error)
  }
}

// 创建全局调试函数
if (typeof window !== 'undefined') {
  (window as any).debugKnowledge = debugKnowledgeModule
}

// 立即运行调试
debugKnowledgeModule()

export { debugKnowledgeModule }