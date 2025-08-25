@echo off
echo 最终认证功能测试...
echo.

echo 1. 注册新用户...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"finaluser\",\"email\":\"final@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 使用正确密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"finaluser\",\"password\":\"password123\"}"
echo.
echo.

echo 3. 使用错误密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"finaluser\",\"password\":\"wrongpassword\"}"
echo.
echo.

echo 测试完成！
pause 