# Redis缓存管理使用指南

> **适用于**: Memorin智能知识复习系统  
> **Redis版本**: 7.0+  
> **更新时间**: 2025-01-08

## 📋 快速开始

### 1. 安装和配置

#### 安装Redis
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# 或使用Docker
docker run -d --name redis-memorin \
  -p 6379:6379 \
  -v /data/redis:/data \
  redis:7.0-alpine redis-server --appendonly yes
```

#### 配置Redis
```bash
# 复制优化配置文件
sudo cp Redis配置文件-生产环境优化.conf /etc/redis/redis.conf

# 修改关键配置
sudo nano /etc/redis/redis.conf

# 重启Redis服务
sudo systemctl restart redis-server
sudo systemctl enable redis-server
```

### 2. 环境变量配置

```bash
# 创建环境配置文件
cat > /etc/memorin/cache.env << EOF
# Redis配置
REDIS_MASTER_HOST=localhost
REDIS_MASTER_PORT=6379
REDIS_MASTER_PASSWORD=your_redis_password

REDIS_SLAVE_HOST=localhost
REDIS_SLAVE_PORT=6379
REDIS_SLAVE_PASSWORD=your_redis_password

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_USER=memorin_user
DB_PASSWORD=your_db_password
DB_NAME=memorin

# 缓存配置
CACHE_WARMUP_KB_COUNT=20
CACHE_WARMUP_CONTENT_COUNT=50
CACHE_CLEANUP_THRESHOLD=5
EOF

# 加载环境变量
source /etc/memorin/cache.env
```

## 🚀 缓存管理操作

### 1. 缓存预热

#### 系统启动预热
```bash
# 执行完整预热
python3 /path/to/redis_cache_management.py --warmup

# 预热特定知识库 (通过Redis CLI)
redis-cli EVAL "
local kb_id = ARGV[1]
local kb_info = redis.call('HGETALL', 'kb:info:' .. kb_id)
if #kb_info == 0 then
    return 'Knowledge base not found'
end
return 'Warmed up knowledge base ' .. kb_id
" 0 123
```

#### 定制预热脚本
```python
# warmup_custom.py
from redis_cache_management import RedisCacheManager, load_config

def warmup_specific_user(user_id):
    """预热特定用户数据"""
    config = load_config()
    manager = RedisCacheManager(config)
    
    try:
        # 预热用户基本信息
        manager._preload_user_session_data(user_id)
        
        # 预热用户复习队列
        today = datetime.now().strftime('%Y-%m-%d')
        manager.redis_master.delete(f"user:review:queue:{user_id}:{today}")
        
        print(f"用户 {user_id} 预热完成")
        
    finally:
        manager.close()

# 使用示例
warmup_specific_user(123)
```

### 2. 缓存清理

#### 自动清理
```bash
# 执行清理过期缓存
python3 /path/to/redis_cache_management.py --cleanup

# 设置定时清理 (添加到crontab)
# 每小时清理一次
0 * * * * /usr/bin/python3 /path/to/redis_cache_management.py --cleanup

# 每天深度清理一次
0 2 * * * redis-cli FLUSHEXPIRED
```

#### 手动清理特定缓存
```bash
# 清理用户会话缓存
redis-cli --scan --pattern "user:online:*" | xargs redis-cli DEL

# 清理过期统计缓存
redis-cli --scan --pattern "stats:*:daily:2025-01-01" | xargs redis-cli DEL

# 清理低访问量内容
redis-cli EVAL "
local keys = redis.call('SCAN', 0, 'MATCH', 'access:count:kp:*', 'COUNT', 1000)
local deleted = 0
for i = 2, #keys[2] do
    local count = redis.call('GET', keys[2][i])
    if count and tonumber(count) < 5 then
        local content_id = string.match(keys[2][i], 'access:count:kp:(.+)')
        redis.call('DEL', 'kp:content:' .. content_id)
        redis.call('DEL', 'kp:choices:' .. content_id)
        deleted = deleted + 1
    end
end
return deleted
" 0
```

### 3. 监控和指标

#### 实时监控
```bash
# 显示缓存指标
python3 /path/to/redis_cache_management.py --monitor

# 监控关键指标
redis-cli INFO memory | grep -E "(used_memory|maxmemory|mem_fragmentation)"
redis-cli INFO stats | grep -E "(keyspace_hits|keyspace_misses|evicted_keys)"

# 实时监控命令
redis-cli --latency -i 1
redis-cli --bigkeys
```

#### 性能分析
```bash
# 慢查询日志
redis-cli SLOWLOG GET 10

# 内存使用分析
redis-cli MEMORY USAGE "kb:info:1"
redis-cli MEMORY STATS

# 连接信息
redis-cli CLIENT LIST
redis-cli INFO clients
```

#### 监控脚本示例
```bash
#!/bin/bash
# monitor_cache.sh

echo "=== Redis缓存监控报告 $(date) ==="

