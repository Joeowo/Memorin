#!/bin/bash

# Memorin 全模块集成测试脚本
# 测试 user、knowledge、review 三个模块的完整集成

echo "🚀 开始 Memorin 全模块集成测试"
echo "=================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 服务地址
USER_SERVICE="http://localhost:8081"
KNOWLEDGE_SERVICE="http://localhost:8082"
REVIEW_SERVICE="http://localhost:8083"

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 测试函数
run_test() {
    local test_name=$1
    local test_command=$2
    
    echo -e "\n📋 测试: ${YELLOW}$test_name${NC}"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    if eval "$test_command"; then
        echo -e "✅ ${GREEN}PASSED${NC}: $test_name"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        return 0
    else
        echo -e "❌ ${RED}FAILED${NC}: $test_name"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi
}

# 检查服务健康状态
check_service_health() {
    local service_name=$1
    local service_url=$2
    
    echo -e "\n🔍 检查 ${YELLOW}$service_name${NC} 服务状态..."
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$service_url/actuator/health")
    if [ "$response" = "200" ]; then
        echo -e "✅ ${GREEN}$service_name${NC} 服务运行正常"
        return 0
    else
        echo -e "❌ ${RED}$service_name${NC} 服务异常 (HTTP $response)"
        return 1
    fi
}

# 测试场景1: 用户注册和认证
test_user_registration() {
    echo -e "\n👤 测试用户注册和认证..."
    
    # 注册用户
    register_response=$(curl -s -X POST "$USER_SERVICE/api/auth/register" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "integration_test_user",
            "email": "integration@test.com",
            "password": "TestPass123"
        }')
    
    user_id=$(echo "$register_response" | jq -r '.data.id')
    if [ "$user_id" != "null" ] && [ "$user_id" != "" ]; then
        echo "✅ 用户注册成功: $user_id"
        
        # 用户登录
        login_response=$(curl -s -X POST "$USER_SERVICE/api/auth/login" \
            -H "Content-Type: application/json" \
            -d '{
                "username": "integration_test_user",
                "password": "TestPass123"
            }')
        
        token=$(echo "$login_response" | jq -r '.data.token')
        if [ "$token" != "null" ] && [ "$token" != "" ]; then
            echo "✅ 用户登录成功"
            echo "USER_ID=$user_id" > /tmp/test_user.env
            echo "TOKEN=$token" >> /tmp/test_user.env
            return 0
        fi
    fi
    
    return 1
}

# 测试场景2: 知识模块集成
test_knowledge_integration() {
    echo -e "\n📚 测试知识模块集成..."
    
    # 加载用户ID
    source /tmp/test_user.env
    
    # 创建学习分类
    category_response=$(curl -s -X POST "$KNOWLEDGE_SERVICE/api/test/categories" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "JavaScript基础",
            "description": "JavaScript核心概念复习",
            "color": "#f7df1e",
            "icon": "📜"
        }')
    
    category_id=$(echo "$category_response" | jq -r '.data.id')
    if [ "$category_id" != "null" ] && [ "$category_id" != "" ]; then
        echo "✅ 创建分类成功: $category_id"
        
        # 创建知识点
        knowledge_response=$(curl -s -X POST "$KNOWLEDGE_SERVICE/api/test/knowledge" \
            -H "Content-Type: application/json" \
            -d '{
                "question": "什么是JavaScript闭包？",
                "explanation": "闭包是指有权访问另一个函数作用域中变量的函数",
                "type": "text",
                "categoryId": "'$category_id'",
                "tags": ["JavaScript", "闭包"],
                "difficulty": 3,
                "estimatedTime": 15
            }')
        
        knowledge_id=$(echo "$knowledge_response" | jq -r '.data.id')
        if [ "$knowledge_id" != "null" ] && [ "$knowledge_id" != "" ]; then
            echo "✅ 创建知识点成功: $knowledge_id"
            echo "CATEGORY_ID=$category_id" > /tmp/test_knowledge.env
            echo "KNOWLEDGE_ID=$knowledge_id" >> /tmp/test_knowledge.env
            return 0
        fi
    fi
    
    return 1
}

