@echo off
echo 完整测试用户认证功能...
echo.

echo 1. 注册新用户...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"authuser\",\"email\":\"auth@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 使用正确密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"authuser\",\"password\":\"password123\"}"
echo.
echo.

echo 3. 使用错误密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"authuser\",\"password\":\"wrongpassword\"}"
echo.
echo.

echo 4. 使用不存在的用户登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"nonexistent\",\"password\":\"password123\"}"
echo.
echo.

echo 5. 测试空用户名登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"\",\"password\":\"password123\"}"
echo.
echo.

echo 6. 测试空密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"authuser\",\"password\":\"\"}"
echo.
echo.

echo 测试完成！
pause 