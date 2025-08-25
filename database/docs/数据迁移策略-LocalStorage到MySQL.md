# Memorin 数据迁移策略：LocalStorage 到 MySQL

> **版本**: v2.0  
> **创建时间**: 2025-01-08  
> **迁移方式**: 离线迁移 + 在线验证  

## 📋 迁移概述

本文档详细说明了Memorin系统从纯前端LocalStorage存储迁移到MySQL数据库的完整方案，包括数据分析、迁移脚本、验证机制和回滚策略。

### 迁移目标

1. **零数据丢失**：确保所有用户数据完整迁移
2. **最小停机时间**：采用分步迁移策略
3. **数据一致性**：保证迁移后数据逻辑正确
4. **可回滚性**：出现问题可快速回滚
5. **性能优化**：迁移过程不影响系统性能

## 🔍 现有数据结构分析

### LocalStorage 数据结构

根据对现有系统的分析，LocalStorage中存储的主要数据结构：

```javascript
// 主要数据对象
const localStorageData = {
    // 知识库数据
    knowledgeBases: {
        "kb1": {
            id: "kb1",
            name: "软件工程",
            description: "软件工程相关知识点",
            icon: "🛠️",
            color: "#667eea",
            areas: ["area1", "area2"]
        }
    },
    
    // 知识区数据
    knowledgeAreas: {
        "area1": {
            id: "area1",
            name: "需求分析",
            color: "#667eea",
            knowledgeBase: "kb1",
            points: ["point1", "point2"]
        }
    },
    
    // 知识点数据
    knowledgePoints: {
        "point1": {
            id: "point1",
            question: "什么是软件工程？",
            answer: "软件工程是...",
            type: "fill", // 或 "choice"
            difficulty: 3,
            knowledgeArea: "area1",
            knowledgeBase: "kb1",
            // 选择题特有字段
            choices: ["选项A", "选项B", "选项C", "选项D"],
            correctChoice: 0
        }
    },
    
    // 用户复习数据
    reviewData: {
        "point1": {
            knowledgePointId: "point1",
            reviewCount: 5,
            correctCount: 4,
            consecutiveCorrect: 2,
            easiness: 2.5,
            interval: 6,
            repetitions: 3,
            lastReviewed: "2024-12-15T10:30:00.000Z",
            nextReview: "2025-01-10T09:00:00.000Z",
            isBookmarked: true
        }
    },
    
    // 错题记录
    mistakes: [
        {
            id: "mistake1",
            knowledgePointId: "point1",
            incorrectAnswer: "错误答案",
            timestamp: "2024-12-01T14:20:00.000Z",
            reviewedAt: null,
            isResolved: false
        }
    ],
    
    // 统计数据
    statistics: {
        totalReviews: 150,
        totalCorrect: 120,
        dailyGoal: 20,
        streakDays: 5,
        lastActiveDate: "2024-12-20"
    },
    
    // 用户设置
    settings: {
        theme: "auto",
        difficulty: 3,
        dailyGoal: 20,
        notifications: true
    }
};
```

## 📊 数据映射关系

### LocalStorage → MySQL 映射表

| LocalStorage 对象 | MySQL 表 | 映射说明 |
|-------------------|----------|----------|
| knowledgeBases | knowledge_bases + knowledge_base_metadata | 核心信息与元数据分离 |
| knowledgeAreas | knowledge_areas + knowledge_area_metadata | 核心信息与元数据分离 |
| knowledgePoints | knowledge_point_contents + knowledge_point_metadata + knowledge_point_choice_options | 内容、元数据、选项分离 |
| reviewData | user_knowledge_point_states + user_knowledge_point_sm2_data + user_knowledge_point_performance | 状态、算法、性能分离 |
| mistakes | - | 集成到 user_knowledge_point_states 中 |
| statistics | user_profiles + user_settings | 分散到相关表 |
| settings | user_settings + user_profiles | 用户设置和配置 |

### 关联关系处理

```sql
-- LocalStorage中的简单关联 → MySQL中的多对多关系
-- knowledgePoints.knowledgeArea → knowledge_area_content_relations
-- knowledgePoints.knowledgeBase → knowledge_base_content_relations
```

