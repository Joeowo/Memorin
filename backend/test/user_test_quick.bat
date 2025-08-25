@echo off
chcp 65001 > nul
REM Windows用户服务API测试脚本
echo === 用户服务API测试开始 ===

REM 1. 健康检查
echo 1. 健康检查...
curl -s http://localhost:8081/test/ping
echo.

REM 2. 注册测试用户
echo 2. 注册测试用户...
curl -s -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"apitest\",\"email\":\"apitest@example.com\",\"password\":\"Test123456\"}"
echo.

REM 3. 登录测试
echo 3. 登录测试...
curl -s -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"apitest\",\"password\":\"Test123456\"}"
echo.

echo === 测试完成 ===
pause