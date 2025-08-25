@echo off
echo 正在测试用户服务 - 包含密码加密功能...
echo.

echo 1. 测试数据库连接
curl -s http://localhost:8081/test/database/test
echo.
echo.

echo 2. 测试密码加密验证功能
curl -s -X POST -H "Content-Type: application/json" -d "{\"password\":\"testpassword123\"}" http://localhost:8081/test/password/verify
echo.
echo.

echo 3. 创建测试用户（密码将自动加密）
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test@memorin.com\",\"password\":\"password123\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 4. 获取所有用户（查看加密后的密码）
curl -s http://localhost:8081/test/users
echo.
echo.

echo 5. 根据用户名查询用户
curl -s http://localhost:8081/test/user/username/testuser
echo.
echo.

echo 6. 创建第二个用户（使用不同密码）
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"email\":\"admin@memorin.com\",\"password\":\"admin123456\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 7. 再次获取所有用户
curl -s http://localhost:8081/test/users
echo.
echo.

echo 8. 重复用户名测试（应该失败）
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test2@memorin.com\",\"password\":\"123456\"}" http://localhost:8081/test/user/create
echo.
echo.

echo 9. 再次测试密码验证（不同密码）
curl -s -X POST -H "Content-Type: application/json" -d "{\"password\":\"mySecurePassword2025\"}" http://localhost:8081/test/password/verify
echo.
echo.

echo 密码加密功能测试完成！
echo.
echo 验证要点：
echo - 每个用户的passwordHash字段应该是BCrypt加密的哈希值
echo - 相同明文密码生成的哈希值应该不同（每次加密都有随机盐）
echo - correctPasswordMatch应该为true，wrongPasswordMatch应该为false
echo.
pause 