## 🔧 迁移脚本设计

### 1. 数据导出脚本（前端）

```javascript
// 文件: export_localstorage_data.js
function exportLocalStorageData() {
    const exportData = {
        version: "2.0.0",
        exportTime: new Date().toISOString(),
        userData: {
            userId: generateUserId(), // 为现有数据生成用户ID
            username: prompt("请输入用户名：") || "migrated_user",
            email: prompt("请输入邮箱：") || "user@example.com"
        },
        knowledgeBases: JSON.parse(localStorage.getItem('knowledgeBases') || '{}'),
        knowledgeAreas: JSON.parse(localStorage.getItem('knowledgeAreas') || '{}'),
        knowledgePoints: JSON.parse(localStorage.getItem('knowledgePoints') || '{}'),
        reviewData: JSON.parse(localStorage.getItem('reviewData') || '{}'),
        mistakes: JSON.parse(localStorage.getItem('mistakes') || '[]'),
        statistics: JSON.parse(localStorage.getItem('statistics') || '{}'),
        settings: JSON.parse(localStorage.getItem('settings') || '{}'),
        customQuestionLists: JSON.parse(localStorage.getItem('customQuestionLists') || '{}')
    };
    
    // 数据验证
    validateExportData(exportData);
    
    // 生成导出文件
    const blob = new Blob([JSON.stringify(exportData, null, 2)], {
        type: 'application/json'
    });
    
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `memorin_data_export_${Date.now()}.json`;
    a.click();
    URL.revokeObjectURL(url);
    
    return exportData;
}

function validateExportData(data) {
    const errors = [];
    
    // 验证必要字段
    if (!data.userData.username) errors.push("缺少用户名");
    if (!data.userData.email) errors.push("缺少邮箱");
    
    // 验证数据完整性
    Object.keys(data.knowledgePoints).forEach(pointId => {
        const point = data.knowledgePoints[pointId];
        if (!point.question || !point.answer) {
            errors.push(`知识点 ${pointId} 缺少必要内容`);
        }
    });
    
    if (errors.length > 0) {
        throw new Error("数据验证失败：\n" + errors.join('\n'));
    }
}

function generateUserId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
}
```

### 2. 数据转换脚本（Node.js）

```javascript
// 文件: data_transformer.js
const fs = require('fs');
const crypto = require('crypto');

class DataTransformer {
    constructor(inputFile) {
        this.inputData = JSON.parse(fs.readFileSync(inputFile, 'utf8'));
        this.outputSql = [];
        this.userId = null;
        this.idMappings = {
            knowledgeBases: {},
            knowledgeAreas: {},
            knowledgePoints: {}
        };
    }
    
    transform() {
        console.log('开始数据转换...');
        
        // 1. 创建用户
        this.createUser();
        
        // 2. 转换知识库
        this.transformKnowledgeBases();
        
        // 3. 转换知识区
        this.transformKnowledgeAreas();
        
        // 4. 转换知识点
        this.transformKnowledgePoints();
        
        // 5. 转换复习数据
        this.transformReviewData();
        
        // 6. 转换用户设置
        this.transformUserSettings();
        
        // 7. 建立关联关系
        this.createRelationships();
        
        console.log('数据转换完成!');
        return this.outputSql.join('\n\n');
    }
    
    createUser() {
        const userData = this.inputData.userData;
        this.userId = this.generateNewId();
        
        const passwordHash = '$2b$12$dummyHashForMigratedUser12345';
        
        this.outputSql.push(`
-- 创建迁移用户
INSERT INTO users (id, username, email, password_hash, status) VALUES 
(${this.userId}, '${userData.username}', '${userData.email}', '${passwordHash}', 'active');

INSERT INTO user_profiles (user_id, nickname, timezone, language) VALUES 
(${this.userId}, '${userData.username}', 'Asia/Shanghai', 'zh-CN');
        `);
    }
    
    transformKnowledgeBases() {
        const knowledgeBases = this.inputData.knowledgeBases;
        
        Object.keys(knowledgeBases).forEach(oldId => {
            const kb = knowledgeBases[oldId];
            const newId = this.generateNewId();
            this.idMappings.knowledgeBases[oldId] = newId;
            
            // 核心表
            this.outputSql.push(`
