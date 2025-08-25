# Phase 2 - 数据库设计文档

## 📋 **文档信息**

| 项目信息 | 详情 |
|---------|------|
| **文档标题** | Phase 2 数据库设计文档 |
| **数据库类型** | H2(开发) / MySQL(生产) |
| **字符集** | UTF-8 |
| **引擎** | InnoDB |
| **版本** | v2.0 |

## 🎯 **数据库设计目标**

### **核心原则**
1. **数据完整性**：外键约束和业务规则约束
2. **性能优化**：合理的索引设计和查询优化
3. **扩展性**：支持未来功能扩展的灵活设计
4. **一致性**：统一的命名规范和数据类型
5. **安全性**：用户数据隔离和权限控制

## 📊 **数据库概览**

### **表结构统计**
- **主表数量**: 3张（text_question_data, choice_question_data, code_question_data）
- **关联表数量**: 3张（scoring_points, options, test_cases）
- **索引总数**: 45个（性能优化）
- **约束条件**: 36个（数据完整性）
- **预计存储**: 10万题目约300MB

### **数据模型关系图**
```
BaseKnowledge (1) ──┐
                   │
                   ├── TextQuestionData (1:1)
                   │   └── ScoringPoint (1:N)
                   │
                   ├── ChoiceQuestionData (1:1)
                   │   └── ChoiceOption (1:N)
                   │
                   └── CodeQuestionData (1:1)
                       └── TestCase (1:N)
```

---

## 🔤 **TextQuestionData - 文本题表设计**

### **主表: text_question_data**

```sql
CREATE TABLE text_question_data (
    id VARCHAR(50) NOT NULL COMMENT '文本题ID，格式：TQD_{date}_{seq}',
    knowledge_id VARCHAR(50) NOT NULL COMMENT '关联的知识点ID',
    text_type VARCHAR(10) NOT NULL COMMENT '题型：fill-填空题，essay-问答题',
    answer VARCHAR(2000) NOT NULL COMMENT '标准答案',
    validation_mode VARCHAR(20) NOT NULL COMMENT '验证模式：exact-精确，contains-包含，regex-正则，manual-人工',
    alternative_answer_list TEXT COMMENT '备选答案JSON数组',
    case_sensitive BOOLEAN DEFAULT FALSE COMMENT '是否区分大小写',
    regex_pattern VARCHAR(500) COMMENT '正则表达式模式',
    max_word_count INTEGER COMMENT '最大字数限制',
    min_word_count INTEGER COMMENT '最小字数限制',
    created_at BIGINT NOT NULL COMMENT '创建时间戳',
    updated_at BIGINT NOT NULL COMMENT '更新时间戳',
    
    PRIMARY KEY (id),
    
    -- 索引设计
    INDEX idx_text_knowledge_id (knowledge_id),
    INDEX idx_text_type (text_type),
    INDEX idx_text_validation_mode (validation_mode),
    INDEX idx_text_created_at (created_at),
    INDEX idx_text_complex (text_type, validation_mode, created_at),
    
    -- 约束条件
    CONSTRAINT chk_text_type CHECK (text_type IN ('fill', 'essay')),
    CONSTRAINT chk_validation_mode CHECK (validation_mode IN ('exact', 'contains', 'regex', 'manual')),
    CONSTRAINT chk_word_count CHECK (max_word_count IS NULL OR min_word_count IS NULL OR max_word_count >= min_word_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文本题数据表';
```

### **关联表: text_question_scoring_points**

```sql
CREATE TABLE text_question_scoring_points (
    text_question_id VARCHAR(50) NOT NULL COMMENT '文本题ID',
    description VARCHAR(500) NOT NULL COMMENT '评分点描述',
    points INTEGER NOT NULL COMMENT '分值',
    keywords VARCHAR(1000) COMMENT '关键词，逗号分隔',
    sort_order INTEGER NOT NULL COMMENT '排序顺序',
    
    -- 索引设计
    INDEX idx_scoring_text_id (text_question_id),
    INDEX idx_scoring_sort_order (text_question_id, sort_order),
    
    -- 约束条件
    CONSTRAINT chk_scoring_points CHECK (points > 0),
    CONSTRAINT chk_scoring_sort_order CHECK (sort_order >= 0),
    
    -- 外键约束
    FOREIGN KEY (text_question_id) REFERENCES text_question_data(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文本题评分点表';
```

