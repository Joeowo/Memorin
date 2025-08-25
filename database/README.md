# Memorin 数据库模块

> **版本**: v2.0  
> **更新时间**: 2025-01-08  
> **架构**: 模块化设计，核心边缘分离  

## 📋 目录概述

本目录包含Memorin智能知识复习系统的完整数据库解决方案，采用模块化设计理念，实现数据存储、完整性验证、迁移策略的全方位覆盖。

## 📁 目录结构

```
database/
├── docs/                                          # 📚 数据库设计文档
│   ├── 数据库设计-多用户共享知识库与个人复习状态分离.md      # 核心设计文档
│   ├── 数据库设计对比-旧vs新模型分析.md                  # 新旧模型对比
│   ├── 数据库设计-自定义题目列表多对多关系.md             # 题目列表设计
│   ├── 数据库视图-自定义题目列表管理.md                  # 视图设计
│   ├── 数据完整性验证和一致性检查系统.md                 # 完整性验证系统
│   ├── 数据迁移策略-LocalStorage到MySQL.md           # 迁移策略文档
│   ├── Redis缓存策略设计-热点数据与性能优化.md           # Redis缓存架构设计
│   ├── Redis配置文件-生产环境优化.conf                 # Redis生产环境配置
│   └── Redis缓存管理使用指南.md                       # 缓存管理操作指南
├── migrations/                                    # 🔧 数据库迁移脚本
│   ├── 数据库初始化脚本-模块化设计.sql                   # 数据表结构定义
│   ├── 003_data_integrity_validation.sql           # 数据完整性验证机制
│   └── 004_redis_cache_management.py               # Redis缓存管理工具
├── seeds/                                         # 🌱 种子数据脚本
│   └── 数据库种子数据-seed_data.sql                   # 示例和初始数据
└── README.md                                     # 📖 本文档
```

## 🎯 核心设计特点

### 1. 模块化架构
- **域分离原则**: 每个表的域尽可能小，职责单一
- **核心边缘分离**: 核心属性与扩展属性分表存储
- **业务解耦**: 不同业务模块数据隔离，便于维护

### 2. 多用户支持
- **共享知识库**: 支持官方和用户创建的公共知识库
- **个人复习状态**: 每个用户独立的学习进度和复习数据
- **权限控制**: 基于角色的访问控制机制

### 3. 数据完整性保障
- **约束完整性**: 外键、唯一约束、检查约束
- **业务逻辑一致性**: 复习数据逻辑验证
- **自动修复机制**: 检测和修复数据不一致问题

## 🗃️ 数据库表结构概览

### 用户模块 (4个表)
- `users` - 用户核心信息
- `user_profiles` - 用户扩展信息
- `user_tokens` - 认证令牌管理
- `user_settings` - 用户个性化设置

### 知识库模块 (4个表)
- `knowledge_bases` + `knowledge_base_metadata` - 知识库核心与元数据
- `knowledge_areas` + `knowledge_area_metadata` - 知识区核心与元数据

### 知识内容模块 (3个表)
- `knowledge_point_contents` - 知识点内容核心
- `knowledge_point_metadata` - 知识点元数据
- `knowledge_point_choice_options` - 选择题选项

### 学习状态模块 (3个表)
- `user_knowledge_point_states` - 用户学习状态
- `user_knowledge_point_sm2_data` - SM-2算法数据
- `user_knowledge_point_performance` - 学习表现数据

### 关系模块 (2个表)
- `knowledge_base_content_relations` - 知识库-内容关联
- `knowledge_area_content_relations` - 知识区-内容关联

### 题目列表模块 (3个表)
- `custom_question_lists` + `question_list_metadata` - 自定义题目列表
- `question_list_point_relations` - 题目列表关联关系

### 复习队列模块 (2个表)
- `user_review_queues` - 个人复习队列
- `review_queue_metadata` - 复习队列元数据

### 订阅模块 (2个表)
- `user_knowledge_base_subscriptions` - 知识库订阅
- `subscription_settings` - 订阅个性化设置

## 🚀 快速开始

### 1. 初始化数据库

