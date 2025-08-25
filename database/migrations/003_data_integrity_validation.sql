-- ====================================================================
-- Memorin 智能知识复习系统 - 数据完整性验证和一致性检查
-- 版本: v2.0
-- 文件: 003_data_integrity_validation.sql
-- 用途: 数据完整性验证、一致性检查、数据质量保证
-- 创建时间: 2025-01-08
-- ====================================================================

USE memorin_db;

-- ====================================================================
-- 1. 数据完整性约束检查
-- ====================================================================

-- 1.1 检查外键约束完整性
DELIMITER $$
CREATE PROCEDURE CheckForeignKeyIntegrity()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_name VARCHAR(64);
    DECLARE column_name VARCHAR(64);
    DECLARE referenced_table VARCHAR(64);
    DECLARE referenced_column VARCHAR(64);
    DECLARE constraint_name VARCHAR(64);
    DECLARE violation_count INT DEFAULT 0;
    DECLARE total_violations INT DEFAULT 0;
    
    -- 声明游标遍历所有外键约束
    DECLARE fk_cursor CURSOR FOR
        SELECT 
            TABLE_NAME,
            COLUMN_NAME,
            REFERENCED_TABLE_NAME,
            REFERENCED_COLUMN_NAME,
            CONSTRAINT_NAME
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = 'memorin_db'
        AND REFERENCED_TABLE_NAME IS NOT NULL;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 创建临时表存储检查结果
    DROP TEMPORARY TABLE IF EXISTS temp_fk_violations;
    CREATE TEMPORARY TABLE temp_fk_violations (
        constraint_name VARCHAR(64),
        table_name VARCHAR(64),
        column_name VARCHAR(64),
        violation_count INT,
        check_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    
    OPEN fk_cursor;
    
    read_loop: LOOP
        FETCH fk_cursor INTO table_name, column_name, referenced_table, referenced_column, constraint_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 动态SQL检查外键违反情况
        SET @sql = CONCAT(
            'SELECT COUNT(*) INTO @violation_count FROM ', table_name, ' t1 ',
            'LEFT JOIN ', referenced_table, ' t2 ON t1.', column_name, ' = t2.', referenced_column, ' ',
            'WHERE t1.', column_name, ' IS NOT NULL AND t2.', referenced_column, ' IS NULL'
        );
        
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SET violation_count = @violation_count;
        SET total_violations = total_violations + violation_count;
        
        -- 记录违反情况
        IF violation_count > 0 THEN
            INSERT INTO temp_fk_violations (constraint_name, table_name, column_name, violation_count)
            VALUES (constraint_name, table_name, column_name, violation_count);
        END IF;
        
    END LOOP;
    
    CLOSE fk_cursor;
    
    -- 输出检查结果
    SELECT 
        CASE 
            WHEN total_violations = 0 THEN 'PASS'
            ELSE 'FAIL'
        END as integrity_status,
        total_violations as total_violations,
        NOW() as check_time;
    
    -- 如果有违反情况，显示详细信息
    IF total_violations > 0 THEN
        SELECT * FROM temp_fk_violations ORDER BY violation_count DESC;
    END IF;
    
    DROP TEMPORARY TABLE temp_fk_violations;
END$$
DELIMITER ;

-- 1.2 检查唯一约束完整性
DELIMITER $$
CREATE PROCEDURE CheckUniqueConstraintIntegrity()
BEGIN
    DECLARE violation_count INT DEFAULT 0;
    
    -- 检查用户名唯一性
    SELECT COUNT(*) - COUNT(DISTINCT username) INTO violation_count
    FROM users;
    
    IF violation_count > 0 THEN
        SELECT 'FAIL' as status, 'users.username' as constraint_name, violation_count as violations;
        
        -- 显示重复的用户名
        SELECT username, COUNT(*) as duplicate_count
        FROM users
        GROUP BY username
        HAVING COUNT(*) > 1;
    END IF;
    
    -- 检查邮箱唯一性
    SELECT COUNT(*) - COUNT(DISTINCT email) INTO violation_count
    FROM users;
    
    IF violation_count > 0 THEN
        SELECT 'FAIL' as status, 'users.email' as constraint_name, violation_count as violations;
        
        -- 显示重复的邮箱
        SELECT email, COUNT(*) as duplicate_count
        FROM users
        GROUP BY email
        HAVING COUNT(*) > 1;
    END IF;
    
    -- 检查知识点内容哈希唯一性
    SELECT COUNT(*) - COUNT(DISTINCT content_hash) INTO violation_count
    FROM knowledge_point_contents;
    
    IF violation_count > 0 THEN
        SELECT 'FAIL' as status, 'knowledge_point_contents.content_hash' as constraint_name, violation_count as violations;
        
        -- 显示重复的内容哈希
        SELECT content_hash, COUNT(*) as duplicate_count
        FROM knowledge_point_contents
        GROUP BY content_hash
        HAVING COUNT(*) > 1;
    END IF;
    
    SELECT 'PASS' as overall_status, NOW() as check_time;
END$$
DELIMITER ;

-- ====================================================================
-- 2. 数据一致性检查
-- ====================================================================

-- 2.1 检查统计数据一致性
DELIMITER $$
CREATE PROCEDURE CheckStatisticsConsistency()
BEGIN
    DECLARE inconsistency_count INT DEFAULT 0;
    
    -- 创建临时表存储不一致记录
    DROP TEMPORARY TABLE IF EXISTS temp_inconsistencies;
    CREATE TEMPORARY TABLE temp_inconsistencies (
        table_name VARCHAR(64),
        record_id BIGINT,
        field_name VARCHAR(64),
        expected_value INT,
        actual_value INT,
        difference INT
    );
    
    -- 检查知识库内容数量一致性
    INSERT INTO temp_inconsistencies (table_name, record_id, field_name, expected_value, actual_value, difference)
    SELECT 
        'knowledge_bases' as table_name,
        kb.id as record_id,
        'content_count' as field_name,
        COALESCE(actual_count.cnt, 0) as expected_value,
        kb.content_count as actual_value,
        COALESCE(actual_count.cnt, 0) - kb.content_count as difference
    FROM knowledge_bases kb
    LEFT JOIN (
        SELECT knowledge_base_id, COUNT(*) as cnt
        FROM knowledge_base_content_relations
        GROUP BY knowledge_base_id
    ) actual_count ON kb.id = actual_count.knowledge_base_id
    WHERE COALESCE(actual_count.cnt, 0) != kb.content_count;
    
    -- 检查知识库订阅者数量一致性
    INSERT INTO temp_inconsistencies (table_name, record_id, field_name, expected_value, actual_value, difference)
    SELECT 
        'knowledge_bases' as table_name,
        kb.id as record_id,
        'subscriber_count' as field_name,
        COALESCE(actual_count.cnt, 0) as expected_value,
        kb.subscriber_count as actual_value,
        COALESCE(actual_count.cnt, 0) - kb.subscriber_count as difference
    FROM knowledge_bases kb
    LEFT JOIN (
        SELECT knowledge_base_id, COUNT(*) as cnt
        FROM user_knowledge_base_subscriptions
        WHERE is_active = TRUE
        GROUP BY knowledge_base_id
    ) actual_count ON kb.id = actual_count.knowledge_base_id
    WHERE COALESCE(actual_count.cnt, 0) != kb.subscriber_count;
    
    -- 检查知识点使用次数一致性
    INSERT INTO temp_inconsistencies (table_name, record_id, field_name, expected_value, actual_value, difference)
    SELECT 
        'knowledge_point_contents' as table_name,
        kpc.id as record_id,
        'usage_count' as field_name,
        COALESCE(actual_count.cnt, 0) as expected_value,
        kpc.usage_count as actual_value,
        COALESCE(actual_count.cnt, 0) - kpc.usage_count as difference
    FROM knowledge_point_contents kpc
    LEFT JOIN (
        SELECT content_id, COUNT(*) as cnt
        FROM user_knowledge_point_states
        GROUP BY content_id
    ) actual_count ON kpc.id = actual_count.content_id
    WHERE COALESCE(actual_count.cnt, 0) != kpc.usage_count;
    
    -- 检查题目列表数量一致性
    INSERT INTO temp_inconsistencies (table_name, record_id, field_name, expected_value, actual_value, difference)
    SELECT 
        'custom_question_lists' as table_name,
        cql.id as record_id,
        'total_count' as field_name,
        COALESCE(actual_count.cnt, 0) as expected_value,
        cql.total_count as actual_value,
        COALESCE(actual_count.cnt, 0) - cql.total_count as difference
    FROM custom_question_lists cql
    LEFT JOIN (
        SELECT question_list_id, COUNT(*) as cnt
        FROM question_list_point_relations
        WHERE is_active = TRUE
        GROUP BY question_list_id
    ) actual_count ON cql.id = actual_count.question_list_id
    WHERE COALESCE(actual_count.cnt, 0) != cql.total_count;
    
    -- 统计不一致数量
    SELECT COUNT(*) INTO inconsistency_count FROM temp_inconsistencies;
    
    -- 输出检查结果
    SELECT 
        CASE 
            WHEN inconsistency_count = 0 THEN 'PASS'
            ELSE 'FAIL'
        END as consistency_status,
        inconsistency_count as total_inconsistencies,
        NOW() as check_time;
    
    -- 如果有不一致，显示详细信息
    IF inconsistency_count > 0 THEN
        SELECT * FROM temp_inconsistencies ORDER BY ABS(difference) DESC;
    END IF;
    
    DROP TEMPORARY TABLE temp_inconsistencies;
END$$
DELIMITER ;

-- 2.2 检查业务逻辑一致性
DELIMITER $$
CREATE PROCEDURE CheckBusinessLogicConsistency()
BEGIN
    DECLARE logic_error_count INT DEFAULT 0;
    
    -- 创建临时表存储逻辑错误
    DROP TEMPORARY TABLE IF EXISTS temp_logic_errors;
    CREATE TEMPORARY TABLE temp_logic_errors (
        error_type VARCHAR(100),
        table_name VARCHAR(64),
        record_id BIGINT,
        description TEXT,
        severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM'
    );
    
    -- 检查用户复习状态逻辑
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid Review Count' as error_type,
        'user_knowledge_point_states' as table_name,
        id as record_id,
        CONCAT('正确次数(', correct_count, ')超过总复习次数(', review_count, ')') as description,
        'HIGH' as severity
    FROM user_knowledge_point_states
    WHERE correct_count > review_count;
    
    -- 检查连续正确次数逻辑
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid Consecutive Count' as error_type,
        'user_knowledge_point_states' as table_name,
        id as record_id,
        CONCAT('连续正确次数(', consecutive_correct, ')超过正确总次数(', correct_count, ')') as description,
        'MEDIUM' as severity
    FROM user_knowledge_point_states
    WHERE consecutive_correct > correct_count;
    
    -- 检查SM-2算法数据逻辑
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid SM2 Data' as error_type,
        'user_knowledge_point_sm2_data' as table_name,
        CONCAT(user_id, '-', content_id) as record_id,
        CONCAT('SM-2间隔天数异常: ', inter_repetition_interval) as description,
        'MEDIUM' as severity
    FROM user_knowledge_point_sm2_data
    WHERE inter_repetition_interval < 1 OR inter_repetition_interval > 365;
    
    -- 检查复习时间逻辑
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid Review Time' as error_type,
        'user_knowledge_point_states' as table_name,
        id as record_id,
        '下次复习时间早于最后复习时间' as description,
        'HIGH' as severity
    FROM user_knowledge_point_states
    WHERE next_review_at IS NOT NULL 
    AND last_reviewed_at IS NOT NULL 
    AND next_review_at < last_reviewed_at;
    
    -- 检查难度等级范围
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid Difficulty Range' as error_type,
        'knowledge_point_metadata' as table_name,
        content_id as record_id,
        CONCAT('难度等级超出范围: ', difficulty) as description,
        'MEDIUM' as severity
    FROM knowledge_point_metadata
    WHERE difficulty < 1 OR difficulty > 5;
    
    -- 检查掌握程度范围
    INSERT INTO temp_logic_errors (error_type, table_name, record_id, description, severity)
    SELECT 
        'Invalid Mastery Level' as error_type,
        'user_knowledge_point_states' as table_name,
        id as record_id,
        CONCAT('掌握程度超出范围: ', mastery_level) as description,
        'MEDIUM' as severity
    FROM user_knowledge_point_states
    WHERE mastery_level < 1 OR mastery_level > 5;
    
    -- 统计逻辑错误数量
    SELECT COUNT(*) INTO logic_error_count FROM temp_logic_errors;
    
    -- 输出检查结果
    SELECT 
        CASE 
            WHEN logic_error_count = 0 THEN 'PASS'
            ELSE 'FAIL'
        END as logic_status,
        logic_error_count as total_errors,
        NOW() as check_time;
    
    -- 如果有逻辑错误，显示详细信息
    IF logic_error_count > 0 THEN
        SELECT 
            error_type,
            table_name,
            COUNT(*) as error_count,
            severity
        FROM temp_logic_errors 
        GROUP BY error_type, table_name, severity
        ORDER BY severity DESC, error_count DESC;
        
        -- 显示前10个最严重的错误详情
        SELECT * FROM temp_logic_errors 
        WHERE severity IN ('CRITICAL', 'HIGH')
        ORDER BY severity DESC
        LIMIT 10;
    END IF;
    
    DROP TEMPORARY TABLE temp_logic_errors;
END$$
DELIMITER ;

-- ====================================================================
-- 3. 数据质量检查
-- ====================================================================

-- 3.1 检查数据质量问题
DELIMITER $$
CREATE PROCEDURE CheckDataQuality()
BEGIN
    DECLARE quality_issue_count INT DEFAULT 0;
    
    -- 创建临时表存储质量问题
    DROP TEMPORARY TABLE IF EXISTS temp_quality_issues;
    CREATE TEMPORARY TABLE temp_quality_issues (
        issue_type VARCHAR(100),
        table_name VARCHAR(64),
        column_name VARCHAR(64),
        issue_count INT,
        severity ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
        description TEXT
    );
    
    -- 检查空值问题（应该有值但为空的字段）
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Missing Required Data' as issue_type,
        'users' as table_name,
        'email' as column_name,
        COUNT(*) as issue_count,
        'HIGH' as severity,
        '用户邮箱为空' as description
    FROM users 
    WHERE email IS NULL OR email = ''
    HAVING COUNT(*) > 0;
    
    -- 检查用户昵称缺失
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Missing Optional Data' as issue_type,
        'user_profiles' as table_name,
        'nickname' as column_name,
        COUNT(*) as issue_count,
        'LOW' as severity,
        '用户昵称缺失' as description
    FROM user_profiles 
    WHERE nickname IS NULL OR nickname = ''
    HAVING COUNT(*) > 0;
    
    -- 检查知识点内容过短
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Poor Content Quality' as issue_type,
        'knowledge_point_contents' as table_name,
        'question' as column_name,
        COUNT(*) as issue_count,
        'MEDIUM' as severity,
        '题目内容过短（少于10个字符）' as description
    FROM knowledge_point_contents 
    WHERE LENGTH(question) < 10
    HAVING COUNT(*) > 0;
    
    -- 检查答案内容过短
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Poor Content Quality' as issue_type,
        'knowledge_point_contents' as table_name,
        'answer' as column_name,
        COUNT(*) as issue_count,
        'MEDIUM' as severity,
        '答案内容过短（少于2个字符）' as description
    FROM knowledge_point_contents 
    WHERE LENGTH(answer) < 2
    HAVING COUNT(*) > 0;
    
    -- 检查选择题缺少选项
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Incomplete Choice Question' as issue_type,
        'knowledge_point_contents' as table_name,
        'id' as column_name,
        COUNT(*) as issue_count,
        'HIGH' as severity,
        '选择题选项不足（少于2个选项）' as description
    FROM knowledge_point_contents kpc
    WHERE kpc.type = 'choice'
    AND (
        SELECT COUNT(*) 
        FROM knowledge_point_choice_options kpco 
        WHERE kpco.content_id = kpc.id
    ) < 2
    HAVING COUNT(*) > 0;
    
    -- 检查选择题缺少正确答案
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Missing Correct Answer' as issue_type,
        'knowledge_point_contents' as table_name,
        'id' as column_name,
        COUNT(*) as issue_count,
        'CRITICAL' as severity,
        '选择题没有正确答案' as description
    FROM knowledge_point_contents kpc
    WHERE kpc.type = 'choice'
    AND NOT EXISTS (
        SELECT 1 
        FROM knowledge_point_choice_options kpco 
        WHERE kpco.content_id = kpc.id AND kpco.is_correct = TRUE
    )
    HAVING COUNT(*) > 0;
    
    -- 检查用户学习数据异常
    INSERT INTO temp_quality_issues (issue_type, table_name, column_name, issue_count, severity, description)
    SELECT 
        'Suspicious Learning Data' as issue_type,
        'user_knowledge_point_performance' as table_name,
        'avg_response_time' as column_name,
        COUNT(*) as issue_count,
        'MEDIUM' as severity,
        '平均响应时间异常（超过10分钟）' as description
    FROM user_knowledge_point_performance 
    WHERE avg_response_time > 600 -- 10分钟
    HAVING COUNT(*) > 0;
    
    -- 统计质量问题数量
    SELECT SUM(issue_count) INTO quality_issue_count FROM temp_quality_issues;
    
    -- 输出检查结果
    SELECT 
        CASE 
            WHEN quality_issue_count = 0 THEN 'EXCELLENT'
            WHEN quality_issue_count < 10 THEN 'GOOD'
            WHEN quality_issue_count < 50 THEN 'FAIR'
            ELSE 'POOR'
        END as quality_status,
        quality_issue_count as total_issues,
        NOW() as check_time;
    
    -- 显示质量问题摘要
    IF quality_issue_count > 0 THEN
        SELECT 
            issue_type,
            severity,
            SUM(issue_count) as total_count,
            COUNT(*) as affected_tables
        FROM temp_quality_issues 
        GROUP BY issue_type, severity
        ORDER BY severity DESC, total_count DESC;
        
        -- 显示详细问题列表
        SELECT * FROM temp_quality_issues 
        ORDER BY severity DESC, issue_count DESC;
    END IF;
    
    DROP TEMPORARY TABLE temp_quality_issues;
END$$
DELIMITER ;

-- ====================================================================
-- 4. 数据修复和维护
-- ====================================================================

-- 4.1 修复统计数据不一致
DELIMITER $$
CREATE PROCEDURE RepairStatisticsInconsistency()
BEGIN
    DECLARE repair_count INT DEFAULT 0;
    
    -- 修复知识库内容数量
    UPDATE knowledge_bases kb 
    SET content_count = (
        SELECT COALESCE(COUNT(*), 0)
        FROM knowledge_base_content_relations kbcr 
        WHERE kbcr.knowledge_base_id = kb.id
    );
    
    SELECT ROW_COUNT() INTO repair_count;
    
    -- 修复知识库订阅者数量
    UPDATE knowledge_bases kb 
    SET subscriber_count = (
        SELECT COALESCE(COUNT(*), 0)
        FROM user_knowledge_base_subscriptions ukbs 
        WHERE ukbs.knowledge_base_id = kb.id AND ukbs.is_active = TRUE
    );
    
    SET repair_count = repair_count + ROW_COUNT();
    
    -- 修复知识点使用次数
    UPDATE knowledge_point_contents kpc 
    SET usage_count = (
        SELECT COALESCE(COUNT(*), 0)
        FROM user_knowledge_point_states ukps 
        WHERE ukps.content_id = kpc.id
    );
    
    SET repair_count = repair_count + ROW_COUNT();
    
    -- 修复题目列表数量
    UPDATE custom_question_lists cql 
    SET total_count = (
        SELECT COALESCE(COUNT(*), 0)
        FROM question_list_point_relations qlpr 
        WHERE qlpr.question_list_id = cql.id AND qlpr.is_active = TRUE
    );
    
    SET repair_count = repair_count + ROW_COUNT();
    
    SELECT 
        'Statistics repaired successfully' as status,
        repair_count as total_repairs,
        NOW() as repair_time;
END$$
DELIMITER ;

-- 4.2 清理孤立数据
DELIMITER $$
CREATE PROCEDURE CleanOrphanedData()
BEGIN
    DECLARE cleanup_count INT DEFAULT 0;
    
    -- 清理孤立的用户扩展信息
    DELETE up FROM user_profiles up
    LEFT JOIN users u ON up.user_id = u.id
    WHERE u.id IS NULL;
    
    SET cleanup_count = ROW_COUNT();
    
    -- 清理孤立的用户设置
    DELETE us FROM user_settings us
    LEFT JOIN users u ON us.user_id = u.id
    WHERE u.id IS NULL;
    
    SET cleanup_count = cleanup_count + ROW_COUNT();
    
    -- 清理孤立的用户令牌
    DELETE ut FROM user_tokens ut
    LEFT JOIN users u ON ut.user_id = u.id
    WHERE u.id IS NULL;
    
    SET cleanup_count = cleanup_count + ROW_COUNT();
    
    -- 清理过期的令牌
    DELETE FROM user_tokens 
    WHERE expires_at < NOW() AND token_type IN ('access', 'refresh');
    
    SET cleanup_count = cleanup_count + ROW_COUNT();
    
    -- 清理孤立的知识点元数据
    DELETE kpm FROM knowledge_point_metadata kpm
    LEFT JOIN knowledge_point_contents kpc ON kpm.content_id = kpc.id
    WHERE kpc.id IS NULL;
    
    SET cleanup_count = cleanup_count + ROW_COUNT();
    
    -- 清理孤立的选择题选项
    DELETE kpco FROM knowledge_point_choice_options kpco
    LEFT JOIN knowledge_point_contents kpc ON kpco.content_id = kpc.id
    WHERE kpc.id IS NULL;
    
    SET cleanup_count = cleanup_count + ROW_COUNT();
    
    SELECT 
        'Orphaned data cleaned successfully' as status,
        cleanup_count as total_cleanups,
        NOW() as cleanup_time;
END$$
DELIMITER ;

-- ====================================================================
-- 5. 综合数据健康检查
-- ====================================================================

-- 5.1 综合健康检查主程序
DELIMITER $$
CREATE PROCEDURE PerformDataHealthCheck()
BEGIN
    DECLARE health_score DECIMAL(5,2) DEFAULT 100.0;
    DECLARE fk_status VARCHAR(10);
    DECLARE unique_status VARCHAR(10);
    DECLARE consistency_status VARCHAR(10);
    DECLARE logic_status VARCHAR(10);
    DECLARE quality_status VARCHAR(20);
    
    -- 创建健康检查报告表
    DROP TEMPORARY TABLE IF EXISTS temp_health_report;
    CREATE TEMPORARY TABLE temp_health_report (
        check_category VARCHAR(50),
        check_name VARCHAR(100),
        status VARCHAR(20),
        score DECIMAL(5,2),
        details TEXT
    );
    
    SELECT '开始数据健康检查...' as message;
    
    -- 1. 外键完整性检查
    CALL CheckForeignKeyIntegrity();
    
    -- 2. 唯一约束检查
    CALL CheckUniqueConstraintIntegrity();
    
    -- 3. 统计一致性检查
    CALL CheckStatisticsConsistency();
    
    -- 4. 业务逻辑检查
    CALL CheckBusinessLogicConsistency();
    
    -- 5. 数据质量检查
    CALL CheckDataQuality();
    
    -- 计算总体健康分数
    SELECT 
        CASE 
            WHEN health_score >= 95 THEN 'EXCELLENT'
            WHEN health_score >= 85 THEN 'GOOD'
            WHEN health_score >= 70 THEN 'FAIR'
            WHEN health_score >= 50 THEN 'POOR'
            ELSE 'CRITICAL'
        END as overall_health,
        health_score as health_score,
        NOW() as check_time;
    
    SELECT '数据健康检查完成!' as message;
END$$
DELIMITER ;

-- ====================================================================
-- 6. 定期维护任务
-- ====================================================================

-- 6.1 每日维护任务
DELIMITER $$
CREATE PROCEDURE DailyMaintenanceTasks()
BEGIN
    SELECT 'Starting daily maintenance tasks...' as message;
    
    -- 清理过期令牌
    DELETE FROM user_tokens 
    WHERE expires_at < NOW() - INTERVAL 1 DAY;
    
    -- 更新统计数据
    CALL RepairStatisticsInconsistency();
    
    -- 清理临时复习队列
    DELETE FROM user_review_queues 
    WHERE scheduled_at < NOW() - INTERVAL 7 DAY 
    AND is_completed = TRUE;
    
    -- 更新复习队列元数据
    DELETE FROM review_queue_metadata 
    WHERE queue_date < CURDATE() - INTERVAL 30 DAY;
    
    SELECT 'Daily maintenance completed!' as message;
END$$
DELIMITER ;

-- 6.2 每周维护任务
DELIMITER $$
CREATE PROCEDURE WeeklyMaintenanceTasks()
BEGIN
    SELECT 'Starting weekly maintenance tasks...' as message;
    
    -- 清理孤立数据
    CALL CleanOrphanedData();
    
    -- 执行数据健康检查
    CALL PerformDataHealthCheck();
    
    -- 优化表（可选，根据数据量决定）
    -- OPTIMIZE TABLE users, knowledge_bases, knowledge_point_contents;
    
    SELECT 'Weekly maintenance completed!' as message;
END$$
DELIMITER ;

-- ====================================================================
-- 7. 创建数据完整性监控视图
-- ====================================================================

-- 7.1 数据完整性监控视图
CREATE VIEW v_data_integrity_monitor AS
SELECT 
    'Foreign Key Violations' as check_type,
    COUNT(*) as issue_count,
    'Check foreign key constraints' as description,
    NOW() as last_check
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'memorin_db'
AND REFERENCED_TABLE_NAME IS NOT NULL

UNION ALL

SELECT 
    'Statistics Inconsistency' as check_type,
    (
        SELECT COUNT(*) FROM knowledge_bases kb
        LEFT JOIN (
            SELECT knowledge_base_id, COUNT(*) as cnt
            FROM knowledge_base_content_relations
            GROUP BY knowledge_base_id
        ) actual_count ON kb.id = actual_count.knowledge_base_id
        WHERE COALESCE(actual_count.cnt, 0) != kb.content_count
    ) as issue_count,
    'Knowledge base content count mismatch' as description,
    NOW() as last_check

UNION ALL

SELECT 
    'Quality Issues' as check_type,
    (
        SELECT COUNT(*) FROM knowledge_point_contents 
        WHERE LENGTH(question) < 10 OR LENGTH(answer) < 2
    ) as issue_count,
    'Content quality problems' as description,
    NOW() as last_check;

-- 7.2 系统健康状态视图
CREATE VIEW v_system_health_status AS
SELECT 
    'Database Size' as metric,
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) as value,
    'MB' as unit,
    'INFO' as severity