# 基础信息
redis-cli INFO server | grep redis_version
redis-cli INFO memory | grep used_memory_human
redis-cli INFO stats | grep instantaneous_ops_per_sec

# 命中率
HITS=$(redis-cli INFO stats | grep keyspace_hits | cut -d: -f2 | tr -d '\r')
MISSES=$(redis-cli INFO stats | grep keyspace_misses | cut -d: -f2 | tr -d '\r')
TOTAL=$((HITS + MISSES))
if [ $TOTAL -gt 0 ]; then
    HIT_RATE=$(echo "scale=2; $HITS * 100 / $TOTAL" | bc)
    echo "缓存命中率: ${HIT_RATE}%"
fi

# 缓存层统计
echo "热点数据缓存: $(redis-cli ZCARD hotspot:kp:daily)"
echo "在线用户数: $(redis-cli --scan --pattern 'user:online:*' | wc -l)"
echo "内容缓存数: $(redis-cli --scan --pattern 'kp:content:*' | wc -l)"

echo "========================="
```

### 4. 一致性检查

#### 定期检查
```bash
# 执行一致性检查
python3 /path/to/redis_cache_management.py --check

# 检查特定数据类型
redis-cli EVAL "
local kb_keys = redis.call('SCAN', 0, 'MATCH', 'kb:info:*', 'COUNT', 100)
local inconsistent = {}
for i = 2, #kb_keys[2] do
    local kb_id = string.match(kb_keys[2][i], 'kb:info:(.+)')
    local cached_name = redis.call('HGET', kb_keys[2][i], 'name')
    -- 这里需要与数据库比较，简化示例
    if not cached_name then
        table.insert(inconsistent, kb_id)
    end
end
return inconsistent
" 0
```

#### 修复不一致数据
```python
# repair_inconsistency.py
def repair_knowledge_base_cache(kb_id):
    """修复知识库缓存"""
    # 从数据库重新加载
    cursor = db.cursor()
    cursor.execute("SELECT * FROM knowledge_bases WHERE id = %s", [kb_id])
    kb_data = cursor.fetchone()
    
    if kb_data:
        # 更新缓存
        cache_data = {
            'id': str(kb_data['id']),
            'name': kb_data['name'],
            'owner_id': str(kb_data['owner_id']),
            # ... 其他字段
        }
        redis.hset(f"kb:info:{kb_id}", mapping=cache_data)
        redis.expire(f"kb:info:{kb_id}", 604800)
        
        print(f"修复知识库 {kb_id} 缓存完成")
```

## 🔧 高级功能

### 1. 热点数据管理

#### 查看热点排行
```bash
# 查看热点知识点
redis-cli ZREVRANGE hotspot:kp:daily 0 10 WITHSCORES

# 查看热点知识库
redis-cli ZREVRANGE hotspot:kb:weekly 0 10 WITHSCORES

# 手动添加热点数据
redis-cli ZADD hotspot:kp:daily 1000 content_123
```

#### 热点阈值调整
```bash
# 查看当前阈值
redis-cli GET hotspot:threshold

# 调整阈值
redis-cli SETEX hotspot:threshold 3600 150

# 重新计算热点排行
redis-cli EVAL "
local threshold = tonumber(redis.call('GET', 'hotspot:threshold') or 100)
local access_keys = redis.call('SCAN', 0, 'MATCH', 'access:count:kp:*', 'COUNT', 1000)
for i = 2, #access_keys[2] do
    local count = tonumber(redis.call('GET', access_keys[2][i]) or 0)
    if count >= threshold then
        local content_id = string.match(access_keys[2][i], 'access:count:kp:(.+)')
        redis.call('ZADD', 'hotspot:kp:daily', count, content_id)
    end
end
return 'Hotspot ranking updated'
" 0
```

### 2. 智能预加载

#### 基于用户行为预加载
```python
def preload_based_on_behavior(user_id):
    """基于用户行为预加载"""
    # 获取用户最近行为
    behaviors = redis.lrange(f"user:behavior:{user_id}", 0, 10)
    
    # 分析行为模式
    knowledge_bases = set()
    areas = set()
    
    for behavior in behaviors:
        behavior_str = behavior.decode()
        if behavior_str.startswith('view:kb:'):
            kb_id = behavior_str.split(':')[2]
            knowledge_bases.add(kb_id)
        elif behavior_str.startswith('review:area:'):
            area_id = behavior_str.split(':')[2]
            areas.add(area_id)
    
    # 预加载相关数据
    for kb_id in knowledge_bases:
        preload_knowledge_base_detail(kb_id)
    
    for area_id in areas:
        preload_area_content(area_id)
