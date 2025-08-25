# 复习API测试脚本
# 使用方法: 在PowerShell中运行此脚本
# 前提条件: 确保复习服务(8083)和知识点服务(8082)正在运行

Write-Host "=== 复习API完整测试脚本 ===" -ForegroundColor Green
Write-Host "测试时间: $(Get-Date)" -ForegroundColor Gray
Write-Host ""

# 全局变量
$baseUrl = "http://localhost:8083"
$sessionId = $null

# 测试函数
function Test-API {
    param(
        [string]$TestName,
        [string]$Method,
        [string]$Url,
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{"Content-Type" = "application/json"}
    )
    
    Write-Host "测试: $TestName" -ForegroundColor Yellow
    Write-Host "URL: $Method $Url" -ForegroundColor Gray
    
    try {
        $startTime = Get-Date
        
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10
            Write-Host "请求体:" -ForegroundColor Gray
            Write-Host $jsonBody -ForegroundColor DarkGray
            
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Body $jsonBody -Headers $Headers
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $Headers
        }
        
        $duration = ((Get-Date) - $startTime).TotalMilliseconds
        Write-Host "✅ 成功 (${duration}ms)" -ForegroundColor Green
        
        if ($response) {
            Write-Host "响应数据:" -ForegroundColor Gray
            $response | ConvertTo-Json -Depth 3 | Write-Host -ForegroundColor DarkGray
        }
        
        return $response
    }
    catch {
        $duration = ((Get-Date) - $startTime).TotalMilliseconds
        Write-Host "❌ 失败 (${duration}ms)" -ForegroundColor Red
        Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
        
        if ($_.Exception.Response) {
            Write-Host "HTTP状态: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $responseBody = $reader.ReadToEnd()
                Write-Host "响应内容: $responseBody" -ForegroundColor Red
            } catch {
                Write-Host "无法读取响应内容" -ForegroundColor Red
            }
        }
        
        return $null
    }
    finally {
        Write-Host "" # 空行分隔
    }
}

# 1. 测试创建会话
Write-Host "=== 1. 测试创建复习会话 ===" -ForegroundColor Cyan
$createSessionBody = @{
    reviewMode = "daily"
    knowledgeBaseId = "TEST_BASE_001"
    targetQuestionCount = 3
    onlyDueQuestions = $false
    autoStart = $false
}

$session = Test-API -TestName "创建复习会话" -Method "POST" -Url "$baseUrl/api/review/sessions" -Body $createSessionBody

if ($session -and $session.data) {
    $sessionId = $session.data.id
    Write-Host "✓ 保存会话ID: $sessionId" -ForegroundColor Green
    Write-Host "✓ 用户ID: $($session.data.userId)" -ForegroundColor Green
    Write-Host "✓ 题目列表: $($session.data.questionList)" -ForegroundColor Green
} else {
    Write-Host "❌ 无法获取会话ID，后续测试将跳过" -ForegroundColor Red
    exit
}

# 2. 测试开始会话
Write-Host "=== 2. 测试开始复习会话 ===" -ForegroundColor Cyan
$startResult = Test-API -TestName "开始复习会话" -Method "PUT" -Url "$baseUrl/api/review/sessions/$sessionId/start"

# 3. 测试提交答案 - 多种情况
Write-Host "=== 3. 测试提交答案API ===" -ForegroundColor Cyan

# 3.1 使用会话中的知识点ID
Write-Host "--- 3.1 使用会话中的知识点ID (KP_DEMO_001) ---" -ForegroundColor Magenta
$submitBody1 = @{
    sessionId = $sessionId
    knowledgePointId = "KP_DEMO_001"
    questionIndex = 0
    userAnswer = "test answer"
    isCorrect = $true
    qualityRating = 4
    timeSpentSeconds = 30
    mistakeReason = ""
    studyNotes = ""
    perceivedDifficulty = 3
    isSkipped = $false
    submissionType = "manual"
    questionType = "text"
}

Test-API -TestName "提交答案(使用KP_DEMO_001)" -Method "POST" -Url "$baseUrl/api/review/sessions/$sessionId/submit" -Body $submitBody1

# 3.2 使用真实知识点ID
Write-Host "--- 3.2 使用真实知识点ID (KP_TEXT_20250819_002) ---" -ForegroundColor Magenta
$submitBody2 = @{
    sessionId = $sessionId
    knowledgePointId = "KP_TEXT_20250819_002"
    questionIndex = 0
    userAnswer = "函数的极限是当自变量趋近某个值时，函数值趋近的值"
    isCorrect = $true
    qualityRating = 5
    timeSpentSeconds = 45
    mistakeReason = ""
    studyNotes = ""
    perceivedDifficulty = 3
    isSkipped = $false
    submissionType = "manual"
    questionType = "text"
}

Test-API -TestName "提交答案(使用真实知识点)" -Method "POST" -Url "$baseUrl/api/review/sessions/$sessionId/submit" -Body $submitBody2

# 3.3 最小化请求体测试
Write-Host "--- 3.3 最小化请求体测试 ---" -ForegroundColor Magenta
$submitBody3 = @{
    sessionId = $sessionId
    knowledgePointId = "KP_DEMO_001"
    questionIndex = 0
    userAnswer = "test"
    isCorrect = $true
    qualityRating = 3
    timeSpentSeconds = 10
}

Test-API -TestName "提交答案(最小化参数)" -Method "POST" -Url "$baseUrl/api/review/sessions/$sessionId/submit" -Body $submitBody3

# 4. 测试获取错题统计
Write-Host "=== 4. 测试获取错题统计 ===" -ForegroundColor Cyan
Test-API -TestName "获取错题统计" -Method "GET" -Url "$baseUrl/api/review/mistakes/user/USER_001/statistics"

# 5. 测试获取会话列表
Write-Host "=== 5. 测试获取会话列表 ===" -ForegroundColor Cyan
Test-API -TestName "获取会话列表" -Method "GET" -Url "$baseUrl/api/review/sessions"

# 6. 检查知识点服务数据
Write-Host "=== 6. 检查知识点服务数据 ===" -ForegroundColor Cyan
try {
    $knowledgeResult = Invoke-RestMethod -Uri "http://localhost:8082/api/test/knowledge" -Method Get
    Write-Host "✓ 知识点服务连接正常" -ForegroundColor Green
    Write-Host "知识点数量: $($knowledgeResult.data.Count)" -ForegroundColor Green
    if ($knowledgeResult.data.Count -gt 0) {
        Write-Host "现有知识点:" -ForegroundColor Gray
        $knowledgeResult.data | ForEach-Object {
            Write-Host "  - ID: $($_.id), 用户: $($_.userId), 问题: $($_.question)" -ForegroundColor DarkGray
        }
    }
} catch {
    Write-Host "❌ 知识点服务连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Green
Write-Host "完成时间: $(Get-Date)" -ForegroundColor Gray
Write-Host ""
Write-Host "📋 测试总结:" -ForegroundColor Yellow
Write-Host "1. 如果创建会话、开始会话、获取统计都成功，说明基础功能正常" -ForegroundColor White
Write-Host "2. 如果提交答案失败，请检查:" -ForegroundColor White
Write-Host "   - SubmitAnswerRequest DTO的字段验证" -ForegroundColor White
Write-Host "   - 知识点存在性验证逻辑" -ForegroundColor White
Write-Host "   - 会话状态和关联关系验证" -ForegroundColor White
Write-Host "3. 查看后端日志以获取详细错误信息" -ForegroundColor White 