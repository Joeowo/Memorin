# Memorin 知识管理模块开发指导 Prompt

## 🎯 项目概述
你正在参与开发 Memorin 智能知识复习系统的知识管理模块。这是一个从纯前端应用重构为 Vue3 + Spring Boot 微服务架构的项目。

### 🚀 当前项目状态
- ✅ **已完成**: 用户服务 (Spring Boot 8081端口，包含完整的认证和异常处理)
- ✅ **已完成**: 前端项目架构 (Vue3 + TypeScript + Vite)
- ✅ **已完成**: 数据结构重构方案 (完善版 v2.1)
- 🚧 **开发中**: 知识管理模块 (你负责的部分)
- ⏳ **待开发**: 复习算法模块、统计分析模块

### 📋 关键文件位置
```
D:\CODE\memorin-demo\                           # 原始系统参考
├── js\knowledge.js                            # 1900行核心知识管理逻辑
├── js\storage.js                              # 1272行数据存储逻辑
└── js\app.js                                  # 主应用控制器

Memorin-rebuild\                               # 新系统开发
├── docs\数据结构重构方案-完善版.md               # 数据结构设计规范
├── frontend\                                  # Vue3前端项目
└── backend\
    ├── memorin-user-service\                  # 已完成的用户服务 (8081端口)
    └── memorin-knowledge-service\             # 需要创建的知识服务 (8082端口)
```

## 📊 核心数据结构 (必须严格遵循)

### 1. 知识分类系统 (Category)
```typescript
interface Category {
  id: string                    // CAT_{level}_{seq}_{hash} 格式
  name: string                  // 分类名称
  parentId: string | null       // 父分类ID，null为顶级
  level: number                 // 1:学科 2:章节 3:小节
  path: string                  // 完整路径，如"数学/高等数学/极限理论"
  description?: string          // 分类描述
  color: string                 // UI显示颜色 hex格式
  icon: string                  // UI显示图标 emoji
  sortOrder: number            // 同级排序权重
  isActive: boolean            // 软删除标记
  userId: string               // 关联用户
  createdAt: number            // 创建时间戳
  updatedAt: number            // 更新时间戳
}
```

### 2. 知识点实体系统 (解耦设计)
```typescript
// 基础知识点实体
interface BaseKnowledge {
  id: string                           // KP_{type}_{date}_{seq} 格式
  question: string                     // 问题内容
  explanation?: string                 // 答案解析
  categoryId: string                   // 所属分类ID
  type: 'text' | 'choice' | 'code'    // 题目类型
  tags: string[]                       // 标签数组
  difficulty: 1 | 2 | 3 | 4 | 5       // 难度等级
  estimatedTime: number               // 预估学习时间(分钟)
  status: 'draft' | 'published' | 'archived'  // 知识点状态
  userId: string                       // 关联用户
  createdAt: number                    // 创建时间戳
  updatedAt: number                    // 更新时间戳
}

// 文本题数据 (合并填空题和问答题)
interface TextQuestionData {
  id: string
  knowledgeId: string
  textType: 'fill' | 'essay'          // fill:填空题 essay:问答题
  answer: string                       // 标准答案/参考答案
  validationMode: 'exact' | 'contains' | 'regex' | 'manual'
  alternativeAnswers?: string[]        // 可接受的同义答案
  caseSensitive?: boolean             // 是否区分大小写
  regexPattern?: string               // 正则表达式
  scoringPoints?: ScoringPoint[]      // 评分要点(问答题)
  maxWordCount?: number               // 最大字数
  minWordCount?: number               // 最小字数
  createdAt: number
  updatedAt: number
}

// 选择题数据
interface ChoiceQuestionData {
  id: string
  knowledgeId: string
  choiceType: 'single' | 'multiple'   // 单选/多选
  options: ChoiceOption[]              // 选项列表
  correctAnswer: string                // 正确答案 "A" 或 "A,C"
  shuffleOptions: boolean             // 是否随机打乱选项
  createdAt: number
  updatedAt: number
}

// 编程题数据 (简化版)
interface CodeQuestionData {
  id: string
  knowledgeId: string
  language: 'javascript' | 'python' | 'java' | 'cpp' | 'sql' | 'html' | 'css'
  referenceCode: string               // 参考代码
  referenceAnswer: string             // 参考结果
  createdAt: number
  updatedAt: number
}
```

## 🚀 开发流程规范 (严格遵循)

### 🔴 渐进式开发原则 (CRITICAL)
- ✅ **每次只开发一个功能模块**
- ✅ **先完成后端API，再开发前端组件**
- ✅ **每个功能完成后立即测试验证**
- ❌ **禁止同时开发多个功能**
- ❌ **禁止跳跃式开发**

