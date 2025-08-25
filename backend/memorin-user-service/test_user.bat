@echo off
echo 正在测试用户实体类功能...
echo.

echo 1. 测试数据库连接
curl -s http://localhost:8081/test/database/test
echo.
echo.

echo 2. 创建测试用户
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test@memorin.com\",\"password\":\"password123\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 3. 获取所有用户
curl -s http://localhost:8081/test/users
echo.
echo.

echo 4. 根据用户名查询用户
curl -s http://localhost:8081/test/user/username/testuser
echo.
echo.

echo 5. 创建第二个用户
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"email\":\"admin@memorin.com\",\"password\":\"admin123\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 6. 再次获取所有用户
curl -s http://localhost:8081/test/users
echo.
echo.

echo 7. 重复用户名测试（应该失败）
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test2@memorin.com\",\"password\":\"123456\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 测试完成！
pause 