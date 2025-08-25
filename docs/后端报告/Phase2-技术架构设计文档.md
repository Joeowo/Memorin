# Phase 2 - 技术架构设计文档

## 📋 **文档信息**

| 项目信息 | 详情 |
|---------|------|
| **文档标题** | Phase 2 技术架构设计文档 |
| **创建时间** | 2025年8月15日 |
| **版本** | v2.0 |
| **适用范围** | 题型数据管理模块 |

## 🎯 **架构设计目标**

### **核心原则**
1. **分层解耦**：清晰的职责分离，降低模块间耦合度
2. **统一标准**：三个题型模块采用相同的架构模式
3. **扩展性**：易于添加新题型和功能扩展
4. **可维护性**：代码结构清晰，便于理解和维护
5. **性能优化**：合理的查询策略和缓存机制

## 🏗️ **五层架构详细设计**

### **1. Controller层 - API接口层**

#### **职责**
- RESTful API接口定义
- HTTP请求/响应处理
- JWT认证集成
- 参数验证和异常处理

#### **设计模式**
```java
@RestController
@RequestMapping("/api/{resource}")
@CrossOrigin(origins = "*")
public class {Resource}Controller {
    
    @Autowired
    private {Resource}Service service;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    // CRUD操作
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRequest request, 
                                   HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        // 业务逻辑调用
        return ResponseEntity.ok(service.create(request, userId));
    }
    
    // 统一错误处理
    private String getUserIdFromRequest(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
        return jwtTokenUtil.getUserIdFromToken(token);
    }
}
```

#### **双控制器设计**
- **正式控制器**：集成JWT认证，用于生产环境
- **测试控制器**：硬编码用户ID，用于开发调试

### **2. DTO层 - 数据传输对象层**

#### **职责**
- 数据传输格式定义
- 请求/响应数据验证
- 业务规则验证
- 数据转换逻辑

#### **Request DTO设计**
```java
public class Create{Resource}Request {
    
    // 基础字段验证
    @NotBlank(message = "字段不能为空")
    @Size(max = 200, message = "长度不能超过200个字符")
    private String field;
    
    // 嵌套对象集合
    @Valid
    @Size(min = 1, max = 20, message = "数量必须在1-20个之间")
    private List<NestedRequest> nestedObjects;
    
    // 业务规则验证方法
    public boolean isValidConfiguration() {
        // 自定义业务验证逻辑
        return validateBusinessRules();
    }
}
```

#### **Response DTO设计**
```java
public class {Resource}Response {
    
    // 实体数据字段
    private String id;
    private String title;
    
    // 计算属性
    private int totalCount;
    private boolean hasComplexLogic;
    private String displayName;
    
    // 静态转换方法
    public static {Resource}Response fromEntity({Resource}Entity entity) {
        {Resource}Response response = new {Resource}Response();
        // 字段映射
        response.setId(entity.getId());
        // 计算属性
        response.setTotalCount(entity.calculateTotal());
        return response;
    }
}
```

### **3. Service层 - 业务逻辑层**

#### **职责**
- 核心业务逻辑实现
- 事务管理
- 数据验证
- ID生成策略
- 权限控制

#### **设计模式**
```java
@Service
@Transactional
public class {Resource}Service {
    
    @Autowired
    private {Resource}Repository repository;
    
    @Autowired
    private BaseKnowledgeRepository baseKnowledgeRepository;
    
    // CRUD操作
    public {Resource}Response create(Create{Resource}Request request, String userId) {
        // 1. 数据验证
        validateRequest(request);
        
        // 2. 权限验证
        validatePermissions(request.getKnowledgeId(), userId);
        
        // 3. 业务逻辑
        {Resource}Entity entity = new {Resource}Entity();
        entity.setId(generateId());
        // 字段设置
        
        // 4. 数据持久化
        {Resource}Entity savedEntity = repository.save(entity);
        
        // 5. 响应转换
        return {Resource}Response.fromEntity(savedEntity);
    }
    
    // ID生成策略
    private synchronized String generateId() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long todayCount = getTodayCount();
        String sequence = String.format("%03d", todayCount + 1);
        return "PREFIX_" + today + "_" + sequence;
    }
}
```

#### **事务管理策略**
- **@Transactional**：方法级事务控制
- **readOnly = true**：只读查询优化
- **rollbackFor**：异常回滚策略

### **4. Repository层 - 数据访问层**

#### **职责**
- 数据访问接口定义
- 自定义查询方法
- 权限控制查询
- 性能优化查询

