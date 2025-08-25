// Test script to verify frontend integration with real backend data
import { memorinAPI } from '@/utils/memorin-sdk'

async function testRealDataIntegration() {
  console.log('🚀 开始测试前端与真实后端数据集成...')
  
  try {
    // 1. 检查服务状态
    const isHealthy = await memorinAPI.healthCheck()
    console.log('✅ 服务状态:', isHealthy ? '正常' : '异常')
    
    if (!isHealthy) {
      console.error('❌ 服务不可用')
      return
    }
    
    // 2. 获取真实分类数据
    console.log('📂 获取真实分类数据...')
    const categories = await memorinAPI.categories.getAll()
    console.log(`✅ 成功获取 ${categories.length} 个分类:`, categories)
    
    // 3. 获取真实知识点数据
    console.log('💡 获取真实知识点数据...')
    const knowledge = await memorinAPI.knowledge.getAll()
    console.log(`✅ 成功获取 ${knowledge.length} 个知识点:`, knowledge)
    
    // 4. 获取真实文本题数据
    console.log('📝 获取真实文本题数据...')
    const textQuestions = await memorinAPI.textQuestions.getAll()
    console.log(`✅ 成功获取 ${textQuestions.length} 个文本题:`, textQuestions)
    
    // 5. 测试创建新的分类和知识点（可选）
    if (categories.length === 0) {
      console.log('🆕 创建测试分类...')
      const newCategory = await memorinAPI.categories.create({
        name: '测试分类',
        description: '这是一个测试分类'
      })
      console.log('✅ 创建分类成功:', newCategory)
      
      console.log('🆕 创建测试知识点...')
      const newKnowledge = await memorinAPI.knowledge.create({
        question: '测试知识点问题',
        explanation: '这是一个测试知识点的详细解释',
        type: 'text',
        categoryName: '测试分类'
      })
      console.log('✅ 创建知识点成功:', newKnowledge)
    }
    
    console.log('🎉 所有测试通过！前端已成功集成真实后端数据')
    
  } catch (error) {
    console.error('❌ 测试失败:', error)
  }
}

// 运行测试
if (typeof window !== 'undefined') {
  // 在浏览器中运行
  (window as any).testRealDataIntegration = testRealDataIntegration
} else {
  // 在Node环境中运行
  testRealDataIntegration()
}

export { testRealDataIntegration }