### 📋 开发任务优先级 (严格按顺序)

#### 🔴 Phase 1: 后端微服务基础 (3-4天)
1. **项目初始化**
   - 创建 Spring Boot 项目结构
   - 配置数据库连接 (H2开发/MySQL生产)
   - 集成用户服务JWT认证
   - 配置Redis缓存

2. **分类管理API**
   - Category实体和Repository
   - 分类CRUD接口 (/api/categories)
   - 层级关系维护 (path自动更新)
   - 软删除功能

3. **知识点基础API**
   - BaseKnowledge实体和Repository
   - 知识点CRUD接口 (/api/knowledge)
   - 分类关联和验证

#### 🟡 Phase 2: 题型数据管理 (2-3天)
4. **文本题数据API**
   - TextQuestionData实体
   - 文本题CRUD接口
   - 填空题和问答题支持

5. **选择题数据API**
   - ChoiceQuestionData实体
   - 选择题CRUD接口
   - 单选/多选支持

6. **编程题数据API**
   - CodeQuestionData实体
   - 编程题CRUD接口
   - 多语言支持

## 🛠️ 开发前必须确认的问题
在开始任何开发工作前，你必须明确回答：

1. **当前要开发哪个具体功能？** (从优先级列表中选择一个)
2. **是后端API还是前端组件？**
3. **依赖的前置功能是否已完成？**
4. **数据结构设计是否符合重构方案？**
5. **API设计是否遵循RESTful规范？**

## 🔧 API设计规范

### RESTful接口设计
```typescript
// 分类管理API
const categoryRoutes = {
  'GET /api/categories': '获取分类树',
  'GET /api/categories/:id': '获取单个分类',
  'POST /api/categories': '创建分类',
  'PUT /api/categories/:id': '更新分类',
  'DELETE /api/categories/:id': '删除分类',
  'GET /api/categories/:id/knowledge': '获取分类下的知识点'
}

// 知识点管理API
const knowledgeRoutes = {
  'GET /api/knowledge': '获取知识点列表',
  'GET /api/knowledge/:id': '获取单个知识点',
  'POST /api/knowledge': '创建知识点',
  'PUT /api/knowledge/:id': '更新知识点',
  'DELETE /api/knowledge/:id': '删除知识点',
  'GET /api/knowledge/search': '搜索知识点'
}
```

## 📱 UI设计要求 (保持一致性)

### 毛玻璃风格 (必须保持)
```scss
// 核心样式规范
.knowledge-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  transition: transform 0.3s ease;
}

.gradient-bg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
```

## 🧪 测试要求

### 后端API测试
```powershell
# 分类管理测试
curl -X GET http://localhost:8082/api/categories
curl -X POST http://localhost:8082/api/categories -H "Content-Type: application/json" -d '{"name":"数学","level":1,"color":"#667eea","icon":"📐"}'

# 知识点管理测试  
curl -X GET http://localhost:8082/api/knowledge
curl -X POST http://localhost:8082/api/knowledge -H "Content-Type: application/json" -d '{"question":"什么是极限？","type":"text","categoryId":"CAT_1_001_A7B2"}'
```

## 🎯 完成标准

### 功能完成标准
- ✅ **后端API**: 所有端点正常响应，返回正确的数据格式
- ✅ **前端组件**: 能够正确显示数据，交互功能正常
- ✅ **数据一致性**: 符合重构方案的数据结构设计
- ✅ **错误处理**: 合理的错误提示和异常处理
- ✅ **用户体验**: 保持原有毛玻璃风格，响应速度良好

### 代码质量标准
- ✅ **TypeScript**: 完整的类型定义，无any类型
- ✅ **组件设计**: 遵循单一职责原则，可复用性高
- ✅ **状态管理**: 合理使用Pinia，避免状态冗余
- ✅ **性能优化**: 合理的缓存策略，避免不必要的重复请求

## 🚨 关键注意事项

### 1. 数据结构严格遵循
- 必须按照《数据结构重构方案-完善版.md》的设计实现
- 不得随意修改实体字段和关联关系
- ID生成必须遵循有意义的格式规范

### 2. 题型解耦设计
- 不同题型的数据完全独立存储
- 通过type字段区分，使用联合类型确保类型安全
- 新增题型时只需添加新的QuestionData实体

### 3. 与原系统兼容
- 功能对等：新系统必须包含原系统的所有核心功能
- 数据完整迁移：确保原有数据能够完整迁移到新结构
- 用户体验一致：保持原有的操作习惯和界面风格

---

**重要提醒**: 严格遵循渐进式开发原则，每次只专注一个小功能，确保质量后再进行下一步开发。成功的关键在于按部就班，稳扎稳打！ 