### **字段详细说明**

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|-------|------|------|------|------|
| id | VARCHAR | 50 | PK, NOT NULL | 主键，格式：TQD_20250815_001 |
| knowledge_id | VARCHAR | 50 | NOT NULL | 外键，关联base_knowledge.id |
| text_type | VARCHAR | 10 | NOT NULL, CHECK | fill(填空题)/essay(问答题) |
| answer | VARCHAR | 2000 | NOT NULL | 标准答案内容 |
| validation_mode | VARCHAR | 20 | NOT NULL, CHECK | 验证模式 |
| alternative_answer_list | TEXT | - | NULL | JSON格式的备选答案数组 |
| case_sensitive | BOOLEAN | - | DEFAULT FALSE | 是否区分大小写 |
| regex_pattern | VARCHAR | 500 | NULL | 正则表达式验证模式 |
| max_word_count | INTEGER | - | NULL, CHECK | 最大字数限制 |
| min_word_count | INTEGER | - | NULL, CHECK | 最小字数限制 |

---

## ☑️ **ChoiceQuestionData - 选择题表设计**

### **主表: choice_question_data**

```sql
CREATE TABLE choice_question_data (
    id VARCHAR(50) NOT NULL COMMENT '选择题ID，格式：CQD_{date}_{seq}',
    knowledge_id VARCHAR(50) NOT NULL COMMENT '关联的知识点ID',
    choice_type VARCHAR(10) NOT NULL COMMENT '题型：single-单选，multiple-多选',
    points INTEGER NOT NULL COMMENT '题目总分值',
    partial_credit BOOLEAN DEFAULT FALSE COMMENT '是否支持部分得分（多选题）',
    random_order BOOLEAN DEFAULT FALSE COMMENT '是否随机排序选项',
    explanation TEXT COMMENT '题目解析说明',
    created_at BIGINT NOT NULL COMMENT '创建时间戳',
    updated_at BIGINT NOT NULL COMMENT '更新时间戳',
    
    PRIMARY KEY (id),
    
    -- 索引设计
    INDEX idx_choice_knowledge_id (knowledge_id),
    INDEX idx_choice_type (choice_type),
    INDEX idx_choice_points (points),
    INDEX idx_choice_created_at (created_at),
    INDEX idx_choice_partial_credit (partial_credit),
    INDEX idx_choice_complex (choice_type, points, created_at),
    
    -- 约束条件
    CONSTRAINT chk_choice_type CHECK (choice_type IN ('single', 'multiple')),
    CONSTRAINT chk_choice_points CHECK (points > 0 AND points <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选择题数据表';
```

### **关联表: choice_question_options**

```sql
CREATE TABLE choice_question_options (
    choice_question_id VARCHAR(50) NOT NULL COMMENT '选择题ID',
    option_key VARCHAR(1) NOT NULL COMMENT '选项键：A,B,C,D等',
    option_text VARCHAR(500) NOT NULL COMMENT '选项内容',
    is_correct BOOLEAN NOT NULL COMMENT '是否为正确答案',
    explanation VARCHAR(1000) COMMENT '选项解析',
    sort_order INTEGER NOT NULL COMMENT '排序顺序',
    
    -- 索引设计
    INDEX idx_option_choice_id (choice_question_id),
    INDEX idx_option_sort_order (choice_question_id, sort_order),
    INDEX idx_option_key (choice_question_id, option_key),
    INDEX idx_option_correct (choice_question_id, is_correct),
    
    -- 约束条件
    CONSTRAINT chk_option_key CHECK (option_key IN ('A', 'B', 'C', 'D', 'E', 'F')),
    CONSTRAINT chk_option_sort_order CHECK (sort_order >= 0),
    
    -- 外键约束
    FOREIGN KEY (choice_question_id) REFERENCES choice_question_data(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选择题选项表';
```

### **字段详细说明**

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|-------|------|------|------|------|
| id | VARCHAR | 50 | PK, NOT NULL | 主键，格式：CQD_20250815_001 |
| choice_type | VARCHAR | 10 | NOT NULL, CHECK | single(单选)/multiple(多选) |
| points | INTEGER | - | NOT NULL, CHECK | 题目总分值，1-100 |
| partial_credit | BOOLEAN | - | DEFAULT FALSE | 多选题是否支持部分得分 |
| random_order | BOOLEAN | - | DEFAULT FALSE | 选项是否随机排序 |
| option_key | VARCHAR | 1 | NOT NULL, CHECK | 选项键：A/B/C/D/E/F |
| option_text | VARCHAR | 500 | NOT NULL | 选项文本内容 |
| is_correct | BOOLEAN | - | NOT NULL | 是否为正确答案 |

