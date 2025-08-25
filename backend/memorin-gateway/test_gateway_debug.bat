@echo off
echo 网关调试测试...
echo.

echo 1. 直接访问用户服务测试端点...
curl -X GET http://localhost:8081/test/ping
echo.
echo.

echo 2. 通过网关访问测试端点...
curl -X GET http://localhost:8080/api/test/ping
echo.
echo.

echo 3. 直接访问用户服务认证端点...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo 4. 通过网关访问认证端点...
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

pause 