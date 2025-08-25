# Memorin智能知识复习系统 - 开发环境微服务启动脚本
# 更新时间: 2025年1月17日
# 运行方式: 在PowerShell中执行 .\启动微服务_开发环境.ps1

Write-Host "🎯 Memorin微服务启动脚本" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor DarkCyan
Write-Host ""

# 检查当前目录
Write-Host "📍 当前工作目录:" -ForegroundColor Yellow
Get-Location
Write-Host ""

# Step 1: Nacos服务注册中心 (如果还未启动)
Write-Host "🏥 Step 1: Nacos服务注册中心 (8848)" -ForegroundColor Green
Write-Host "📝 命令: infrastructure\nacos\bin\startup.cmd -m standalone" -ForegroundColor Gray
Write-Host "🌐 访问: http://localhost:8848/nacos (nacos/nacos)" -ForegroundColor Gray
Write-Host "✅ Nacos通常已在运行中" -ForegroundColor Green
Write-Host ""

# Step 2: 用户认证服务
Write-Host "👤 Step 2: 用户认证服务 (8081)" -ForegroundColor Green
Write-Host "📝 启动命令:" -ForegroundColor Yellow
Write-Host "   cd Memorin-rebuild\backend\memorin-user-service" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run `"-Dspring-boot.run.profiles=dev`"" -ForegroundColor Cyan
Write-Host "🌐 访问: http://localhost:8081/swagger-ui.html" -ForegroundColor Gray
Write-Host ""

# Step 3: 知识库管理服务
Write-Host "📚 Step 3: 知识库管理服务 (8082)" -ForegroundColor Green
Write-Host "📝 启动命令:" -ForegroundColor Yellow
Write-Host "   cd Memorin-rebuild\backend\memorin-knowledge-service" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run `"-Dspring-boot.run.profiles=dev`"" -ForegroundColor Cyan
Write-Host "🌐 访问: http://localhost:8082/swagger-ui.html" -ForegroundColor Gray
Write-Host ""

# Step 4: 智能复习服务
Write-Host "🧠 Step 4: 智能复习服务 (8083)" -ForegroundColor Green
Write-Host "📝 启动命令:" -ForegroundColor Yellow
Write-Host "   cd Memorin-rebuild\backend\memorin-review-service" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run `"-Dspring-boot.run.profiles=dev`"" -ForegroundColor Cyan
Write-Host "🌐 访问: http://localhost:8083/swagger-ui.html" -ForegroundColor Gray
Write-Host ""

# Step 5: 统计分析服务
Write-Host "📊 Step 5: 统计分析服务 (8084)" -ForegroundColor Green
Write-Host "📝 启动命令:" -ForegroundColor Yellow
Write-Host "   cd Memorin-rebuild\backend\memorin-statistics-service" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run `"-Dspring-boot.run.profiles=dev`"" -ForegroundColor Cyan
Write-Host "🌐 访问: http://localhost:8084/swagger-ui.html" -ForegroundColor Gray
Write-Host ""

# Step 6: API网关
Write-Host "🌐 Step 6: API网关 (8080)" -ForegroundColor Green
Write-Host "📝 启动命令:" -ForegroundColor Yellow
Write-Host "   cd Memorin-rebuild\backend\memorin-gateway" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run `"-Dspring-boot.run.profiles=dev`"" -ForegroundColor Cyan
Write-Host "🌐 访问: http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
Write-Host ""

Write-Host "=================================================" -ForegroundColor DarkCyan
Write-Host "🎊 重要提示:" -ForegroundColor Yellow
Write-Host "1. 每个服务需要在新的PowerShell窗口中启动" -ForegroundColor Gray
Write-Host "2. 启动顺序: Nacos → 业务服务 → 网关" -ForegroundColor Gray
Write-Host "3. 等待每个服务完全启动后再启动下一个" -ForegroundColor Gray
Write-Host "4. 服务启动成功标志: 看到 'Started xxxApplication'" -ForegroundColor Gray
Write-Host ""

Write-Host "🔗 API文档聚合中心:" -ForegroundColor Cyan
Write-Host "   📋 聚合API: http://localhost:8080/api-docs" -ForegroundColor Cyan
Write-Host "   🎨 可视化: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""

Write-Host "🚀 开始启动微服务！" -ForegroundColor Green

# 检查端口占用情况
Write-Host ""
Write-Host "🔍 检查关键端口占用情况:" -ForegroundColor Yellow
$ports = @(8080, 8081, 8082, 8083, 8084, 8848)
foreach ($port in $ports) {
    $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($connection) {
        Write-Host "✅ 端口 $port 已被使用" -ForegroundColor Green
    } else {
        Write-Host "⭕ 端口 $port 空闲" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "💡 快速启动提示:" -ForegroundColor Cyan
Write-Host "如果需要重启所有服务，可以使用以下命令检查并停止进程:" -ForegroundColor Gray
Write-Host "Get-Process -Name java | Stop-Process -Force" -ForegroundColor Red
Write-Host "" 