@echo off
echo 完整测试网关功能...
echo.

echo 1. 测试网关健康检查...
curl -X GET http://localhost:8080/actuator/health
echo.
echo.

echo 2. 测试网关路由信息...
curl -X GET http://localhost:8080/actuator/gateway/routes
echo.
echo.

echo 3. 直接访问用户服务...
curl -X GET http://localhost:8081/test/ping
echo.
echo.

echo 4. 通过网关访问用户服务...
curl -X GET http://localhost:8080/api/test/ping
echo.
echo.

echo 5. 通过网关注册用户...
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"gatewaytest\",\"email\":\"gatewaytest@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 6. 通过网关登录用户...
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"gatewaytest\",\"password\":\"password123\"}"
echo.
echo.

echo 测试完成！
pause 