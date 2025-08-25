# Memorin智能知识复习系统 - Cursor AI 规则配置

## 🎯 项目概述
这是一个智能知识复习系统的重构项目，从纯前端静态应用升级为Vue3+微服务架构。

### 当前系统特点
- **技术栈**: HTML5 + CSS3 + JavaScript ES6+
- **架构**: 纯前端单体应用，LocalStorage数据存储
- **核心功能**: SM-2算法复习系统、三级知识管理、错题本、数据统计
- **UI特色**: 毛玻璃蓝紫色主题，响应式设计
- **部署**: 静态文件托管，支持离线使用

### 重构目标
- **前端升级**: Vue3 + TypeScript + Vite + Pinia
- **后端新建**: Spring Boot微服务架构
- **数据库**: MySQL集群 + Redis缓存
- **部署**: 容器化云原生部署
- **保持**: 现有UI风格和用户体验100%保留

## 📁 项目文件结构重要性排序

### 🔴 核心业务逻辑文件 (最高优先级，优先阅读)
```
js/
├── app.js              # 主应用控制器
├── review.js           # 复习管理核心逻辑 (2099行，重点重构)
├── knowledge.js        # 知识管理核心逻辑 (1900行，重点重构)
├── storage.js          # 数据存储管理 (1272行，重点重构)
└── statistics.js       # 统计分析逻辑
```

### 🟡 配置和架构文件 (高优先级)
```
package.json            # 项目配置
index.html             # 主页面结构
rebuildDocs/           # 重构文档中心
└── 需求文档/           # 需求分析文档
```

### 🟢 样式和资源文件 (中优先级)
```
css/
├── style.css          # 主样式文件
├── components.css     # 组件样式
└── responsive.css     # 响应式样式
```

### 🔵 测试和部署文件 (低优先级)
```
test/                  # 测试脚本
deploy/                # 部署配置
docs/                  # 现有文档
```

## 🎯 文件使用优化规则

### 1. 每次对话文件数量限制
- **最多同时阅读**: 3-5个相关文件
- **优先策略**: 先读取核心业务逻辑文件
- **避免**: 一次性读取大量无关文件

### 2. 代码分析策略
```yaml
问题分析:
  1. 先读取 app.js 了解整体架构
  2. 根据问题类型选择对应的核心文件
  3. 只读取与当前任务相关的文件部分

功能开发:
  1. 先查看现有实现逻辑
  2. 分析数据流和状态管理
  3. 设计Vue组件化方案

重构规划:
  1. 优先分析巨型文件 (review.js, knowledge.js, storage.js)
  2. 识别可拆分的模块和组件
  3. 设计微服务边界
```

### 3. 文件读取优先级
```javascript
// 按照以下顺序读取文件
const readingPriority = {
  highest: ['js/app.js', 'package.json'],
  high: ['js/review.js', 'js/knowledge.js', 'js/storage.js'],
  medium: ['index.html', 'css/style.css'],
  low: ['docs/', 'test/', 'deploy/']
}
```

## 🏗️ 代码结构化和解耦化要求

### 1. 当前系统问题识别
- **巨型单体文件**: review.js (2099行), knowledge.js (1900行), storage.js (1272行)
- **全局变量耦合**: 7个主要全局对象相互依赖
- **职责混乱**: UI逻辑与业务逻辑混合

### 2. Vue重构结构化要求
```yaml
组件设计原则:
  - 单一职责: 每个组件只负责一个明确功能
  - 可复用性: 提取公共组件和工具函数
  - 状态管理: 使用Pinia进行集中状态管理
  - 类型安全: 完整的TypeScript类型定义

目录结构规划:
  src/
  ├── components/     # 可复用组件
  ├── views/         # 页面级组件
  ├── stores/        # Pinia状态管理
  ├── services/      # API服务层
  ├── utils/         # 工具函数
  ├── types/         # TypeScript类型定义
  └── assets/        # 静态资源
```

### 3. 微服务解耦要求
```yaml
服务边界清晰:
  - 用户服务: 认证授权独立
  - 知识库服务: 数据管理独立
  - 复习服务: 算法逻辑独立
  - 统计服务: 分析计算独立

接口设计原则:
  - RESTful API设计
  - 统一错误处理
  - 接口版本管理
  - 完整的API文档
```