# 测试场景3: 复习模块集成
test_review_integration() {
    echo -e "\n🎯 测试复习模块集成..."
    
    # 加载测试数据
    source /tmp/test_user.env
    source /tmp/test_knowledge.env
    
    # 创建复习会话
    session_response=$(curl -s -X POST "$REVIEW_SERVICE/api/review/sessions" \
        -H "Content-Type: application/json" \
        -H "User-Id: $USER_ID" \
        -d '{
            "reviewMode": "SM2",
            "targetQuestionCount": 5,
            "maxDurationMinutes": 30,
            "notes": "JavaScript基础复习"
        }')
    
    session_id=$(echo "$session_response" | jq -r '.data.id')
    if [ "$session_id" != "null" ] && [ "$session_id" != "" ]; then
        echo "✅ 创建复习会话成功: $session_id"
        
        # 启动会话
        start_response=$(curl -s -X PUT "$REVIEW_SERVICE/api/review/sessions/$session_id/start" \
            -H "User-Id: $USER_ID")
        
        if echo "$start_response" | jq -r '.success' | grep -q "true"; then
            echo "✅ 启动复习会话成功"
            
            # 记录错题
            mistake_response=$(curl -s -X POST "$REVIEW_SERVICE/api/review/mistakes" \
                -H "Content-Type: application/json" \
                -d '{
                    "userId": "'$USER_ID'",
                    "knowledgePointId": "'$KNOWLEDGE_ID'",
                    "mistakeReason": "概念理解错误",
                    "questionType": "text",
                    "difficultyLevel": "medium"
                }')
            
            if echo "$mistake_response" | jq -r '.success' | grep -q "true"; then
                echo "✅ 记录错题成功"
                echo "SESSION_ID=$session_id" > /tmp/test_review.env
                return 0
            fi
        fi
    fi
    
    return 1
}

# 测试场景4: 完整学习工作流
test_complete_workflow() {
    echo -e "\n📖 测试完整学习工作流..."
    
    source /tmp/test_user.env
    source /tmp/test_review.env
    
    # 模拟答题过程
    for i in {1..3}; do
        answer_response=$(curl -s -X POST "$REVIEW_SERVICE/api/review/sessions/$SESSION_ID/submit" \
            -H "Content-Type: application/json" \
            -H "User-Id: $USER_ID" \
            -d '{
                "knowledgePointId": "test_knowledge_'$i'",
                "qualityRating": '$((i % 4 + 1))',
                "timeSpentSeconds": '$((i * 30 + 20))',
                "isSkipped": false,
                "isCorrect": '$([ $((i % 2)) -eq 0 ] && echo "true" || echo "false")',
                "studyNotes": "第'$i'题答题记录"
            }')
        
        if echo "$answer_response" | jq -r '.success' | grep -q "true"; then
            echo "✅ 提交第 $i 题答案成功"
        else
            echo "❌ 提交第 $i 题答案失败"
            return 1
        fi
    done
    
    # 完成会话
    complete_response=$(curl -s -X PUT "$REVIEW_SERVICE/api/review/sessions/$SESSION_ID/complete" \
        -H "User-Id: $USER_ID")
    
    if echo "$complete_response" | jq -r '.success' | grep -q "true"; then
        echo "✅ 完成复习会话成功"
        return 0
    fi
    
    return 1
}

# 测试场景5: 错题分析和统计
test_mistake_analysis() {
    echo -e "\n📊 测试错题分析和统计..."
    
    source /tmp/test_user.env
    
    # 获取错题统计
    stats_response=$(curl -s "$REVIEW_SERVICE/api/review/mistakes/user/$USER_ID/statistics")
    if echo "$stats_response" | jq -r '.success' | grep -q "true"; then
        total_mistakes=$(echo "$stats_response" | jq -r '.data.totalMistakes')
        echo "✅ 错题统计获取成功: 总错题数=$total_mistakes"
        
        # 获取未解决错题
        unresolved_response=$(curl -s "$REVIEW_SERVICE/api/review/mistakes/user/$USER_ID/unresolved")
        unresolved_count=$(echo "$unresolved_response" | jq -r '.data | length')
        echo "✅ 未解决错题获取成功: 数量=$unresolved_count"
        
        return 0
    fi
    
    return 1
}

# 运行所有测试
echo "🧪 开始集成测试..."

# 检查服务健康状态
run_test "检查用户服务健康状态" "check_service_health '用户服务' '$USER_SERVICE'"
run_test "检查知识服务健康状态" "check_service_health '知识服务' '$KNOWLEDGE_SERVICE'"
run_test "检查复习服务健康状态" "check_service_health '复习服务' '$REVIEW_SERVICE'"

# 运行测试场景
run_test "用户注册和认证" "test_user_registration"
run_test "知识模块集成" "test_knowledge_integration"
run_test "复习模块集成" "test_review_integration"
run_test "完整学习工作流" "test_complete_workflow"
run_test "错题分析和统计" "test_mistake_analysis"

# 显示测试结果
echo -e "\n=================================="
echo -e "🎯 集成测试完成！"
echo -e "总测试数: $TOTAL_TESTS"
echo -e "通过测试: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败测试: ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "🎉 ${GREEN}所有测试通过！系统运行正常${NC}"
    exit 0
else
    echo -e "⚠️  ${YELLOW}部分测试失败，请检查日志${NC}"
    exit 1
fi