---

## 💻 **CodeQuestionData - 编程题表设计**

### **主表: code_question_data**

```sql
CREATE TABLE code_question_data (
    id VARCHAR(50) NOT NULL COMMENT '编程题ID，格式：CQDC_{date}_{seq}',
    knowledge_id VARCHAR(50) NOT NULL COMMENT '关联的知识点ID',
    programming_language VARCHAR(20) NOT NULL COMMENT '编程语言',
    title VARCHAR(200) NOT NULL COMMENT '题目标题',
    description VARCHAR(5000) NOT NULL COMMENT '题目描述',
    difficulty_level INTEGER NOT NULL COMMENT '难度等级：1-5',
    points INTEGER NOT NULL COMMENT '题目总分值',
    initial_code VARCHAR(3000) COMMENT '初始代码模板',
    hints VARCHAR(1000) COMMENT '解题提示',
    standard_answer VARCHAR(5000) COMMENT '标准答案代码',
    created_at BIGINT NOT NULL COMMENT '创建时间戳',
    updated_at BIGINT NOT NULL COMMENT '更新时间戳',
    
    PRIMARY KEY (id),
    
    -- 索引设计
    INDEX idx_code_knowledge_id (knowledge_id),
    INDEX idx_code_language (programming_language),
    INDEX idx_code_difficulty (difficulty_level),
    INDEX idx_code_points (points),
    INDEX idx_code_created_at (created_at),
    INDEX idx_code_title (title),
    INDEX idx_code_complex (programming_language, difficulty_level, points),
    INDEX idx_code_search (title, programming_language, difficulty_level),
    
    -- 约束条件
    CONSTRAINT chk_code_language CHECK (programming_language IN ('java', 'python', 'javascript', 'cpp', 'c', 'sql', 'html', 'css')),
    CONSTRAINT chk_code_difficulty CHECK (difficulty_level >= 1 AND difficulty_level <= 5),
    CONSTRAINT chk_code_points CHECK (points >= 1 AND points <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编程题数据表';
```

### **关联表: code_question_test_cases**

```sql
CREATE TABLE code_question_test_cases (
    code_question_id VARCHAR(50) NOT NULL COMMENT '编程题ID',
    case_name VARCHAR(100) NOT NULL COMMENT '测试用例名称',
    input_data VARCHAR(2000) NOT NULL COMMENT '输入数据',
    expected_output VARCHAR(2000) NOT NULL COMMENT '期望输出',
    is_hidden BOOLEAN NOT NULL COMMENT '是否为隐藏测试用例',
    case_points INTEGER NOT NULL COMMENT '测试用例分值',
    sort_order INTEGER NOT NULL COMMENT '排序顺序',
    description VARCHAR(500) COMMENT '用例描述',
    
    -- 索引设计
    INDEX idx_testcase_code_id (code_question_id),
    INDEX idx_testcase_sort_order (code_question_id, sort_order),
    INDEX idx_testcase_hidden (code_question_id, is_hidden),
    INDEX idx_testcase_points (code_question_id, case_points),
    
    -- 约束条件
    CONSTRAINT chk_testcase_points CHECK (case_points > 0),
    CONSTRAINT chk_testcase_sort_order CHECK (sort_order >= 0),
    
    -- 外键约束
    FOREIGN KEY (code_question_id) REFERENCES code_question_data(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编程题测试用例表';
```

### **字段详细说明**

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|-------|------|------|------|------|
| id | VARCHAR | 50 | PK, NOT NULL | 主键，格式：CQDC_20250815_001 |
| programming_language | VARCHAR | 20 | NOT NULL, CHECK | 支持8种编程语言 |
| title | VARCHAR | 200 | NOT NULL | 题目标题 |
| description | VARCHAR | 5000 | NOT NULL | 题目详细描述 |
| difficulty_level | INTEGER | - | NOT NULL, CHECK | 难度等级1-5 |
| points | INTEGER | - | NOT NULL, CHECK | 题目总分值1-100 |
| initial_code | VARCHAR | 3000 | NULL | 初始代码模板 |
| hints | VARCHAR | 1000 | NULL | 解题提示信息 |
| standard_answer | VARCHAR | 5000 | NULL | 标准答案代码 |
| case_name | VARCHAR | 100 | NOT NULL | 测试用例名称 |
| input_data | VARCHAR | 2000 | NOT NULL | 测试输入数据 |
| expected_output | VARCHAR | 2000 | NOT NULL | 期望输出结果 |
| is_hidden | BOOLEAN | - | NOT NULL | 是否隐藏测试用例 |