INSERT INTO knowledge_bases (id, owner_id, name, visibility, status) VALUES 
(${newId}, ${this.userId}, '${this.escapeString(kb.name)}', 'private', 'active');
            `);
            
            // 元数据表
            this.outputSql.push(`
INSERT INTO knowledge_base_metadata (knowledge_base_id, description, icon, color) VALUES 
(${newId}, '${this.escapeString(kb.description || '')}', '${kb.icon || '📚'}', '${kb.color || '#667eea'}');
            `);
        });
    }
    
    transformKnowledgeAreas() {
        const knowledgeAreas = this.inputData.knowledgeAreas;
        
        Object.keys(knowledgeAreas).forEach(oldId => {
            const area = knowledgeAreas[oldId];
            const newId = this.generateNewId();
            this.idMappings.knowledgeAreas[oldId] = newId;
            
            // 核心表
            this.outputSql.push(`
INSERT INTO knowledge_areas (id, creator_id, name, level, sort_order) VALUES 
(${newId}, ${this.userId}, '${this.escapeString(area.name)}', 1, 0);
            `);
            
            // 元数据表
            this.outputSql.push(`
INSERT INTO knowledge_area_metadata (knowledge_area_id, description, color) VALUES 
(${newId}, '${this.escapeString(area.description || '')}', '${area.color || '#667eea'}');
            `);
        });
    }
    
    transformKnowledgePoints() {
        const knowledgePoints = this.inputData.knowledgePoints;
        
        Object.keys(knowledgePoints).forEach(oldId => {
            const point = knowledgePoints[oldId];
            const newId = this.generateNewId();
            this.idMappings.knowledgePoints[oldId] = newId;
            
            // 计算内容哈希
            const contentHash = this.calculateContentHash(point);
            
            // 内容表
            this.outputSql.push(`
INSERT INTO knowledge_point_contents (id, content_hash, type, question, answer, explanation, created_by, is_public) VALUES 
(${newId}, '${contentHash}', '${point.type}', '${this.escapeString(point.question)}', '${this.escapeString(point.answer)}', '${this.escapeString(point.explanation || '')}', ${this.userId}, FALSE);
            `);
            
            // 元数据表
            this.outputSql.push(`
INSERT INTO knowledge_point_metadata (content_id, difficulty, estimated_time, tags) VALUES 
(${newId}, ${point.difficulty || 3}, ${this.estimateTime(point)}, '${JSON.stringify(point.tags || [])}');
            `);
            
            // 选择题选项
            if (point.type === 'choice' && point.choices) {
                point.choices.forEach((choice, index) => {
                    const isCorrect = index === point.correctChoice;
                    this.outputSql.push(`
INSERT INTO knowledge_point_choice_options (content_id, choice_type, option_key, option_text, is_correct, sort_order) VALUES 
(${newId}, 'single', '${String.fromCharCode(65 + index)}', '${this.escapeString(choice)}', ${isCorrect}, ${index});
                    `);
                });
            }
        });
    }
    
    transformReviewData() {
        const reviewData = this.inputData.reviewData;
        
        Object.keys(reviewData).forEach(oldPointId => {
            const review = reviewData[oldPointId];
            const newPointId = this.idMappings.knowledgePoints[oldPointId];
            
            if (!newPointId) return;
            
            // 用户知识点状态
            this.outputSql.push(`
INSERT INTO user_knowledge_point_states (user_id, content_id, mastery_level, review_count, correct_count, consecutive_correct, first_learned_at, last_reviewed_at, next_review_at, is_bookmarked) VALUES 
(${this.userId}, ${newPointId}, ${this.calculateMasteryLevel(review)}, ${review.reviewCount || 0}, ${review.correctCount || 0}, ${review.consecutiveCorrect || 0}, '${this.formatDate(review.firstLearned)}', '${this.formatDate(review.lastReviewed)}', '${this.formatDate(review.nextReview)}', ${review.isBookmarked || false});
            `);
            
            // SM-2 算法数据
            this.outputSql.push(`
INSERT INTO user_knowledge_point_sm2_data (user_id, content_id, easiness_factor, repetition_number, inter_repetition_interval) VALUES 
(${this.userId}, ${newPointId}, ${review.easiness || 2.5}, ${review.repetitions || 0}, ${review.interval || 1});
            `);
        });
    }
    
    transformUserSettings() {
        const settings = this.inputData.settings;
        const statistics = this.inputData.statistics;
        
        this.outputSql.push(`
INSERT INTO user_settings (user_id, theme, daily_review_goal, difficulty_preference, review_notification, email_notification) VALUES 
(${this.userId}, '${settings.theme || 'auto'}', ${settings.dailyGoal || 20}, ${settings.difficulty || 3}, ${settings.notifications !== false}, TRUE);
        `);
    }
    
    createRelationships() {
        const knowledgePoints = this.inputData.knowledgePoints;
        
        Object.keys(knowledgePoints).forEach(oldPointId => {
            const point = knowledgePoints[oldPointId];
            const newPointId = this.idMappings.knowledgePoints[oldPointId];
            const newAreaId = this.idMappings.knowledgeAreas[point.knowledgeArea];
            const newBaseId = this.idMappings.knowledgeBases[point.knowledgeBase];
            
            if (!newPointId) return;
            
            // 知识区-内容关联
            if (newAreaId) {
                this.outputSql.push(`
INSERT INTO knowledge_area_content_relations (knowledge_area_id, content_id, relevance_score, is_primary, relationship_type, added_by) VALUES 
(${newAreaId}, ${newPointId}, 1.0, TRUE, 'contains', ${this.userId});
                `);
            }
            
            // 知识库-内容关联
            if (newBaseId) {
                this.outputSql.push(`
INSERT INTO knowledge_base_content_relations (knowledge_base_id, content_id, sort_order, added_by) VALUES 
(${newBaseId}, ${newPointId}, 0, ${this.userId});
                `);
            }
        });
    }
    
    // 辅助方法
    generateNewId() {
        return Date.now() + Math.floor(Math.random() * 1000);
    }
    
    calculateContentHash(point) {
        const content = `${point.question}${point.answer}${point.type}`;
        return crypto.createHash('sha256').update(content).digest('hex');
    }
    
    calculateMasteryLevel(review) {
        const accuracy = review.reviewCount > 0 ? review.correctCount / review.reviewCount : 0;
        if (accuracy >= 0.9) return 5;
        if (accuracy >= 0.8) return 4;
        if (accuracy >= 0.6) return 3;
        if (accuracy >= 0.4) return 2;
        return 1;
    }
    
    estimateTime(point) {
        // 根据内容长度估算时间
        const questionLength = point.question ? point.question.length : 0;
        const baseTime = point.type === 'choice' ? 60 : 90;
        return Math.max(30, baseTime + questionLength * 0.5);
    }
    
    formatDate(dateString) {
        if (!dateString) return null;
        return new Date(dateString).toISOString().slice(0, 19).replace('T', ' ');
    }
    
    escapeString(str) {
        if (!str) return '';
        return str.replace(/'/g, "''").replace(/\\/g, '\\\\');
    }
}

// 使用示例
if (require.main === module) {
    const inputFile = process.argv[2];
    if (!inputFile) {
        console.error('请提供输入文件路径');
        process.exit(1);
    }
    
    try {
        const transformer = new DataTransformer(inputFile);
        const sql = transformer.transform();
        
        const outputFile = inputFile.replace('.json', '_migration.sql');
        fs.writeFileSync(outputFile, sql);
        
        console.log(`迁移SQL已生成: ${outputFile}`);
    } catch (error) {
        console.error('转换失败:', error.message);
        process.exit(1);
    }
}

module.exports = DataTransformer;
```

### 3. 数据验证脚本

```sql
-- 文件: validate_migration.sql
-- 数据迁移验证脚本

-- 1. 基础数据统计
SELECT 
    '基础数据统计' as category,
    'users' as table_name,
    COUNT(*) as record_count
FROM users
WHERE username LIKE 'migrated_%'

UNION ALL

SELECT 
    '基础数据统计' as category,
    'knowledge_bases' as table_name,
    COUNT(*) as record_count
FROM knowledge_bases

UNION ALL

SELECT 
    '基础数据统计' as category,
    'knowledge_points' as table_name,
    COUNT(*) as record_count
FROM knowledge_point_contents

UNION ALL

SELECT 
    '基础数据统计' as category,
    'review_data' as table_name,
    COUNT(*) as record_count
FROM user_knowledge_point_states;

-- 2. 数据完整性检查
SELECT 
    '完整性检查' as category,
    'orphaned_areas' as check_type,
    COUNT(*) as issue_count
FROM knowledge_areas ka
LEFT JOIN knowledge_area_metadata kam ON ka.id = kam.knowledge_area_id
WHERE kam.knowledge_area_id IS NULL

UNION ALL

SELECT 
    '完整性检查' as category,
    'orphaned_points' as check_type,
    COUNT(*) as issue_count
FROM knowledge_point_contents kpc
LEFT JOIN knowledge_point_metadata kpm ON kpc.id = kpm.content_id
WHERE kpm.content_id IS NULL

UNION ALL

SELECT 
    '完整性检查' as category,
    'invalid_relationships' as check_type,
    COUNT(*) as issue_count
FROM knowledge_area_content_relations kacr
LEFT JOIN knowledge_areas ka ON kacr.knowledge_area_id = ka.id
LEFT JOIN knowledge_point_contents kpc ON kacr.content_id = kpc.id
WHERE ka.id IS NULL OR kpc.id IS NULL;

-- 3. 业务逻辑检查
SELECT 
    '业务逻辑检查' as category,
    'invalid_review_counts' as check_type,
    COUNT(*) as issue_count
FROM user_knowledge_point_states
WHERE correct_count > review_count

UNION ALL

SELECT 
    '业务逻辑检查' as category,
    'invalid_mastery_levels' as check_type,
    COUNT(*) as issue_count
FROM user_knowledge_point_states
WHERE mastery_level < 1 OR mastery_level > 5

UNION ALL

SELECT 
    '业务逻辑检查' as category,
    'missing_choice_options' as check_type,
    COUNT(*) as issue_count
FROM knowledge_point_contents kpc
WHERE kpc.type = 'choice'
AND NOT EXISTS (
    SELECT 1 FROM knowledge_point_choice_options kpco 
    WHERE kpco.content_id = kpc.id
);

-- 4. 生成迁移报告
SELECT 
    '迁移报告' as section,
    CONCAT('总用户数: ', COUNT(*)) as summary
FROM users
WHERE username LIKE 'migrated_%'

UNION ALL

SELECT 
    '迁移报告' as section,
    CONCAT('总知识库数: ', COUNT(*)) as summary
FROM knowledge_bases

UNION ALL

SELECT 
    '迁移报告' as section,
    CONCAT('总知识点数: ', COUNT(*)) as summary
FROM knowledge_point_contents

UNION ALL

SELECT 
    '迁移报告' as section,
    CONCAT('总复习记录数: ', COUNT(*)) as summary
FROM user_knowledge_point_states;
```

## 🚀 迁移执行流程

### Phase 1: 准备阶段

1. **环境准备**
   ```bash
   # 1. 创建数据库备份
   mysqldump -u root -p memorin_db > backup_before_migration.sql
   
   # 2. 安装Node.js依赖
   npm install crypto fs
   
   # 3. 准备迁移工具
   mkdir migration_tools
   cd migration_tools
   ```

2. **数据导出**
   ```javascript
   // 在旧系统前端控制台执行
   exportLocalStorageData();
   ```

### Phase 2: 转换阶段

1. **数据转换**
   ```bash
   # 执行数据转换
   node data_transformer.js exported_data.json
   ```

2. **SQL检查**
   ```bash
   # 检查生成的SQL语法
   mysql -u root -p --execute="source exported_data_migration.sql" --verbose --dry-run
   ```

### Phase 3: 执行阶段

1. **测试环境迁移**
   ```bash
   # 1. 在测试环境执行迁移
   mysql -u root -p memorin_test < exported_data_migration.sql
   
   # 2. 执行验证脚本
   mysql -u root -p memorin_test < validate_migration.sql
   ```

2. **生产环境迁移**
   ```bash
   # 1. 停止应用服务
   systemctl stop memorin-app
   
   # 2. 执行迁移
   mysql -u root -p memorin_db < exported_data_migration.sql
   
   # 3. 执行验证
   mysql -u root -p memorin_db < validate_migration.sql
   
   # 4. 启动应用服务
   systemctl start memorin-app
   ```

### Phase 4: 验证阶段

1. **数据完整性验证**
   ```sql
   CALL PerformDataHealthCheck();
   ```

2. **业务功能测试**
   - 用户登录测试
   - 知识点复习测试
   - 统计数据显示测试
   - 错题本功能测试

## 🔄 回滚策略

### 自动回滚触发条件

1. 数据完整性检查失败
2. 关键业务功能异常
3. 性能显著下降
4. 用户数据丢失

### 回滚步骤

```bash
# 1. 立即停止服务
systemctl stop memorin-app

# 2. 恢复数据库
mysql -u root -p memorin_db < backup_before_migration.sql

# 3. 切换到旧版本应用
systemctl start memorin-app-legacy

# 4. 验证回滚成功
curl -f http://localhost:3000/health
```

## 🔍 质量保证

### 迁移前检查清单

- [ ] 完整备份现有数据
- [ ] 测试环境迁移成功
- [ ] 迁移脚本语法检查通过
- [ ] 验证脚本准备就绪
- [ ] 回滚方案准备完毕
- [ ] 用户通知已发送

### 迁移后验证清单

- [ ] 所有表数据记录数正确
- [ ] 外键关系完整
- [ ] 业务逻辑验证通过
- [ ] 性能测试通过
- [ ] 用户功能测试通过
- [ ] 数据完整性检查通过

## 📊 监控和报告

### 迁移过程监控

```sql
-- 创建迁移进度监控表
CREATE TABLE migration_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    step_name VARCHAR(100),
    status ENUM('pending', 'running', 'completed', 'failed'),
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    records_processed INT DEFAULT 0,
    error_message TEXT NULL
);

-- 记录迁移步骤
INSERT INTO migration_progress (step_name, status, start_time) 
VALUES ('user_creation', 'running', NOW());
```

### 迁移报告生成

```sql
-- 生成迁移完成报告
SELECT 
    '数据迁移完成报告' as title,
    NOW() as report_time,
    (SELECT COUNT(*) FROM users WHERE username LIKE 'migrated_%') as migrated_users,
    (SELECT COUNT(*) FROM knowledge_bases) as total_knowledge_bases,
    (SELECT COUNT(*) FROM knowledge_point_contents) as total_knowledge_points,
    (SELECT COUNT(*) FROM user_knowledge_point_states) as total_review_records;
```

## 🚨 常见问题和解决方案

### 问题1: 字符编码问题
**现象**: 中文字符显示乱码
**解决**: 确保导出和导入过程都使用UTF-8编码

### 问题2: 数据类型不匹配
**现象**: 某些字段值转换失败
**解决**: 在转换脚本中添加数据类型验证和转换逻辑

### 问题3: 性能问题
**现象**: 迁移过程过慢
**解决**: 分批处理大量数据，使用事务优化

### 问题4: 内存不足
**现象**: Node.js 转换脚本内存溢出
**解决**: 使用流式处理或分块处理大文件

## 📄 相关文档

- [数据库表结构设计](./数据库设计-多用户共享知识库与个人复习状态分离.md)
- [数据完整性验证系统](./数据完整性验证和一致性检查系统.md)
- [系统部署指南](../../deploy/)

---

**重要提醒**: 
- 迁移前务必做好完整备份
- 建议先在测试环境完整验证迁移流程
- 准备充足的时间窗口进行迁移
- 制定详细的应急预案 