#### **设计模式**
```java
@Repository
public interface {Resource}Repository extends JpaRepository<{Resource}Entity, String> {
    
    // 基础权限控制查询
    @Query("SELECT r FROM {Resource}Entity r WHERE r.id = :id AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = r.knowledgeId " +
           "AND bk.userId = :userId AND bk.isActive = true)")
    Optional<{Resource}Entity> findByIdAndUserId(@Param("id") String id, 
                                                 @Param("userId") String userId);
    
    // 分页查询
    @Query("SELECT r FROM {Resource}Entity r WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = r.knowledgeId " +
           "AND bk.userId = :userId AND bk.isActive = true)")
    Page<{Resource}Entity> findByUserIdWithPagination(@Param("userId") String userId, 
                                                      Pageable pageable);
    
    // 统计查询
    @Query("SELECT COUNT(r) FROM {Resource}Entity r WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = r.knowledgeId " +
           "AND bk.userId = :userId AND bk.isActive = true)")
    long countByUserId(@Param("userId") String userId);
    
    // 条件查询
    @Query("SELECT r FROM {Resource}Entity r WHERE r.field = :value AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = r.knowledgeId " +
           "AND bk.userId = :userId AND bk.isActive = true)")
    List<{Resource}Entity> findByFieldAndUserId(@Param("value") String value,
                                               @Param("userId") String userId);
}
```

#### **查询策略设计**
- **EXISTS子查询**：替代JOIN提升性能
- **用户权限控制**：所有查询都包含用户隔离
- **分页支持**：大数据量的分页处理
- **索引优化**：关键字段的索引配置

### **5. Entity层 - 数据模型层**

#### **职责**
- 数据模型定义
- JPA注解配置
- 嵌套实体管理
- 生命周期回调

#### **主实体设计**
```java
@Entity
@Table(name = "{resource}_data", indexes = {
    @Index(name = "idx_{resource}_knowledge_id", columnList = "knowledge_id"),
    @Index(name = "idx_{resource}_user_created", columnList = "created_at"),
    @Index(name = "idx_{resource}_type", columnList = "type")
})
public class {Resource}Entity {
    
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    
    @NotBlank(message = "关联知识点ID不能为空")
    @Column(name = "knowledge_id", length = 50, nullable = false)
    private String knowledgeId;
    
    // 嵌套实体集合
    @Valid
    @Size(min = 1, max = 20, message = "数量必须在1-20个之间")
    @ElementCollection
    @CollectionTable(name = "{resource}_nested_data",
                    joinColumns = @JoinColumn(name = "{resource}_id"))
    @OrderBy("sortOrder ASC")
    private List<NestedEntity> nestedEntities = new ArrayList<>();
    
    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
        updatedAt = System.currentTimeMillis();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
    
    // 业务方法
    public int calculateTotal() {
        return nestedEntities.stream()
                .mapToInt(NestedEntity::getValue)
                .sum();
    }
}
```

#### **嵌套实体设计**
```java
@Embeddable
public class NestedEntity {
    
    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @NotNull(message = "值不能为空")
    @Column(name = "value", nullable = false)
    private Integer value;
    
    @NotNull(message = "排序顺序不能为空")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    
    // 构造方法、getter/setter
}
```

## 🔧 **关键技术实现**

### **1. 统一ID生成策略**

#### **设计目标**
- 全局唯一性
- 时间有序性
- 业务可读性
- 并发安全性

#### **实现方案**
```java
public class IdGenerator {
    
    private static final Map<String, AtomicLong> DAILY_COUNTERS = new ConcurrentHashMap<>();
    
    public synchronized String generateId(String prefix) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = prefix + "_" + today;
        
        AtomicLong counter = DAILY_COUNTERS.computeIfAbsent(key, k -> new AtomicLong(0));
        long sequence = counter.incrementAndGet();
        
        return prefix + "_" + today + "_" + String.format("%03d", sequence);
    }
}
```

### **2. 权限控制机制**

#### **设计原则**
- 数据隔离：用户只能访问自己的数据
- 层级控制：Repository层统一实现
- 性能优化：EXISTS子查询替代JOIN

#### **实现方案**
```java
// 基础权限查询模板
public static final String USER_PERMISSION_FILTER = 
    "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = r.knowledgeId " +
    "AND bk.userId = :userId AND bk.isActive = true)";

@Query("SELECT r FROM {Resource}Entity r WHERE " + USER_PERMISSION_FILTER)
List<{Resource}Entity> findAllByUserId(@Param("userId") String userId);
```

### **3. 嵌套实体管理**