### 4. 代码质量要求
```typescript
// 示例：组件结构化要求
interface ComponentProps {
  // 明确的属性类型定义
}

interface ComponentState {
  // 明确的状态类型定义
}

// 使用Composition API
export default defineComponent({
  name: 'ComponentName',
  props: // 类型化的props,
  setup(props) {
    // 逻辑拆分为可复用的composables
    const { state, actions } = useComponentLogic()
    
    return {
      ...state,
      ...actions
    }
  }
})
```

## 🖥️ 终端环境配置

### 环境信息
- **操作系统**: Windows 10.0.26100
- **Shell**: PowerShell 5.1.26100.4202 (ConsoleHost)
- **Node.js**: v22.17.0
- **执行策略**: Restricted (限制性)
- **工作目录**: E:\Code\memorin-demo
- **语言环境**: zh-CN (中文)

### PowerShell语法要求
```powershell
# ✅ 正确的PowerShell语法
# 目录操作
Get-ChildItem                   # 而不是 ls
Set-Location "rebuildDocs"      # 而不是 cd
New-Item -ItemType Directory    # 而不是 mkdir

# 文件操作
Get-Content "file.txt"          # 而不是 cat
Copy-Item "src" "dest"          # 而不是 cp
Remove-Item "file.txt"          # 而不是 rm

# 环境变量
$env:NODE_ENV = "development"   # 而不是 export NODE_ENV=development

# 多命令执行
command1; command2             # 而不是 command1 && command2
```

### 路径处理注意事项
```powershell
# Windows路径格式
$projectPath = "E:\Code\memorin-demo"
$relativePath = ".\rebuildDocs\需求文档\"

# 处理中文路径
[System.Text.Encoding]::UTF8.GetString([System.Text.Encoding]::Default.GetBytes("需求文档"))
```

### 执行策略限制处理
```powershell
# 当前执行策略是Restricted，可能导致npm等脚本无法运行
# 如需运行脚本，建议使用以下方法：

# 方法1: 使用npx替代npm
npx create-vue@latest           # 而不是 npm create vue@latest

# 方法2: 直接调用node
node ".\node_modules\.bin\vite" # 而不是 npm run dev

# 方法3: 使用PowerShell别名
Get-Alias npm                   # 检查是否有npm别名
```

### 🚀 Spring Boot微服务启动规范 (CRITICAL)

#### 用户模块启动方式
```powershell
# ✅ 正确的用户服务启动命令
Set-Location "Memorin-rebuild\backend\memorin-user-service"

# 清理并编译项目
mvn clean compile

# 启动服务（开发环境）
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

#### 微服务启动通用规范
```powershell
# 1. 进入服务目录
Set-Location "Memorin-rebuild\backend\{service-name}"

# 2. 清理编译
mvn clean compile

# 3. 启动服务（指定配置文件）
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

# 4. 后台启动（可选）
Start-Process -NoNewWindow mvn -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=dev"
```

#### 服务端口配置
```yaml
微服务端口分配:
  用户服务: 8081
  知识库服务: 8082  
  复习服务: 8083
  统计服务: 8084
  API网关: 8080
```

#### 常见启动问题解决
```powershell
# 问题1: Maven插件未找到
# 解决: 确保在正确的服务目录下执行命令
Get-Location  # 检查当前目录

# 问题2: 端口被占用
# 解决: 检查端口占用情况
netstat -ano | findstr :8081

# 问题3: 配置文件加载失败
# 解决: 检查application-dev.yml文件是否存在
Get-ChildItem "src\main\resources\application-dev.yml"

# 问题4: 数据库连接失败
# 解决: 检查H2数据库配置
Get-Content "src\main\resources\application-dev.yml"
```

#### 开发环境测试命令
```powershell
# 健康检查
Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method Get

# 服务信息
Invoke-RestMethod -Uri "http://localhost:8081/api/test/info" -Method Get