```

### 3. 缓存性能优化

#### 批量操作优化
```python
def batch_cache_operations(operations):
    """批量缓存操作"""
    pipeline = redis.pipeline()
    
    for op in operations:
        if op['type'] == 'set':
            pipeline.setex(op['key'], op['ttl'], op['value'])
        elif op['type'] == 'hset':
            pipeline.hset(op['key'], mapping=op['data'])
            pipeline.expire(op['key'], op['ttl'])
        elif op['type'] == 'delete':
            pipeline.delete(op['key'])
    
    results = pipeline.execute()
    return results
```

#### 内存优化策略
```bash
# 启用内存压缩
redis-cli CONFIG SET hash-max-ziplist-entries 512
redis-cli CONFIG SET hash-max-ziplist-value 64

# 内存碎片整理
redis-cli MEMORY PURGE

# 查看内存使用详情
redis-cli MEMORY DOCTOR
```

## 📊 监控和告警

### 1. Prometheus监控集成

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'redis-memorin'
    static_configs:
      - targets: ['localhost:9121']
    metrics_path: /metrics
    scrape_interval: 30s
```

### 2. 告警规则

```yaml
# redis_alerts.yml
groups:
  - name: redis-memorin
    rules:
      - alert: RedisHighMemoryUsage
        expr: redis_memory_usage_bytes / redis_maxmemory_bytes > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Redis内存使用率过高"
          
      - alert: RedisCacheHitRateLow
        expr: redis_keyspace_hits / (redis_keyspace_hits + redis_keyspace_misses) < 0.8
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Redis缓存命中率过低"
```

### 3. 健康检查脚本

```bash
#!/bin/bash
# health_check.sh

REDIS_HOST=${REDIS_HOST:-localhost}
REDIS_PORT=${REDIS_PORT:-6379}

# 连接测试
if redis-cli -h $REDIS_HOST -p $REDIS_PORT ping | grep -q PONG; then
    echo "✅ Redis连接正常"
else
    echo "❌ Redis连接失败"
    exit 1
fi

# 内存检查
MEMORY_USAGE=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO memory | grep used_memory_percentage | cut -d: -f2 | tr -d '\r')
if (( $(echo "$MEMORY_USAGE > 90" | bc -l) )); then
    echo "⚠️  内存使用率过高: ${MEMORY_USAGE}%"
fi

# 命中率检查
HITS=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO stats | grep keyspace_hits | cut -d: -f2 | tr -d '\r')
MISSES=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO stats | grep keyspace_misses | cut -d: -f2 | tr -d '\r')
TOTAL=$((HITS + MISSES))

if [ $TOTAL -gt 0 ]; then
    HIT_RATE=$(echo "scale=2; $HITS * 100 / $TOTAL" | bc)
    if (( $(echo "$HIT_RATE < 80" | bc -l) )); then
        echo "⚠️  缓存命中率过低: ${HIT_RATE}%"
    else
        echo "✅ 缓存命中率正常: ${HIT_RATE}%"
    fi
fi

echo "✅ 健康检查完成"
```

## 🛠️ 故障排除

### 常见问题和解决方案

#### 1. 内存使用过高
```bash
# 查看内存使用详情
redis-cli INFO memory

# 查看大键
redis-cli --bigkeys

# 清理过期键
redis-cli --scan --pattern "*" | xargs -I {} redis-cli TTL {} | grep -B1 "^-1$"

# 手动执行内存清理
python3 /path/to/redis_cache_management.py --cleanup
```

#### 2. 缓存命中率低
```bash
# 分析访问模式
redis-cli MONITOR | head -100

# 检查热点数据配置
redis-cli GET hotspot:threshold

# 执行缓存预热
python3 /path/to/redis_cache_management.py --warmup
```

#### 3. 连接数过多
```bash
# 查看当前连接
redis-cli CLIENT LIST

# 关闭空闲连接
redis-cli CLIENT KILL TYPE normal SKIPME yes

# 调整连接池配置
redis-cli CONFIG SET maxclients 5000
```

#### 4. 性能问题
```bash
# 查看慢查询
redis-cli SLOWLOG GET 20

# 监控延迟
redis-cli --latency

# 检查配置
redis-cli CONFIG GET "*"
```

## 📚 最佳实践

### 1. 数据模型设计
- 使用合适的数据结构（Hash、List、Set、ZSet）
- 避免大键，单个键值不超过10MB
- 设置合理的TTL，避免内存泄漏
- 使用命名空间组织键名

### 2. 性能优化
- 使用Pipeline批量操作
- 避免在生产环境使用KEYS命令
- 使用连接池管理连接
- 启用压缩减少内存使用

### 3. 安全措施
- 启用认证和ACL
- 使用TLS加密传输
- 重命名危险命令
- 定期备份数据

### 4. 监控运维
- 设置完善的监控和告警
- 定期检查缓存一致性
- 建立缓存预热机制
- 制定故障恢复计划

---

**注意事项**:
1. 定期备份Redis数据和配置
2. 监控内存使用和性能指标
3. 根据业务负载调整配置参数
4. 保持Redis版本更新
5. 建立完善的监控和告警体系 