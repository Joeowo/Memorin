-- ====================================================================
-- Memorin 智能知识复习系统 - 数据库初始化脚本
-- 版本: v2.0 - 模块化设计
-- 设计理念: 域分离、核心边缘分离、模块化、解耦化
-- 创建时间: 2025-01-08
-- ====================================================================

-- 设置字符集和校对规则
SET NAMES utf8mb4;
SET character_set_client = utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS memorin_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE memorin_db;

-- ====================================================================
-- 1. 用户认证模块 (User Authentication Module)
-- ====================================================================

-- 1.1 用户核心表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '账户状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) COMMENT='用户核心信息表';

-- 1.2 用户扩展信息表
CREATE TABLE user_profiles (
    user_id BIGINT PRIMARY KEY,
    nickname VARCHAR(100) COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    location VARCHAR(100) COMMENT '地理位置',
    website VARCHAR(200) COMMENT '个人网站',
    birth_date DATE COMMENT '出生日期',
    gender ENUM('male', 'female', 'other') COMMENT '性别',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    language VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言偏好',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='用户扩展信息表';

-- 1.3 用户认证令牌表
CREATE TABLE user_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_type ENUM('access', 'refresh', 'reset_password', 'email_verify') NOT NULL,
    token_value VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    device_info JSON COMMENT '设备信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_type (user_id, token_type),
    INDEX idx_token_value (token_value(100)),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_active (is_active),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='用户认证令牌表';

-- 1.4 用户设置表
CREATE TABLE user_settings (
    user_id BIGINT PRIMARY KEY,
    theme ENUM('light', 'dark', 'auto') DEFAULT 'auto' COMMENT '主题模式',
    review_notification BOOLEAN DEFAULT TRUE COMMENT '复习提醒',
    email_notification BOOLEAN DEFAULT TRUE COMMENT '邮件通知',
    daily_review_goal INT DEFAULT 20 COMMENT '每日复习目标',
    review_time_preference JSON COMMENT '复习时间偏好',
    difficulty_preference TINYINT DEFAULT 3 COMMENT '难度偏好',
    auto_play_audio BOOLEAN DEFAULT FALSE COMMENT '自动播放音频',
    show_explanation BOOLEAN DEFAULT TRUE COMMENT '显示解析',
    privacy_settings JSON COMMENT '隐私设置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='用户设置表';

-- ====================================================================
-- 2. 知识库管理模块 (Knowledge Base Management Module)
-- ====================================================================

-- 2.1 知识库核心表
CREATE TABLE knowledge_bases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '知识库名称',
    visibility ENUM('private', 'public', 'shared') DEFAULT 'private' COMMENT '可见性',
    is_official BOOLEAN DEFAULT FALSE COMMENT '是否官方知识库',
    status ENUM('active', 'archived', 'draft') DEFAULT 'active' COMMENT '状态',
    content_count INT DEFAULT 0 COMMENT '内容数量',
    subscriber_count INT DEFAULT 0 COMMENT '订阅者数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_owner_visibility (owner_id, visibility),
    INDEX idx_public_official (visibility, is_official),
    INDEX idx_status (status),
    
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='知识库核心表';

-- 2.2 知识库元数据表
CREATE TABLE knowledge_base_metadata (
    knowledge_base_id BIGINT PRIMARY KEY,
    description TEXT COMMENT '知识库描述',
    icon VARCHAR(50) DEFAULT '📚' COMMENT '图标',
    color VARCHAR(20) DEFAULT '#667eea' COMMENT '主题色',
    category VARCHAR(50) COMMENT '分类',
    tags JSON COMMENT '标签',
    difficulty_level DECIMAL(3,1) COMMENT '平均难度',
    estimated_time_hours INT COMMENT '预计学习时间（小时）',
    prerequisite_knowledge JSON COMMENT '前置知识要求',
    learning_objectives JSON COMMENT '学习目标',
    author_notes TEXT COMMENT '作者说明',
    version VARCHAR(20) DEFAULT '1.0.0' COMMENT '版本号',
    changelog JSON COMMENT '更新日志',
    allow_fork BOOLEAN DEFAULT TRUE COMMENT '允许复制',
    allow_contribute BOOLEAN DEFAULT FALSE COMMENT '允许协作',
    license VARCHAR(50) DEFAULT 'CC BY-SA 4.0' COMMENT '许可证',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty_level),
    
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE
) COMMENT='知识库元数据表';

-- 2.3 知识区核心表
CREATE TABLE knowledge_areas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '知识区名称',
    parent_area_id BIGINT COMMENT '父知识区ID',
    level TINYINT DEFAULT 1 COMMENT '层级深度',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_creator_active (creator_id, is_active),
    INDEX idx_parent_sort (parent_area_id, sort_order),
    INDEX idx_level (level),
    
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_area_id) REFERENCES knowledge_areas(id) ON DELETE SET NULL
) COMMENT='知识区核心表';