```bash
# 1. 创建数据库并执行结构脚本
mysql -u root -p < migrations/数据库初始化脚本-模块化设计.sql

# 2. 安装数据完整性验证系统
mysql -u root -p memorin_db < migrations/003_data_integrity_validation.sql

# 3. 导入种子数据
mysql -u root -p memorin_db < seeds/数据库种子数据-seed_data.sql
```

### 2. 验证安装

```sql
-- 检查表结构
SHOW TABLES;

-- 执行健康检查
CALL PerformDataHealthCheck();

-- 查看种子数据统计
SELECT 
    (SELECT COUNT(*) FROM users) as total_users,
    (SELECT COUNT(*) FROM knowledge_bases) as total_knowledge_bases,
    (SELECT COUNT(*) FROM knowledge_point_contents) as total_contents;
```

### 3. 配置Redis缓存

```bash
# 1. 安装Redis
sudo apt update && sudo apt install redis-server

# 2. 复制优化配置
sudo cp docs/Redis配置文件-生产环境优化.conf /etc/redis/redis.conf

# 3. 设置环境变量
export REDIS_MASTER_HOST=localhost
export REDIS_MASTER_PORT=6379
export REDIS_MASTER_PASSWORD=your_redis_password

# 4. 启动Redis服务
sudo systemctl restart redis-server
sudo systemctl enable redis-server

# 5. 执行缓存预热
python3 migrations/004_redis_cache_management.py --warmup
```

### 4. 数据迁移（可选）

如果需要从LocalStorage迁移数据：

```bash
# 1. 在原系统导出数据
# 在浏览器控制台执行：exportLocalStorageData()

# 2. 转换数据格式
node migration_tools/data_transformer.js exported_data.json

# 3. 导入转换后的数据
mysql -u root -p memorin_db < exported_data_migration.sql
```

## 📊 数据完整性管理

### 日常检查命令

```sql
-- 1. 外键完整性检查
CALL CheckForeignKeyIntegrity();

-- 2. 统计数据一致性检查
CALL CheckStatisticsConsistency();

-- 3. 业务逻辑一致性检查
CALL CheckBusinessLogicConsistency();

-- 4. 数据质量检查
CALL CheckDataQuality();

-- 5. 综合健康检查
CALL PerformDataHealthCheck();
```

### 维护任务

```sql
-- 每日维护任务
CALL DailyMaintenanceTasks();

-- 每周维护任务
CALL WeeklyMaintenanceTasks();

-- 手动修复统计数据
CALL RepairStatisticsInconsistency();

-- 清理孤立数据
CALL CleanOrphanedData();
```

### 监控视图

```sql
-- 查看数据完整性状态
SELECT * FROM v_data_integrity_monitor;

-- 查看系统健康状态
SELECT * FROM v_system_health_status;

-- 查看知识库统计
SELECT * FROM v_knowledge_base_stats;

-- 查看用户学习状态概览
SELECT * FROM v_user_knowledge_points_full LIMIT 10;
```

## 🔧 开发指南

### 1. 添加新表的最佳实践

```sql
-- 示例：添加新的业务表
CREATE TABLE new_business_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 核心字段
    core_field VARCHAR(100) NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    
    -- 关联字段
    user_id BIGINT NOT NULL,
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user_status (user_id, status),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='新业务表';

-- 对应的元数据表
CREATE TABLE new_business_table_metadata (
    business_id BIGINT PRIMARY KEY,
    -- 扩展字段
    extended_field TEXT,
    metadata_json JSON,
    
    FOREIGN KEY (business_id) REFERENCES new_business_table(id) ON DELETE CASCADE
) COMMENT='新业务表元数据';
```

### 2. 数据模型设计原则

1. **表命名规范**
   - 使用复数形式：`users`、`knowledge_bases`
   - 关联表使用下划线连接：`user_knowledge_point_states`
   - 元数据表添加`_metadata`后缀

2. **字段设计原则**
   - 主键统一使用`BIGINT AUTO_INCREMENT`
   - 必要的时间戳字段：`created_at`、`updated_at`
   - 状态字段使用ENUM类型
   - JSON字段用于灵活的配置数据

3. **索引策略**
   - 外键字段建立索引
   - 常用查询条件建立复合索引
   - 避免过多索引影响写入性能

