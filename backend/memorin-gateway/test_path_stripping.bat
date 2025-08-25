@echo off
echo 测试网关路径剥离功能...
echo.

echo 1. 直接访问用户服务测试端点...
curl -X GET http://localhost:8081/test/ping
echo.
echo.

echo 2. 通过网关访问测试端点 (应该剥离 /api 前缀)...
curl -X GET http://localhost:8080/api/test/ping
echo.
echo.

echo 3. 直接访问用户服务认证端点...
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo 4. 通过网关访问认证端点 (应该剥离 /api 前缀)...
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo 5. 测试网关路由信息...
curl -X GET http://localhost:8080/actuator/gateway/routes
echo.
echo.

echo 6. 测试POST请求体解析...
curl -X POST http://localhost:8081/test/echo -H "Content-Type: application/json" -d "{\"test\":\"data\"}"
echo.
echo.

echo 7. 通过网关测试POST请求体解析...
curl -X POST http://localhost:8080/api/test/echo -H "Content-Type: application/json" -d "{\"test\":\"data\"}"
echo.
echo.

pause 