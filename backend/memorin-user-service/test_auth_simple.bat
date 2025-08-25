@echo off
echo 测试认证功能...
echo.

echo 1. 测试登录接口...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 测试注册接口...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"newuser\",\"email\":\"new@example.com\",\"password\":\"password123\"}"
echo.
echo.

pause 