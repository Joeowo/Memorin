@echo off
echo 测试用户认证功能...
echo.

echo 1. 用户注册测试...
curl -X POST http://localhost:8081/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"authuser\",\"email\":\"auth@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 用户登录测试...
curl -X POST http://localhost:8081/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"authuser\",\"password\":\"password123\"}"
echo.
echo.

echo 3. 错误密码登录测试...
curl -X POST http://localhost:8081/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"authuser\",\"password\":\"wrongpassword\"}"
echo.
echo.

echo 4. 不存在的用户登录测试...
curl -X POST http://localhost:8081/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"nonexistent\",\"password\":\"password123\"}"
echo.
echo.

pause 