#### **设计策略**
- `@ElementCollection`：一对多关系
- `@Embeddable`：嵌套对象定义
- `@OrderBy`：自动排序
- `@CollectionTable`：关联表配置

#### **实现示例**
```java
@ElementCollection
@CollectionTable(
    name = "text_question_scoring_points",
    joinColumns = @JoinColumn(name = "text_question_id")
)
@OrderBy("sortOrder ASC")
private List<ScoringPoint> scoringPoints = new ArrayList<>();
```

### **4. 数据验证框架**

#### **多层验证策略**
1. **注解验证**：`@Valid`、`@NotBlank`、`@Size`
2. **业务验证**：DTO层自定义验证方法
3. **数据库约束**：表级约束和索引

#### **验证实现**
```java
// DTO层业务验证
public boolean isValidConfiguration() {
    // 检查排序顺序连续性
    List<Integer> sortOrders = items.stream()
            .map(Item::getSortOrder)
            .sorted()
            .collect(Collectors.toList());
    
    for (int i = 0; i < sortOrders.size(); i++) {
        if (sortOrders.get(i) != i) {
            return false;
        }
    }
    return true;
}

// Service层验证调用
private void validateRequest(CreateRequest request) {
    if (!request.isValidConfiguration()) {
        throw new RuntimeException("配置不合理：排序顺序必须从0开始且连续");
    }
}
```

## 📊 **性能优化策略**

### **1. 数据库优化**

#### **索引策略**
```sql
-- 用户查询优化
CREATE INDEX idx_{table}_user_created ON {table}_data(knowledge_id, created_at);

-- 类型查询优化
CREATE INDEX idx_{table}_type ON {table}_data(type);

-- 复合查询优化
CREATE INDEX idx_{table}_complex ON {table}_data(type, difficulty_level, created_at);
```

#### **查询优化**
- 使用EXISTS替代JOIN提升性能
- 分页查询避免大数据量加载
- 统计查询缓存热点数据

### **2. 应用层优化**

#### **事务优化**
```java
@Transactional(readOnly = true)  // 只读查询优化
public List<ResponseDTO> getAllByUser(String userId) {
    return repository.findAllByUserId(userId)
            .stream()
            .map(ResponseDTO::fromEntity)
            .collect(Collectors.toList());
}

@Transactional  // 写操作事务
public ResponseDTO create(CreateRequest request, String userId) {
    // 业务逻辑
}
```

#### **内存优化**
- 按需加载嵌套实体
- DTO转换减少内存占用
- 分页查询控制数据量

## 🔄 **扩展性设计**

### **1. 新题型扩展**

#### **扩展模式**
```java
// 新题型只需实现相同的五层架构
public class NewQuestionTypeEntity {
    // 继承通用字段和行为
}

public interface NewQuestionTypeRepository extends JpaRepository<NewQuestionTypeEntity, String> {
    // 复用相同的查询模式
}

public class NewQuestionTypeService {
    // 复用相同的业务逻辑模式
}
```

### **2. 功能扩展**

#### **接口扩展**
- 版本控制：`/api/v2/{resource}`
- 批量操作：`/api/{resource}/batch`
- 导入导出：`/api/{resource}/import|export`

### **3. 微服务拆分**

#### **拆分策略**
```
当前单体服务 → 按题型拆分微服务
├── text-question-service
├── choice-question-service
├── code-question-service
└── common-service (共享组件)
```

## 🛡️ **安全性设计**

### **1. 认证授权**
- JWT令牌验证
- 用户身份识别
- 接口权限控制

### **2. 数据安全**
- 用户数据隔离
- SQL注入防护
- 敏感信息保护

### **3. 接口安全**
- 参数验证
- 异常处理
- 日志记录

## 📝 **代码质量标准**

### **1. 编码规范**
- Java编码规范100%遵循
- 变量命名语义化
- 方法职责单一化

### **2. 注释标准**
- 类级别：功能说明和使用示例
- 方法级别：参数、返回值、异常说明
- 复杂逻辑：行内注释解释

### **3. 异常处理**
- 统一异常处理机制
- 用户友好错误信息
- 完整的错误日志记录

---

## 🎯 **架构优势总结**

1. **统一性**：三个题型模块架构完全一致
2. **扩展性**：易于添加新题型和功能
3. **维护性**：清晰的分层和职责分离
4. **性能**：合理的查询策略和优化
5. **安全性**：完善的权限控制机制
6. **质量**：严格的代码标准和测试覆盖

这套架构设计为Memorin项目的长期发展奠定了坚实的技术基础！

---

*文档版本：v2.0*  
*最后更新：2025年8月15日* 