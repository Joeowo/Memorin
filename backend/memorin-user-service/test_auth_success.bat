@echo off
echo 认证功能成功测试...
echo.

echo 1. 注册新用户...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"successuser\",\"email\":\"success@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 使用正确密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"successuser\",\"password\":\"password123\"}"
echo.
echo.

echo 测试完成！
pause 