### 3. 性能优化建议

1. **查询优化**
   - 优先使用视图进行复杂查询
   - 合理使用分页避免大结果集
   - 利用索引覆盖避免回表查询

2. **写入优化**
   - 批量操作使用事务
   - 合理设置批次大小
   - 避免长事务锁定资源

3. **存储优化**
   - 定期清理过期数据
   - 归档历史数据
   - 监控表空间使用情况

## 🔍 故障排查

### 常见问题及解决方案

1. **外键约束错误**
   ```sql
   -- 查找违反外键约束的数据
   CALL CheckForeignKeyIntegrity();
   
   -- 清理孤立数据
   CALL CleanOrphanedData();
   ```

2. **统计数据不一致**
   ```sql
   -- 检查不一致情况
   CALL CheckStatisticsConsistency();
   
   -- 自动修复
   CALL RepairStatisticsInconsistency();
   ```

3. **性能问题**
   ```sql
   -- 检查慢查询
   SHOW PROCESSLIST;
   
   -- 分析表状态
   ANALYZE TABLE table_name;
   
   -- 优化表
   OPTIMIZE TABLE table_name;
   ```

### 日志分析

```bash
# MySQL错误日志
tail -f /var/log/mysql/error.log

# 慢查询日志
tail -f /var/log/mysql/slow.log

# 查询性能分析
EXPLAIN SELECT * FROM complex_query;
```

## 🚀 Redis缓存管理

### 缓存层架构
- **L1 热点缓存**: 高频访问数据，TTL: 1小时
- **L2 会话缓存**: 用户认证信息，TTL: 24小时
- **L3 内容缓存**: 知识库内容，TTL: 7天
- **L4 统计缓存**: 聚合统计数据，TTL: 5分钟

### 日常管理命令

```bash
# 缓存预热
python3 migrations/004_redis_cache_management.py --warmup

# 缓存清理
python3 migrations/004_redis_cache_management.py --cleanup

# 监控指标
python3 migrations/004_redis_cache_management.py --monitor

# 一致性检查
python3 migrations/004_redis_cache_management.py --check
```

### 性能监控

```bash
# 查看缓存命中率
redis-cli INFO stats | grep -E "(keyspace_hits|keyspace_misses)"

# 监控内存使用
redis-cli INFO memory | grep used_memory_human

# 查看热点数据
redis-cli ZREVRANGE hotspot:kp:daily 0 10 WITHSCORES
```

### 故障排除

```bash
# 内存使用过高
redis-cli --bigkeys
python3 migrations/004_redis_cache_management.py --cleanup

# 缓存命中率低
python3 migrations/004_redis_cache_management.py --warmup

# 连接问题
redis-cli ping
redis-cli CLIENT LIST
```

## 📚 相关文档

- [数据库设计-多用户共享知识库与个人复习状态分离](./docs/数据库设计-多用户共享知识库与个人复习状态分离.md)
- [数据完整性验证和一致性检查系统](./docs/数据完整性验证和一致性检查系统.md)
- [数据迁移策略-LocalStorage到MySQL](./docs/数据迁移策略-LocalStorage到MySQL.md)
- [Redis缓存策略设计-热点数据与性能优化](./docs/Redis缓存策略设计-热点数据与性能优化.md)
- [Redis缓存管理使用指南](./docs/Redis缓存管理使用指南.md)
- [系统部署文档](../deploy/)
- [API开发文档](../backend/)

## 🤝 贡献指南

1. **添加新功能**
   - 先在docs目录添加设计文档
   - 在migrations目录添加SQL脚本
   - 更新seeds目录的测试数据
   - 添加相应的完整性检查

2. **修改现有表结构**
   - 创建新的migration文件
   - 确保向后兼容性
   - 更新相关文档
   - 测试数据迁移脚本

3. **性能优化**
   - 分析现有性能瓶颈
   - 提供基准测试数据
   - 验证优化效果
   - 更新最佳实践文档

---

**重要提醒**: 
- 生产环境操作前务必备份数据
- 定期执行健康检查和维护任务
- 关注系统性能和资源使用情况
- 及时更新文档保持同步 