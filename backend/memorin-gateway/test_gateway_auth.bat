@echo off
echo 测试通过网关访问用户服务认证功能...
echo.

echo 1. 测试网关健康检查...
curl -X GET http://localhost:8080/actuator/health
echo.
echo.

echo 2. 通过网关测试用户服务健康检查...
curl -X GET http://localhost:8080/api/test/ping
echo.
echo.

echo 3. 通过网关注册新用户...
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"gatewayuser\",\"email\":\"gateway@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo 4. 通过网关登录用户...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"gatewayuser\",\"password\":\"password123\"}"
echo.
echo.

echo 5. 通过网关测试错误密码登录...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"gatewayuser\",\"password\":\"wrongpassword\"}"
echo.
echo.

echo 6. 通过网关测试空用户名登录...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"\",\"password\":\"password123\"}"
echo.
echo.

echo 测试完成！
pause 