-- 2.4 知识区元数据表
CREATE TABLE knowledge_area_metadata (
    knowledge_area_id BIGINT PRIMARY KEY,
    description TEXT COMMENT '知识区描述',
    color VARCHAR(20) DEFAULT '#667eea' COMMENT '主题色',
    icon VARCHAR(50) COMMENT '图标',
    category VARCHAR(50) COMMENT '分类',
    tags JSON COMMENT '标签',
    learning_path JSON COMMENT '学习路径',
    prerequisites JSON COMMENT '前置要求',
    objectives JSON COMMENT '学习目标',
    estimated_hours INT COMMENT '预计学习时间',
    difficulty_range JSON COMMENT '难度范围',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    
    FOREIGN KEY (knowledge_area_id) REFERENCES knowledge_areas(id) ON DELETE CASCADE
) COMMENT='知识区元数据表';

-- ====================================================================
-- 3. 知识点内容模块 (Knowledge Point Content Module)
-- ====================================================================

-- 3.1 知识点内容核心表
CREATE TABLE knowledge_point_contents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_hash VARCHAR(64) UNIQUE NOT NULL COMMENT '内容哈希值',
    type ENUM('fill', 'choice') NOT NULL DEFAULT 'fill' COMMENT '题目类型',
    question TEXT NOT NULL COMMENT '问题内容',
    answer TEXT NOT NULL COMMENT '答案内容',
    explanation TEXT COMMENT '解释说明',
    created_by BIGINT NOT NULL COMMENT '创建者',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_created_by (created_by),
    INDEX idx_type (type),
    INDEX idx_public (is_public),
    INDEX idx_usage_count (usage_count),
    FULLTEXT INDEX ft_content (question, answer, explanation),
    
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='知识点内容核心表';

-- 3.2 知识点元数据表
CREATE TABLE knowledge_point_metadata (
    content_id BIGINT PRIMARY KEY,
    difficulty TINYINT DEFAULT 3 COMMENT '难度等级 1-5',
    estimated_time INT DEFAULT 60 COMMENT '预估答题时间（秒）',
    score DECIMAL(5,2) DEFAULT 1.0 COMMENT '分值',
    source VARCHAR(100) COMMENT '来源标识',
    source_url VARCHAR(500) COMMENT '来源链接',
    tags JSON COMMENT '标签数组',
    keywords JSON COMMENT '关键词',
    related_concepts JSON COMMENT '相关概念',
    cognitive_level ENUM('remember', 'understand', 'apply', 'analyze', 'evaluate', 'create') COMMENT '认知层次',
    bloom_taxonomy VARCHAR(50) COMMENT '布鲁姆分类',
    learning_objective TEXT COMMENT '学习目标',
    hint TEXT COMMENT '提示信息',
    references JSON COMMENT '参考资料',
    multimedia_urls JSON COMMENT '多媒体资源URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_difficulty (difficulty),
    INDEX idx_cognitive_level (cognitive_level),
    INDEX idx_score (score),
    
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='知识点元数据表';

-- 3.3 选择题选项表
CREATE TABLE knowledge_point_choice_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    choice_type ENUM('single', 'multiple') NOT NULL COMMENT '选择题类型',
    option_key VARCHAR(10) NOT NULL COMMENT '选项标识 A/B/C/D',
    option_text TEXT NOT NULL COMMENT '选项内容',
    is_correct BOOLEAN DEFAULT FALSE COMMENT '是否正确答案',
    explanation TEXT COMMENT '选项解释',
    sort_order TINYINT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_content_type (content_id, choice_type),
    INDEX idx_content_order (content_id, sort_order),
    
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='选择题选项表';

-- ====================================================================
-- 4. 关联关系模块 (Relationship Module)
-- ====================================================================

-- 4.1 知识库-内容关联表
CREATE TABLE knowledge_base_content_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    category VARCHAR(50) COMMENT '在知识库中的分类',
    chapter VARCHAR(100) COMMENT '章节',
    section VARCHAR(100) COMMENT '小节',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '权重',
    is_required BOOLEAN DEFAULT TRUE COMMENT '是否必学',
    added_by BIGINT NOT NULL COMMENT '添加者',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_base_content (knowledge_base_id, content_id),
    INDEX idx_base_category_order (knowledge_base_id, category, sort_order),
    INDEX idx_content_bases (content_id),
    INDEX idx_added_by (added_by),
    
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE,
    FOREIGN KEY (added_by) REFERENCES users(id)
) COMMENT='知识库-内容关联表';