# 创建用户测试
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/test/users" -Method Post -Body $body -ContentType "application/json"
```

## 🎨 UI风格保持要求

### 毛玻璃主题保留
```css
/* 核心设计元素必须保留 */
.glass-effect {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.gradient-background {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
```

### Vue组件化改造原则
- 保持现有视觉效果100%一致
- 将CSS样式封装为Vue组件
- 使用CSS-in-JS或样式模块化
- 保持响应式设计能力

## 📐 统一布局管理规范 (CRITICAL)

### 🚨 必须遵守的核心原则

1. **统一容器原则**: 所有页面必须使用 `.container` 类，禁止自定义宽度
2. **预定义网格原则**: 使用预定义的网格类(.stats-grid, .test-grid等)，禁止重复定义
3. **响应式统一原则**: 响应式样式由全局layout.css管理，禁止页面级重复定义
4. **布局分离原则**: 页面组件只管理内容样式，不涉及布局宽度和网格

### 🛠️ 标准布局模式

```vue
<template>
  <div class="page-view">
    <!-- 使用预定义网格类 -->
    <div class="stats-grid">
      <!-- 统计卡片内容 -->
    </div>
    
    <div class="action-grid">
      <!-- 操作按钮内容 -->
    </div>
  </div>
</template>

<style scoped>
.page-view {
  width: 100%;
  padding: 2rem 0;
}

/* ✅ 只定义页面特有的样式 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

/* ❌ 禁止重新定义网格和宽度 */
/* 
.stats-grid {
  display: grid;
  max-width: 1400px;
}
*/
</style>
```

### 📋 布局系统文件
- **src/styles/layout.css**: 统一布局管理文件
- **src/styles/README.md**: 布局系统使用文档

### 🚫 严格禁止
- 页面组件中定义 `max-width` 属性
- 重复定义网格布局 (`.stats-grid`, `.action-grid` 等)
- 页面级响应式媒体查询 (布局相关)
- 混合使用旧的和新的布局系统

## 💡 AI助手行为准则

### 🐌 渐进式开发严格控制 (CRITICAL)

#### 每次对话限制规则
- **文件操作限制**: 每次对话只能操作 1 个文件
- **函数操作限制**: 每次操作只能修改/创建 1 个函数或方法
- **代码量限制**: 单次生成代码不超过 50 行
- **等待确认**: 每次修改后必须等待用户确认再继续

#### 操作前必须询问
```
在进行任何代码修改前，必须询问用户：
1. "现在要修改哪个文件？"
2. "要在这个文件中修改/创建哪个具体函数？"  
3. "您确认只修改这一个函数吗？"
4. "是否需要我解释修改思路？"
```

#### 强制分步策略
```yaml
Step 1: 分析阶段
  - 只读取 1 个目标文件
  - 理解该文件的当前结构
  - 识别要修改的具体函数

Step 2: 设计阶段  
  - 只针对 1 个函数设计修改方案
  - 说明修改原因和预期效果
  - 展示修改前后的对比

Step 3: 实施阶段
  - 只修改这 1 个函数
  - 提供详细的修改说明
  - 等待用户确认后再继续

Step 4: 验证阶段
  - 检查修改是否符合预期
  - 询问是否需要调整
  - 确认是否可以进行下一个函数
```

#### 禁止行为
- ❌ 禁止一次性生成整个文件
- ❌ 禁止同时修改多个函数
- ❌ 禁止在用户未明确要求时修改额外代码
- ❌ 禁止提供"完整示例"（除非用户明确要求）
- ❌ 禁止跨文件操作（除非用户明确指定）

### DO (应该做的)
- ✅ 每次只专注一个小目标
- ✅ 详细解释每个修改的原因
- ✅ 提供清晰的修改前后对比
- ✅ 主动询问用户的确认
- ✅ 优先阅读核心业务逻辑文件
- ✅ 分析代码时关注结构化和解耦机会
- ✅ 使用PowerShell兼容的命令语法
- ✅ 保持与现有UI风格的一致性
- ✅ 考虑Vue组件化的最佳实践
- ✅ **遵守响应式布局三大原则**

### DON'T (不应该做的)
- ❌ 一次性读取过多无关文件
- ❌ 一次性生成大量代码
- ❌ 在没有确认的情况下进行多个修改
- ❌ 忽略现有系统的优秀设计
- ❌ 使用Unix shell语法
- ❌ 破坏现有的用户体验
- ❌ 提供脱离实际的理想化方案
- ❌ 忽略技术债务的渐进式解决
- ❌ **硬编码关键布局尺寸**
- ❌ **跨组件样式依赖**

## 🔄 持续优化

这个.cursorrules文件会随着项目进展不断更新，确保AI助手始终了解最新的项目状态和要求。
