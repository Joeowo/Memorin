# Nacos服务发现与负载均衡开发计划

> **项目**: Memorin智能知识复习系统  
> **阶段**: 微服务架构升级 - 服务发现  
> **技术**: Nacos + Spring Cloud Alibaba  
> **时间**: 2-3天开发周期  

## 🎯 **开发目标**

### 核心目标
- ✅ 实现微服务自动注册与发现
- ✅ 替换硬编码服务地址为动态发现
- ✅ 实现负载均衡和故障转移
- ✅ 集成配置管理中心
- ✅ 提供服务监控和健康检查

### 技术收益
- 🔄 **动态扩缩容**: 服务实例可以动态增减
- ⚖️ **负载均衡**: 自动分发请求到可用实例
- 🛡️ **故障恢复**: 自动检测和隔离故障服务
- 📊 **监控可视**: Nacos控制台实时监控
- 🔧 **配置统一**: 集中管理所有服务配置

## 📋 **技术方案**

### 技术栈选择
```yaml
Nacos版本: 2.3.2 (LTS)
  - 支持Java 8+
  - 稳定的服务发现功能
  - 完善的管理界面
  - 良好的Spring Cloud集成

Spring Cloud Alibaba: 2021.0.5.0
  - 兼容Spring Boot 2.7.x
  - 与现有技术栈匹配
  - 官方维护和支持

负载均衡策略:
  - Round Robin (轮询)
  - Random (随机)
  - Weighted Round Robin (加权轮询)
```

### 架构设计
```
┌─────────────────────────────────────────────────────────────┐
│                    Nacos服务器 (8848)                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │  服务注册   │ │  配置管理   │ │  服务监控   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │ 注册/发现
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关 (8080)                         │
│              ┌─服务发现─┐ ┌─负载均衡─┐                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       微服务集群                             │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐        │
│ │用户服务  │ │知识库服务│ │复习服务  │ │统计服务  │        │
│ │多实例    │ │多实例    │ │多实例    │ │多实例    │        │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘        │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 **分阶段实施计划**

### **Phase 1: Nacos服务器搭建** (预计1小时)

#### 任务清单
- [ ] 下载Nacos 2.3.2
- [ ] 配置standalone模式
- [ ] 启动Nacos服务器
- [ ] 验证管理界面访问
- [ ] 配置数据库存储(可选)

#### 关键步骤
```bash
# 1. 下载和解压Nacos
wget https://github.com/alibaba/nacos/releases/download/2.3.2/nacos-server-2.3.2.zip
unzip nacos-server-2.3.2.zip

# 2. 启动服务器
cd nacos/bin
startup.cmd -m standalone

# 3. 访问控制台
http://localhost:8848/nacos
用户名: nacos
密码: nacos
```

#### 验收标准
- ✅ Nacos服务器启动成功(端口8848)
- ✅ 管理界面可以正常访问
- ✅ 服务列表和配置管理功能正常

---

### **Phase 2: 依赖配置升级** (预计1小时)

#### 任务清单
- [ ] 更新父级pom.xml添加Spring Cloud Alibaba
- [ ] 各微服务添加Nacos Discovery依赖
- [ ] 配置服务名称和注册中心地址
- [ ] 移除硬编码服务地址配置

#### 核心依赖
```xml
<!-- 父级pom.xml -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>2021.0.5.0</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- 各微服务pom.xml -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

#### 配置文件修改
```yaml
# application.yml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: memorin-dev
        group: DEFAULT_GROUP
  application:
    name: memorin-user-service  # 每个服务不同
```

---

### **Phase 3: 服务注册实现** (预计2小时)

#### 任务清单
- [ ] 用户服务注册到Nacos
- [ ] 知识库服务注册到Nacos  
- [ ] 复习服务注册到Nacos
- [ ] 统计服务注册到Nacos
- [ ] 验证服务注册状态

#### 实现步骤
1. **启用服务发现**
```java
@SpringBootApplication
@EnableDiscoveryClient  // 添加注解
public class UserServiceApplication {
    // 主启动类
}
```

2. **配置健康检查**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

3. **配置服务元数据**
```yaml
spring:
  cloud:
    nacos:
      discovery:
        metadata:
          version: 1.0.0
          region: default
```

#### 验收标准
- ✅ 所有微服务在Nacos控制台可见
- ✅ 服务健康状态为UP
- ✅ 心跳检测正常

---

### **Phase 4: 网关集成服务发现** (预计2小时)

#### 任务清单
- [ ] 网关集成Nacos Discovery
- [ ] 移除硬编码URI配置
- [ ] 配置动态路由发现
- [ ] 实现负载均衡策略
- [ ] 测试路由动态更新

#### 核心实现
```java
// 网关配置修改
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 使用服务名替代固定URI
                .route("user-service", r -> r
                        .path("/api/user/**")
                        .uri("lb://memorin-user-service"))  // lb:// 表示负载均衡
                .route("statistics-service", r -> r
                        .path("/api/statistics/**")
                        .uri("lb://memorin-statistics-service"))
                .build();
    }
}
```