-- 4.2 知识区-内容关联表
CREATE TABLE knowledge_area_content_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_area_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    relevance_score DECIMAL(3,2) DEFAULT 1.0 COMMENT '相关度评分',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否主要归属',
    relationship_type ENUM('contains', 'relates', 'prerequisite', 'follow_up') DEFAULT 'contains' COMMENT '关系类型',
    added_by BIGINT NOT NULL COMMENT '添加者',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_area_content (knowledge_area_id, content_id),
    INDEX idx_area_relevance (knowledge_area_id, relevance_score),
    INDEX idx_content_areas (content_id),
    INDEX idx_primary (is_primary),
    INDEX idx_relationship_type (relationship_type),
    
    FOREIGN KEY (knowledge_area_id) REFERENCES knowledge_areas(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE,
    FOREIGN KEY (added_by) REFERENCES users(id)
) COMMENT='知识区-内容关联表';

-- ====================================================================
-- 5. 用户学习状态模块 (User Learning State Module)
-- ====================================================================

-- 5.1 用户知识点状态核心表
CREATE TABLE user_knowledge_point_states (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    mastery_level TINYINT DEFAULT 1 COMMENT '掌握程度 1-5',
    review_count INT DEFAULT 0 COMMENT '复习次数',
    correct_count INT DEFAULT 0 COMMENT '正确次数',
    consecutive_correct INT DEFAULT 0 COMMENT '连续正确次数',
    consecutive_wrong INT DEFAULT 0 COMMENT '连续错误次数',
    first_learned_at TIMESTAMP NULL COMMENT '首次学习时间',
    last_reviewed_at TIMESTAMP NULL COMMENT '最后复习时间',
    next_review_at TIMESTAMP NULL COMMENT '下次复习时间',
    is_bookmarked BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    is_hidden BOOLEAN DEFAULT FALSE COMMENT '是否隐藏',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_content (user_id, content_id),
    INDEX idx_user_mastery (user_id, mastery_level),
    INDEX idx_next_review (next_review_at),
    INDEX idx_bookmarked (user_id, is_bookmarked),
    INDEX idx_content_users (content_id),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='用户知识点状态核心表';

-- 5.2 用户知识点SM-2算法数据表
CREATE TABLE user_knowledge_point_sm2_data (
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    easiness_factor DECIMAL(4,2) DEFAULT 2.5 COMMENT 'SM-2算法难度因子',
    repetition_number INT DEFAULT 0 COMMENT 'SM-2算法重复次数',
    inter_repetition_interval INT DEFAULT 1 COMMENT 'SM-2算法间隔天数',
    last_interval INT DEFAULT 1 COMMENT '上次间隔天数',
    quality_responses JSON COMMENT '质量响应历史',
    algorithm_version VARCHAR(10) DEFAULT 'SM2' COMMENT '算法版本',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, content_id),
    INDEX idx_easiness_factor (easiness_factor),
    INDEX idx_interval (inter_repetition_interval),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='用户知识点SM-2算法数据表';

-- 5.3 用户知识点学习表现表
CREATE TABLE user_knowledge_point_performance (
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    personal_difficulty TINYINT COMMENT '个人感知难度 1-5',
    personal_notes TEXT COMMENT '个人笔记',
    personal_tags JSON COMMENT '个人标签',
    avg_response_time INT COMMENT '平均响应时间（秒）',
    fastest_time INT COMMENT '最快答题时间',
    slowest_time INT COMMENT '最慢答题时间',
    total_study_time INT DEFAULT 0 COMMENT '总学习时间（秒）',
    mistake_patterns JSON COMMENT '错误模式分析',
    learning_curve_data JSON COMMENT '学习曲线数据',
    confidence_level TINYINT COMMENT '信心水平 1-5',
    last_confidence_update TIMESTAMP NULL COMMENT '最后信心更新时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, content_id),
    INDEX idx_personal_difficulty (personal_difficulty),
    INDEX idx_confidence_level (confidence_level),
    INDEX idx_avg_response_time (avg_response_time),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='用户知识点学习表现表';

-- ====================================================================
-- 6. 题目列表模块 (Question List Module)
-- ====================================================================

-- 6.1 自定义题目列表核心表
CREATE TABLE custom_question_lists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '题目列表名称',
    type ENUM('practice', 'exam', 'review', 'custom') DEFAULT 'practice' COMMENT '列表类型',
    status ENUM('draft', 'active', 'archived') DEFAULT 'active' COMMENT '状态',
    total_count INT DEFAULT 0 COMMENT '题目总数',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    is_template BOOLEAN DEFAULT FALSE COMMENT '是否为模板',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    last_used_at TIMESTAMP NULL COMMENT '最后使用时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_status (user_id, status),
    INDEX idx_public_template (is_public, is_template),
    INDEX idx_type (type),
    INDEX idx_usage (usage_count, last_used_at),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='自定义题目列表核心表';

-- 6.2 题目列表元数据表
CREATE TABLE question_list_metadata (
    question_list_id BIGINT PRIMARY KEY,
    description TEXT COMMENT '描述说明',
    estimated_time INT COMMENT '预估完成时间（分钟）',
    difficulty_level DECIMAL(3,1) COMMENT '平均难度等级',
    tags JSON COMMENT '标签',
    category VARCHAR(50) COMMENT '分类',
    target_audience VARCHAR(100) COMMENT '目标用户群',
    learning_objectives JSON COMMENT '学习目标',
    prerequisites JSON COMMENT '前置要求',
    generation_config JSON COMMENT '生成配置',
    source_summary JSON COMMENT '数据源摘要',
    performance_metrics JSON COMMENT '性能指标',
    review_feedback JSON COMMENT '用户反馈',
    version VARCHAR(20) DEFAULT '1.0.0' COMMENT '版本号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty_level),
    
    FOREIGN KEY (question_list_id) REFERENCES custom_question_lists(id) ON DELETE CASCADE
) COMMENT='题目列表元数据表';

-- 6.3 题目列表-知识点关联表
CREATE TABLE question_list_point_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_list_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    sequence_order INT NOT NULL COMMENT '在列表中的顺序',
    weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '权重系数',
    source_type VARCHAR(50) COMMENT '来源类型',
    source_id BIGINT COMMENT '来源ID',
    selection_reason VARCHAR(200) COMMENT '选择原因',
    custom_tags JSON COMMENT '自定义标签',
    generation_meta JSON COMMENT '生成元数据',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_list_content (question_list_id, content_id),
    INDEX idx_list_sequence (question_list_id, sequence_order),
    INDEX idx_content_lists (content_id),
    INDEX idx_source (source_type, source_id),
    INDEX idx_weight (weight),
    
    FOREIGN KEY (question_list_id) REFERENCES custom_question_lists(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='题目列表-知识点关联表';

-- ====================================================================
-- 7. 复习队列模块 (Review Queue Module)
-- ====================================================================

-- 7.1 用户复习队列表
CREATE TABLE user_review_queues (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    queue_type ENUM('due', 'new', 'mistake', 'bookmark', 'custom') NOT NULL COMMENT '队列类型',
    priority TINYINT DEFAULT 3 COMMENT '优先级 1-5',
    scheduled_at TIMESTAMP NOT NULL COMMENT '计划复习时间',
    estimated_duration INT COMMENT '预估复习时长（秒）',
    context_info JSON COMMENT '上下文信息',
    is_completed BOOLEAN DEFAULT FALSE COMMENT '是否已完成',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_type_scheduled (user_id, queue_type, scheduled_at),
    INDEX idx_user_priority (user_id, priority),
    INDEX idx_scheduled_completed (scheduled_at, is_completed),
    INDEX idx_content_queues (content_id),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES knowledge_point_contents(id) ON DELETE CASCADE
) COMMENT='用户复习队列表';

-- 7.2 复习队列元数据表
CREATE TABLE review_queue_metadata (
    user_id BIGINT NOT NULL,
    queue_date DATE NOT NULL COMMENT '队列日期',
    total_items INT DEFAULT 0 COMMENT '总项目数',
    completed_items INT DEFAULT 0 COMMENT '已完成项目数',
    due_items INT DEFAULT 0 COMMENT '到期项目数',
    new_items INT DEFAULT 0 COMMENT '新学习项目数',
    review_goal INT DEFAULT 20 COMMENT '复习目标',
    estimated_total_time INT COMMENT '预估总时间（分钟）',
    actual_total_time INT DEFAULT 0 COMMENT '实际总时间（分钟）',
    queue_generated_at TIMESTAMP NULL COMMENT '队列生成时间',
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, queue_date),
    INDEX idx_queue_date (queue_date),
    INDEX idx_generated_at (queue_generated_at),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='复习队列元数据表';

-- ====================================================================
-- 8. 订阅关系模块 (Subscription Module)
-- ====================================================================

-- 8.1 用户知识库订阅核心表
CREATE TABLE user_knowledge_base_subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    knowledge_base_id BIGINT NOT NULL,
    role ENUM('viewer', 'contributor', 'admin') DEFAULT 'viewer' COMMENT '角色权限',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    subscription_type ENUM('free', 'premium', 'organization') DEFAULT 'free' COMMENT '订阅类型',
    auto_sync BOOLEAN DEFAULT TRUE COMMENT '自动同步更新',
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP NULL COMMENT '最后访问时间',
    access_count INT DEFAULT 0 COMMENT '访问次数',
    
    UNIQUE KEY uk_user_base (user_id, knowledge_base_id),
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_base_subscribers (knowledge_base_id),
    INDEX idx_role (role),
    INDEX idx_subscription_type (subscription_type),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE
) COMMENT='用户知识库订阅核心表';

-- 8.2 订阅设置表
CREATE TABLE subscription_settings (
    subscription_id BIGINT PRIMARY KEY,
    personal_name VARCHAR(100) COMMENT '个人重命名',
    personal_color VARCHAR(20) COMMENT '个人颜色',
    personal_icon VARCHAR(50) COMMENT '个人图标',
    sort_order INT DEFAULT 0 COMMENT '个人排序',
    notification_enabled BOOLEAN DEFAULT TRUE COMMENT '通知开启',
    auto_add_new_content BOOLEAN DEFAULT TRUE COMMENT '自动添加新内容',
    sync_frequency ENUM('realtime', 'daily', 'weekly', 'manual') DEFAULT 'daily' COMMENT '同步频率',
    content_filter_settings JSON COMMENT '内容过滤设置',
    personal_tags JSON COMMENT '个人标签',
    notes TEXT COMMENT '个人备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (subscription_id) REFERENCES user_knowledge_base_subscriptions(id) ON DELETE CASCADE
) COMMENT='订阅设置表';

-- ====================================================================
-- 9. 系统支撑表 (System Support Tables)
-- ====================================================================

-- 9.1 系统配置表
CREATE TABLE system_configs (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value TEXT NOT NULL,
    config_type ENUM('string', 'integer', 'boolean', 'json') DEFAULT 'string',
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='系统配置表';

-- 9.2 数据库版本表
CREATE TABLE schema_versions (
    version VARCHAR(20) PRIMARY KEY,
    description TEXT,
    migration_script TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT='数据库版本管理表';

-- ====================================================================
-- 10. 创建视图 (Create Views)
-- ====================================================================

-- 10.1 用户知识点完整信息视图
CREATE VIEW v_user_knowledge_points_full AS
SELECT 
    ukps.user_id,
    ukps.content_id,
    kpc.question,
    kpc.answer,
    kpc.type,
    ukps.mastery_level,
    ukps.review_count,
    ukps.correct_count,
    ukps.next_review_at,
    ukps.is_bookmarked,
    kpm.difficulty,
    kpm.estimated_time,
    ukpp.personal_difficulty,
    ukpp.personal_notes,
    ukpp.avg_response_time,
    sm2.easiness_factor,
    sm2.inter_repetition_interval,
    CASE 
        WHEN ukps.mastery_level >= 4 THEN 'mastered'
        WHEN ukps.next_review_at <= NOW() THEN 'due'
        WHEN ukps.next_review_at IS NULL THEN 'new'
        ELSE 'learning'
    END as learning_status
FROM user_knowledge_point_states ukps
JOIN knowledge_point_contents kpc ON ukps.content_id = kpc.id
LEFT JOIN knowledge_point_metadata kpm ON kpc.id = kpm.content_id
LEFT JOIN user_knowledge_point_performance ukpp ON ukps.user_id = ukpp.user_id AND ukps.content_id = ukpp.content_id
LEFT JOIN user_knowledge_point_sm2_data sm2 ON ukps.user_id = sm2.user_id AND ukps.content_id = sm2.content_id;

-- 10.2 知识库统计视图
CREATE VIEW v_knowledge_base_stats AS
SELECT 
    kb.id,
    kb.name,
    kb.owner_id,
    kb.visibility,
    kb.content_count,
    kb.subscriber_count,
    COUNT(DISTINCT kbcr.content_id) as actual_content_count,
    COUNT(DISTINCT ukbs.user_id) as actual_subscriber_count,
    AVG(kpm.difficulty) as avg_difficulty,
    SUM(kpm.estimated_time) as total_estimated_time
FROM knowledge_bases kb
LEFT JOIN knowledge_base_content_relations kbcr ON kb.id = kbcr.knowledge_base_id
LEFT JOIN knowledge_point_metadata kpm ON kbcr.content_id = kpm.content_id
LEFT JOIN user_knowledge_base_subscriptions ukbs ON kb.id = ukbs.knowledge_base_id AND ukbs.is_active = TRUE
GROUP BY kb.id;

-- ====================================================================
-- 11. 创建索引优化 (Index Optimization)
-- ====================================================================

-- 复合索引优化
CREATE INDEX idx_user_content_review ON user_knowledge_point_states (user_id, next_review_at, mastery_level);
CREATE INDEX idx_content_base_category ON knowledge_base_content_relations (knowledge_base_id, category, sort_order);
CREATE INDEX idx_queue_user_priority_time ON user_review_queues (user_id, priority DESC, scheduled_at ASC);

-- JSON字段索引（MySQL 5.7+支持）
-- CREATE INDEX idx_kpm_tags ON knowledge_point_metadata ((CAST(tags AS CHAR(255) ARRAY)));
-- CREATE INDEX idx_user_settings_privacy ON user_settings ((CAST(privacy_settings->'$.visibility' AS CHAR(20))));

-- ====================================================================
-- 12. 触发器 (Triggers)
-- ====================================================================

-- 12.1 更新知识库内容数量触发器
DELIMITER $$
CREATE TRIGGER tr_update_kb_content_count_insert
AFTER INSERT ON knowledge_base_content_relations
FOR EACH ROW
BEGIN
    UPDATE knowledge_bases 
    SET content_count = (
        SELECT COUNT(*) 
        FROM knowledge_base_content_relations 
        WHERE knowledge_base_id = NEW.knowledge_base_id
    )
    WHERE id = NEW.knowledge_base_id;
END$$

CREATE TRIGGER tr_update_kb_content_count_delete
AFTER DELETE ON knowledge_base_content_relations
FOR EACH ROW
BEGIN
    UPDATE knowledge_bases 
    SET content_count = (
        SELECT COUNT(*) 
        FROM knowledge_base_content_relations 
        WHERE knowledge_base_id = OLD.knowledge_base_id
    )
    WHERE id = OLD.knowledge_base_id;
END$$
DELIMITER ;

-- 12.2 更新订阅者数量触发器
DELIMITER $$
CREATE TRIGGER tr_update_kb_subscriber_count_insert
AFTER INSERT ON user_knowledge_base_subscriptions
FOR EACH ROW
BEGIN
    UPDATE knowledge_bases 
    SET subscriber_count = (
        SELECT COUNT(*) 
        FROM user_knowledge_base_subscriptions 
        WHERE knowledge_base_id = NEW.knowledge_base_id AND is_active = TRUE
    )
    WHERE id = NEW.knowledge_base_id;
END$$

CREATE TRIGGER tr_update_kb_subscriber_count_update
AFTER UPDATE ON user_knowledge_base_subscriptions
FOR EACH ROW
BEGIN
    IF OLD.is_active != NEW.is_active THEN
        UPDATE knowledge_bases 
        SET subscriber_count = (
            SELECT COUNT(*) 
            FROM user_knowledge_base_subscriptions 
            WHERE knowledge_base_id = NEW.knowledge_base_id AND is_active = TRUE
        )
        WHERE id = NEW.knowledge_base_id;
    END IF;
END$$
DELIMITER ;

-- ====================================================================
-- 13. 存储过程 (Stored Procedures)
-- ====================================================================

-- 13.1 生成用户复习队列
DELIMITER $$
CREATE PROCEDURE GenerateUserReviewQueue(
    IN p_user_id BIGINT,
    IN p_queue_date DATE,
    IN p_max_items INT DEFAULT 50
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 清除当日已存在的队列
    DELETE FROM user_review_queues 
    WHERE user_id = p_user_id 
    AND DATE(scheduled_at) = p_queue_date;
    
    -- 生成到期复习队列
    INSERT INTO user_review_queues (
        user_id, content_id, queue_type, priority, 
        scheduled_at, estimated_duration
    )
    SELECT 
        ukps.user_id,
        ukps.content_id,
        'due' as queue_type,
        CASE 
            WHEN ukps.next_review_at <= NOW() - INTERVAL 1 DAY THEN 5
            WHEN ukps.next_review_at <= NOW() THEN 4
            ELSE 3
        END as priority,
        TIMESTAMP(p_queue_date, '09:00:00') as scheduled_at,
        COALESCE(kpm.estimated_time, 60) as estimated_duration
    FROM user_knowledge_point_states ukps
    LEFT JOIN knowledge_point_metadata kpm ON ukps.content_id = kpm.content_id
    WHERE ukps.user_id = p_user_id
    AND ukps.next_review_at <= TIMESTAMP(p_queue_date, '23:59:59')
    AND ukps.is_hidden = FALSE
    ORDER BY ukps.next_review_at ASC
    LIMIT p_max_items;
    
    -- 更新队列元数据
    INSERT INTO review_queue_metadata (
        user_id, queue_date, total_items, due_items,
        queue_generated_at
    ) VALUES (
        p_user_id, 
        p_queue_date,
        (SELECT COUNT(*) FROM user_review_queues 
         WHERE user_id = p_user_id AND DATE(scheduled_at) = p_queue_date),
        (SELECT COUNT(*) FROM user_review_queues 
         WHERE user_id = p_user_id AND DATE(scheduled_at) = p_queue_date AND queue_type = 'due'),
        NOW()
    ) ON DUPLICATE KEY UPDATE
        total_items = VALUES(total_items),
        due_items = VALUES(due_items),
        queue_generated_at = VALUES(queue_generated_at);
    
    COMMIT;
END$$
DELIMITER ;

-- ====================================================================
-- 14. 初始化系统配置
-- ====================================================================

-- 插入系统配置
INSERT INTO system_configs (config_key, config_value, config_type, description, is_public) VALUES
('system.version', '2.0.0', 'string', '系统版本', TRUE),
('review.default_daily_goal', '20', 'integer', '默认每日复习目标', TRUE),
('review.max_queue_size', '100', 'integer', '最大复习队列大小', FALSE),
('sm2.initial_easiness', '2.5', 'string', 'SM-2算法初始难度因子', FALSE),
('knowledge_base.max_content_size', '10000', 'integer', '知识库最大内容数量', FALSE),
('user.max_subscriptions', '50', 'integer', '用户最大订阅数量', FALSE);

-- 插入版本记录
INSERT INTO schema_versions (version, description) VALUES
('2.0.0', '模块化数据库设计 - 核心边缘分离，多模块解耦');

-- ====================================================================
-- 脚本结束
-- ==================================================================== 