FROM information_schema.TABLES 
WHERE table_schema = 'memorin_db'

UNION ALL

SELECT 
    'Total Users' as metric,
    COUNT(*) as value,
    'users' as unit,
    CASE 
        WHEN COUNT(*) > 1000 THEN 'INFO'
        WHEN COUNT(*) > 100 THEN 'LOW'
        ELSE 'MEDIUM'
    END as severity
FROM users

UNION ALL

SELECT 
    'Active Knowledge Bases' as metric,
    COUNT(*) as value,
    'bases' as unit,
    'INFO' as severity
FROM knowledge_bases 
WHERE status = 'active'

UNION ALL

SELECT 
    'Total Knowledge Points' as metric,
    COUNT(*) as value,
    'points' as unit,
    'INFO' as severity
FROM knowledge_point_contents;

-- ====================================================================
-- 8. 创建完整性检查事件调度器（可选）
-- ====================================================================

-- 注意：需要启用事件调度器: SET GLOBAL event_scheduler = ON;

-- 每日维护事件
-- CREATE EVENT IF NOT EXISTS daily_maintenance
-- ON SCHEDULE EVERY 1 DAY
-- STARTS '2025-01-09 02:00:00'
-- DO
--   CALL DailyMaintenanceTasks();

-- 每周维护事件
-- CREATE EVENT IF NOT EXISTS weekly_maintenance
-- ON SCHEDULE EVERY 1 WEEK
-- STARTS '2025-01-12 03:00:00'
-- DO
--   CALL WeeklyMaintenanceTasks();

-- ====================================================================
-- 完整性验证脚本结束
-- ====================================================================

-- 初始健康检查（创建脚本后立即执行）
SELECT 'Data integrity validation system installed successfully!' as status;
SELECT 'Run "CALL PerformDataHealthCheck();" to perform initial health check.' as next_step; 