#### 负载均衡配置
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false  # 禁用Ribbon，使用Spring Cloud LoadBalancer
    nacos:
      discovery:
        load-balancer:
          # 负载均衡策略
          strategy: round-robin  # 轮询策略
```

---

### **Phase 5: 配置管理集成** (预计1小时)

#### 任务清单
- [ ] 集成Nacos Config
- [ ] 迁移配置到Nacos
- [ ] 实现配置动态刷新
- [ ] 配置环境隔离

#### 依赖添加
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

#### 配置文件结构
```
Nacos配置中心:
├── memorin-user-service-dev.yml       # 用户服务开发环境
├── memorin-knowledge-service-dev.yml  # 知识库服务开发环境
├── memorin-review-service-dev.yml     # 复习服务开发环境
├── memorin-statistics-service-dev.yml # 统计服务开发环境
├── memorin-gateway-dev.yml            # 网关服务开发环境
└── memorin-common-dev.yml             # 公共配置
```

---

### **Phase 6: 测试验证** (预计1小时)

#### 功能测试清单
- [ ] **服务注册测试**: 启动/停止服务，观察注册状态
- [ ] **负载均衡测试**: 多实例情况下请求分发
- [ ] **故障转移测试**: 停止服务实例，验证自动切换
- [ ] **配置动态更新**: 修改Nacos配置，验证实时生效
- [ ] **网关路由测试**: 通过服务名访问各微服务

#### 性能测试
```bash
# 并发测试
for i in {1..10}; do
  curl http://localhost:8080/api/statistics/overview/user$i?days=30 &
done
wait

# 负载测试
ab -n 100 -c 10 http://localhost:8080/api/statistics/overview/test?days=7
```

#### 监控验证
- 📊 Nacos控制台服务状态
- 📈 请求分发情况
- ⚡ 响应时间统计
- 🔍 错误率监控

---

## 🔧 **技术细节**

### 端口规划
```yaml
Nacos服务器: 8848
API网关: 8080
用户服务: 8081, 8091, 8101 (多实例)
知识库服务: 8082, 8092, 8102 (多实例)
复习服务: 8083, 8093, 8103 (多实例)
统计服务: 8084, 8094, 8104 (多实例)
```

### 配置管理策略
```yaml
命名空间: memorin-dev (开发环境)
分组: DEFAULT_GROUP
配置格式: YAML
配置ID格式: ${spring.application.name}-${profiles.active}.${file-extension}
```

### 负载均衡算法
1. **Round Robin (轮询)**: 默认策略，平均分配
2. **Random (随机)**: 随机选择可用实例  
3. **Weighted (加权)**: 根据实例权重分配
4. **Least Connections**: 选择连接数最少的实例

### 故障处理机制
- **健康检查**: 30秒心跳间隔
- **超时设置**: 5秒连接超时，30秒读取超时
- **重试策略**: 最多重试3次，间隔1秒
- **熔断保护**: 连续失败5次触发熔断

---

## 📊 **预期收益**

### 运维收益
- 🚀 **部署效率**: 新实例自动注册，无需手动配置
- 📈 **扩容便捷**: 动态增减实例，实时生效
- 🔍 **监控透明**: 统一监控所有服务状态
- 🛡️ **故障隔离**: 自动隔离异常实例

### 开发收益  
- 🔧 **配置统一**: 集中管理，避免配置分散
- 🔄 **热更新**: 配置修改实时生效，无需重启
- 🧪 **环境隔离**: 开发/测试/生产环境完全隔离
- 📝 **版本管理**: 配置版本控制和回滚

### 性能收益
- ⚖️ **负载均衡**: 合理分配请求，提升吞吐量
- 🏃 **就近访问**: 智能路由到最优实例
- 💾 **缓存优化**: 服务发现结果本地缓存
- 📊 **监控指标**: 实时性能数据收集

---

## 🎯 **成功标准**

### 功能验收
- ✅ 所有微服务成功注册到Nacos
- ✅ 网关通过服务发现路由请求
- ✅ 负载均衡算法正常工作
- ✅ 配置动态更新功能正常
- ✅ 故障转移机制有效

### 性能指标
- 📈 **服务发现延迟**: < 100ms
- ⚡ **负载均衡开销**: < 10ms
- 🔄 **配置更新延迟**: < 30s
- 💾 **内存占用增加**: < 50MB
- 📊 **CPU开销增加**: < 5%

### 可用性目标
- 🛡️ **服务可用性**: ≥ 99.9%
- 🔄 **故障恢复时间**: ≤ 30s
- 📈 **并发处理能力**: ≥ 1000 QPS
- 🎯 **负载均衡准确性**: ≥ 95%

---

**🎊 这个开发计划将为Memorin系统带来企业级的服务治理能力，为后续的微服务扩展奠定坚实基础！** 