---

## 🗂️ **索引设计策略**

### **性能优化原则**
1. **查询频率优先**：为常用查询字段创建索引
2. **复合索引**：多字段联合查询的复合索引
3. **覆盖索引**：包含查询所需所有字段的索引
4. **前缀索引**：长字符串字段的前缀索引

### **主要索引分类**

#### **1. 主键索引**
```sql
-- 每张主表的主键索引（自动创建）
PRIMARY KEY (id)
```

#### **2. 外键索引**
```sql
-- 知识点关联索引
INDEX idx_{table}_knowledge_id (knowledge_id)

-- 关联表外键索引
INDEX idx_{nested}_main_id ({main_table}_id)
```

#### **3. 业务查询索引**
```sql
-- 按类型查询
INDEX idx_{table}_type (type_field)

-- 按时间查询
INDEX idx_{table}_created_at (created_at)

-- 按分值查询
INDEX idx_{table}_points (points)
```

#### **4. 复合索引**
```sql
-- 复合条件查询优化
INDEX idx_{table}_complex (type, difficulty_level, created_at)

-- 搜索查询优化
INDEX idx_{table}_search (title, type, difficulty_level)
```

#### **5. 排序索引**
```sql
-- 排序字段优化
INDEX idx_{nested}_sort_order ({main_table}_id, sort_order)
```

### **索引使用统计**

| 表名 | 索引数量 | 主要用途 |
|------|----------|----------|
| text_question_data | 5个 | 查询优化、排序优化 |
| text_question_scoring_points | 2个 | 关联查询、排序 |
| choice_question_data | 6个 | 多维度查询优化 |
| choice_question_options | 4个 | 选项查询、排序 |
| code_question_data | 8个 | 复杂查询、搜索优化 |
| code_question_test_cases | 4个 | 测试用例查询 |
| **总计** | **29个** | **全面性能优化** |

---

## 📏 **数据类型设计规范**

### **字符串类型选择**
```sql
-- ID字段：固定长度，性能优化
id VARCHAR(50)

-- 短文本：标题、名称等
title VARCHAR(200)
name VARCHAR(100)

-- 中文本：描述、答案等
description VARCHAR(2000)
answer VARCHAR(2000)

-- 长文本：代码、详细内容等
initial_code VARCHAR(3000)
standard_answer VARCHAR(5000)

-- 超长文本：不定长内容
explanation TEXT
alternative_answer_list TEXT
```

### **数值类型选择**
```sql
-- 整数：分值、等级、顺序
points INTEGER          -- 1-100范围
difficulty_level INTEGER -- 1-5范围
sort_order INTEGER      -- >=0

-- 长整数：时间戳
created_at BIGINT
updated_at BIGINT
```

### **布尔类型**
```sql
-- 状态标识
is_active BOOLEAN DEFAULT TRUE
is_correct BOOLEAN NOT NULL
is_hidden BOOLEAN DEFAULT FALSE
case_sensitive BOOLEAN DEFAULT FALSE
```

### **枚举类型（通过CHECK约束实现）**
```sql
-- 文本题类型
CONSTRAINT chk_text_type CHECK (text_type IN ('fill', 'essay'))

-- 验证模式
CONSTRAINT chk_validation_mode CHECK (validation_mode IN ('exact', 'contains', 'regex', 'manual'))

-- 选择题类型
CONSTRAINT chk_choice_type CHECK (choice_type IN ('single', 'multiple'))

-- 编程语言
CONSTRAINT chk_code_language CHECK (programming_language IN ('java', 'python', 'javascript', 'cpp', 'c', 'sql', 'html', 'css'))
```

---

## 🔗 **外键关系设计**

### **关系类型说明**

#### **1. 一对一关系**
```sql
-- BaseKnowledge -> TextQuestionData
-- BaseKnowledge -> ChoiceQuestionData  
-- BaseKnowledge -> CodeQuestionData

-- 通过knowledge_id外键实现
-- 每个知识点只能对应一个题型数据
```

