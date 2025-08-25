@echo off
echo 测试用户服务数据验证和错误处理功能...
echo.

echo 1. 测试空用户名登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 测试空密码登录...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"\"}"
echo.
echo.

echo 3. 测试用户名长度不足...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"ab\",\"password\":\"password123\"}"
echo.
echo.

echo 4. 测试用户名包含特殊字符...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"test@user\",\"password\":\"password123\"}"
echo.
echo.

echo 5. 测试密码长度不足...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"123\"}"
echo.
echo.

echo 6. 测试密码复杂度不足...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password\"}"
echo.
echo.

echo 7. 测试注册时邮箱格式错误...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"newuser\",\"email\":\"invalid-email\",\"password\":\"Password123\"}"
echo.
echo.

echo 8. 测试注册时用户名已存在...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"new@example.com\",\"password\":\"Password123\"}"
echo.
echo.

echo 9. 测试注册时邮箱已存在...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"newuser2\",\"email\":\"test@example.com\",\"password\":\"Password123\"}"
echo.
echo.

echo 10. 测试正确的注册请求...
curl -X POST http://localhost:8081/auth/register -H "Content-Type: application/json" -d "{\"username\":\"validuser\",\"email\":\"valid@example.com\",\"password\":\"Password123\"}"
echo.
echo.

echo 11. 测试正确的登录请求...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"validuser\",\"password\":\"Password123\"}"
echo.
echo.

echo 测试完成！
pause 