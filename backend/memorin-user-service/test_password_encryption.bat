@echo off
echo 测试密码加密功能...
echo.

echo 1. 创建用户测试...
curl -X POST http://localhost:8081/test/user/create ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 2. 查询用户信息...
curl -X GET http://localhost:8081/test/user/username/testuser
echo.
echo.

echo 3. 测试密码验证...
curl -X POST http://localhost:8081/test/password/verify ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo 4. 查看所有用户...
curl -X GET http://localhost:8081/test/users
echo.
echo.

pause 