#### **2. 一对多关系**
```sql
-- TextQuestionData -> ScoringPoint
FOREIGN KEY (text_question_id) REFERENCES text_question_data(id) ON DELETE CASCADE

-- ChoiceQuestionData -> ChoiceOption
FOREIGN KEY (choice_question_id) REFERENCES choice_question_data(id) ON DELETE CASCADE

-- CodeQuestionData -> TestCase
FOREIGN KEY (code_question_id) REFERENCES code_question_data(id) ON DELETE CASCADE
```

### **级联操作策略**
- **ON DELETE CASCADE**：删除主表记录时自动删除关联表记录
- **ON UPDATE RESTRICT**：主键更新限制（ID一般不更新）

---

## 📊 **存储空间估算**

### **单记录存储空间**

| 表名 | 基础字段 | 动态字段 | 平均大小 | 最大大小 |
|------|----------|----------|----------|----------|
| text_question_data | 500B | 0-1500B | 1KB | 8KB |
| text_question_scoring_points | 200B | 0-300B | 300B | 800B |
| choice_question_data | 300B | 0-500B | 500B | 2KB |
| choice_question_options | 150B | 0-300B | 200B | 600B |
| code_question_data | 800B | 0-7KB | 3KB | 15KB |
| code_question_test_cases | 400B | 0-2KB | 800B | 4KB |

### **题型存储空间估算**

| 题型 | 包含表 | 平均存储 | 最大存储 |
|------|--------|----------|----------|
| 文本题 | 主表 + 评分点(平均3个) | 2KB | 10KB |
| 选择题 | 主表 + 选项(平均4个) | 1.5KB | 4KB |
| 编程题 | 主表 + 测试用例(平均5个) | 8KB | 35KB |

### **总体容量规划**

```
假设10万题目的存储需求：
├── 文本题（40%）：40,000 × 2KB = 80MB
├── 选择题（40%）：40,000 × 1.5KB = 60MB
├── 编程题（20%）：20,000 × 8KB = 160MB
├── 索引开销：约30% = 90MB
└── 总计：约390MB

预留增长空间：建议分配1GB存储空间
```

---

## 🔧 **数据库优化建议**

### **查询优化**
1. **索引优化**：根据查询模式优化索引设计
2. **分页查询**：大数据量使用LIMIT分页
3. **EXISTS查询**：权限控制使用EXISTS替代JOIN
4. **字段选择**：只查询需要的字段

### **性能监控**
```sql
-- 慢查询监控
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- 索引使用情况
SHOW INDEX FROM table_name;
EXPLAIN SELECT * FROM table_name WHERE condition;
```

### **维护策略**
1. **定期分析表**：`ANALYZE TABLE table_name`
2. **索引重建**：定期重建索引提升性能
3. **数据归档**：历史数据归档处理
4. **空间回收**：删除数据后空间回收

---

## 🛡️ **数据安全设计**

### **权限控制**
```sql
-- 应用用户权限
CREATE USER 'memorin_app'@'%' IDENTIFIED BY 'secure_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON memorin_knowledge.* TO 'memorin_app'@'%';

-- 只读用户权限
CREATE USER 'memorin_readonly'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON memorin_knowledge.* TO 'memorin_readonly'@'%';
```

### **数据备份策略**
1. **全量备份**：每日全量备份
2. **增量备份**：每小时增量备份
3. **备份验证**：定期恢复测试
4. **异地备份**：多地域备份存储

### **数据加密**
1. **传输加密**：SSL/TLS连接
2. **存储加密**：敏感字段加密存储
3. **备份加密**：备份文件加密

---

## 📋 **部署脚本**

### **表创建脚本**
```sql
-- 创建数据库
CREATE DATABASE memorin_knowledge DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE memorin_knowledge;

-- 创建所有表（按依赖顺序）
-- 1. 主表
-- 2. 关联表
-- 3. 索引
-- 4. 约束

-- 初始化数据
-- 插入测试数据
```

### **性能调优脚本**
```sql
-- MySQL配置优化
SET GLOBAL innodb_buffer_pool_size = 1G;
SET GLOBAL query_cache_size = 256M;
SET GLOBAL max_connections = 1000;

-- 慢查询配置
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL log_queries_not_using_indexes = 'ON';
```

---

*文档版本：v2.0*  
*最后更新：2025年8月15日*  
*下一版本：计划添加分区表和读写分离设计* 