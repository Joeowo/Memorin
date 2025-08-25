import axios from 'axios';

// SDK模拟实现
class UserServiceAPI {
    private baseURL: string;

    constructor(baseURL: string) {
        this.baseURL = baseURL;
    }

    async healthCheck(): Promise<boolean> {
        try {
            const response = await axios.get(`${this.baseURL}/actuator/health`);
            return response.status === 200;
        } catch (error) {
            return false;
        }
    }

    auth = {
        register: async (data: { username: string; email: string; password: string }) => {
            const response = await axios.post(`${this.baseURL}/api/auth/register`, data);
            return response.data.data;
        },

        login: async (data: { username: string; password: string }) => {
            const response = await axios.post(`${this.baseURL}/api/auth/login`, data);
            return response.data.data;
        },

        me: async () => {
            // 模拟获取用户信息
            return { id: 'USER_001', username: 'integration_test_user' };
        }
    };
}

class KnowledgeServiceAPI {
    private baseURL: string;

    constructor(baseURL: string) {
        this.baseURL = baseURL;
    }

    async healthCheck(): Promise<boolean> {
        try {
            const response = await axios.get(`${this.baseURL}/actuator/health`);
            return response.status === 200;
        } catch (error) {
            return false;
        }
    }

    categories = {
        create: async (data: { name: string; description: string; icon: string; color: string }) => {
            const response = await axios.post(`${this.baseURL}/api/test/categories`, data);
            return response.data.data;
        },

        getAll: async () => {
            const response = await axios.get(`${this.baseURL}/api/test/categories`);
            return response.data.data;
        }
    };

    knowledge = {
        create: async (data: any) => {
            const response = await axios.post(`${this.baseURL}/api/test/knowledge`, data);
            return response.data.data;
        },

        getAll: async () => {
            const response = await axios.get(`${this.baseURL}/api/test/knowledge`);
            return response.data.data;
        }
    };
}

class ReviewServiceAPI {
    private baseURL: string;

    constructor(baseURL: string) {
        this.baseURL = baseURL;
    }

    async healthCheck(): Promise<boolean> {
        try {
            const response = await axios.get(`${this.baseURL}/actuator/health`);
            return response.status === 200;
        } catch (error) {
            return false;
        }
    }

    sessions = {
        create: async (data: any, userId: string) => {
            const response = await axios.post(`${this.baseURL}/api/review/sessions`, data, {
                headers: { 'User-Id': userId }
            });
            return response.data.data;
        },

        start: async (sessionId: string, userId: string) => {
            const response = await axios.put(`${this.baseURL}/api/review/sessions/${sessionId}/start`, {}, {
                headers: { 'User-Id': userId }
            });
            return response.data;
        },

        submitAnswer: async (sessionId: string, answer: any, userId: string) => {
            const response = await axios.post(`${this.baseURL}/api/review/sessions/${sessionId}/submit`, answer, {
                headers: { 'User-Id': userId }
            });
            return response.data;
        },

        complete: async (sessionId: string, userId: string) => {
            const response = await axios.put(`${this.baseURL}/api/review/sessions/${sessionId}/complete`, {}, {
                headers: { 'User-Id': userId }
            });
            return response.data;
        },

        getAll: async (userId: string) => {
            const response = await axios.get(`${this.baseURL}/api/review/sessions`, {
                headers: { 'User-Id': userId }
            });
            return response.data.data;
        }
    };

    mistakes = {
        add: async (data: any) => {
            const response = await axios.post(`${this.baseURL}/api/review/mistakes`, data);
            return response.data.data;
        },

        getStatistics: async (userId: string) => {
            const response = await axios.get(`${this.baseURL}/api/review/mistakes/user/${userId}/statistics`);
            return response.data.data;
        },

        getAll: async (userId: string) => {
            const response = await axios.get(`${this.baseURL}/api/review/mistakes/user/${userId}`);
            return response.data.data;
        },

        getUnresolved: async (userId: string) => {
            const response = await axios.get(`${this.baseURL}/api/review/mistakes/user/${userId}/unresolved`);
            return response.data.data;
        },

        resolve: async (userId: string, knowledgePointId: string) => {
            const response = await axios.put(`${this.baseURL}/api/review/mistakes/${userId}/${knowledgePointId}/resolve`);
            return response.data;
        }
    };

    generator = {
        generateSmartReview: async (data: any) => {
            const response = await axios.post(`${this.baseURL}/api/review/question-generator/templates/smart-review`, data);
            return response.data.data;
        },

        generateMistakeReview: async (data: any) => {
            const response = await axios.post(`${this.baseURL}/api/review/question-generator/templates/mistake-review/javascript_base`, data);
            return response.data.data;
        }
    };
}

// 测试配置
const config = {
    userService: 'http://localhost:8081',
    knowledgeService: 'http://localhost:8082',
    reviewService: 'http://localhost:8083'
};

// 测试结果
interface TestResult {
    name: string;
    passed: boolean;
    message?: string;
    data?: any;
}

class IntegrationTester {
    private userAPI: UserServiceAPI;
    private knowledgeAPI: KnowledgeServiceAPI;
    private reviewAPI: ReviewServiceAPI;
    private testUser: any = null;
    private authToken: string = '';
    private testCategory: any = null;
    private knowledgePoints: any[] = [];
    private reviewSession: any = null;

    constructor() {
        this.userAPI = new UserServiceAPI(config.userService);
        this.knowledgeAPI = new KnowledgeServiceAPI(config.knowledgeService);
        this.reviewAPI = new ReviewServiceAPI(config.reviewService);
    }

    async runAllTests(): Promise<void> {
        console.log('🚀 开始全模块集成测试（SDK版本）');
        console.log('='.repeat(50));

        const results: TestResult[] = [];

        try {
            // 步骤1: 服务检查
            results.push(await this.checkAllServices());
            
            if (results.every(r => r.passed)) {
                // 步骤2: 执行测试场景
                results.push(await this.testUserRegistration());
                results.push(await this.createLearningSystem());
                results.push(await this.createReviewSession());
                results.push(await this.simulateLearningProcess());
                results.push(await this.analyzeAndReviewMistakes());
                results.push(await this.analyzeLearningProgress());
                
                // 步骤3: 验证结果
                await this.validateIntegration();
            }

            this.printResults(results);

        } catch (error) {
            console.error('❌ 测试执行失败:', error);
        }
    }

    private async checkAllServices(): Promise<TestResult> {
        console.log('🔍 检查所有服务状态...');
        
        const userHealthy = await this.userAPI.healthCheck();
        const knowledgeHealthy = await this.knowledgeAPI.healthCheck();
        const reviewHealthy = await this.reviewAPI.healthCheck();
        
        console.log(`用户服务: ${userHealthy ? '✅' : '❌'}`);
        console.log(`知识服务: ${knowledgeHealthy ? '✅' : '❌'}`);
        console.log(`复习服务: ${reviewHealthy ? '✅' : '❌'}`);
        
        return {
            name: '服务健康检查',
            passed: userHealthy && knowledgeHealthy && reviewHealthy,
            message: '所有服务运行正常'
        };
    }

    private async testUserRegistration(): Promise<TestResult> {
        console.log('\n👤 步骤1: 用户注册与认证');
        
        try {
            // 用户注册
            const registerResponse = await this.userAPI.auth.register({
                username: 'integration_learner',
                email: 'learner@integration.test',
                password: 'TestPass123'
            });
            
            this.testUser = registerResponse;
            console.log(`✅ 用户注册成功: ${this.testUser.id}`);
            
            // 用户登录
            const loginResponse = await this.userAPI.auth.login({
                username: 'integration_learner',
                password: 'TestPass123'
            });
            
            this.authToken = loginResponse.token;
            console.log(`✅ 用户登录成功，Token: ${this.authToken.substring(0, 20)}...`);
            
            return { name: '用户注册和认证', passed: true };
            
        } catch (error: any) {
            return { name: '用户注册和认证', passed: false, message: error.message };
        }
    }

    private async createLearningSystem(): Promise<TestResult> {
        console.log('\n📚 步骤2: 创建学习体系');
        
        try {
            // 创建学习分类
            this.testCategory = await this.knowledgeAPI.categories.create({
                name: '前端开发进阶',
                description: '现代前端开发核心概念与最佳实践',
                icon: '🚀',
                color: '#61dafb'
            });
            console.log(`✅ 创建分类: ${this.testCategory.name} (${this.testCategory.id})`);
            
            // 创建子分类
            const jsCategory = await this.knowledgeAPI.categories.create({
                name: 'JavaScript核心',
                parentName: '前端开发进阶',
                description: 'JavaScript语言核心概念'
            });
            console.log(`✅ 创建子分类: ${jsCategory.name}`);
            
            // 批量创建知识点
            const knowledgeData = [
                {
                    question: '什么是JavaScript的闭包？',
                    explanation: '闭包是指有权访问另一个函数作用域中变量的函数，通过函数嵌套和变量引用来实现',
                    type: 'text',
                    categoryName: 'JavaScript核心',
                    tags: ['JavaScript', '闭包', '作用域'],
                    difficulty: 3,
                    estimatedTime: 20
                },
                {
                    question: '解释JavaScript的事件循环机制',
                    explanation: '事件循环是JavaScript处理异步操作的机制，包括调用栈、消息队列和微任务队列',
                    type: 'text',
                    categoryName: 'JavaScript核心',
                    tags: ['JavaScript', '异步', '事件循环'],
                    difficulty: 4,
                    estimatedTime: 25
                },
                {
                    question: 'var、let、const有什么区别？',
                    explanation: 'var有函数作用域和变量提升，let和const有块级作用域，const声明后不可重新赋值',
                    type: 'choice',
                    categoryName: 'JavaScript核心',
                    tags: ['JavaScript', '变量声明', '作用域'],
                    difficulty: 2,
                    estimatedTime: 15
                },
                {
                    question: '如何实现一个简单的Promise？',
                    explanation: 'Promise是一个代表异步操作最终完成或失败的对象，通过状态机和回调函数实现',
                    type: 'code',
                    categoryName: 'JavaScript核心',
                    tags: ['JavaScript', 'Promise', '异步'],
                    difficulty: 4,
                    estimatedTime: 30
                }
            ];
            
            this.knowledgePoints = [];
            for (const data of knowledgeData) {
                const knowledge = await this.knowledgeAPI.knowledge.create(data);
                this.knowledgePoints.push(knowledge);
                console.log(`  ✅ 创建知识点: ${knowledge.question.substring(0, 30)}...`);
            }
            
            return { name: '创建学习体系', passed: true };
            
        } catch (error: any) {
            return { name: '创建学习体系', passed: false, message: error.message };
        }
    }

    private async createReviewSession(): Promise<TestResult> {
        console.log('\n🎯 步骤3: 创建复习会话');
        
        try {
            // 使用智能复习模板生成题目
            const questions = await this.reviewAPI.generator.generateSmartReview({
                userId: this.testUser.id,
                count: 4,
                baseId: this.testCategory.name,
                onlyDue: false
            });
            
            console.log(`✅ 生成题目: ${questions.questions.length} 道`);
            
            // 创建复习会话
            this.reviewSession = await this.reviewAPI.sessions.create({
                reviewMode: 'SM2',
                targetQuestionCount: questions.questions.length,
                maxDurationMinutes: 45,
                notes: 'JavaScript核心概念复习'
            }, this.testUser.id);
            
            console.log(`✅ 创建会话: ${this.reviewSession.id}`);
            
            // 启动会话
            await this.reviewAPI.sessions.start(this.reviewSession.id, this.testUser.id);
            console.log('✅ 会话已启动');
            
            return { name: '创建复习会话', passed: true };
            
        } catch (error: any) {
            return { name: '创建复习会话', passed: false, message: error.message };
        }
    }

    private async simulateLearningProcess(): Promise<TestResult> {
        console.log('\n📖 步骤4: 模拟学习过程');
        
        try {
            const learningData = [
                {
                    knowledgePointId: this.knowledgePoints[0].id,
                    quality: 2, // 模糊
                    correct: false,
                    time: 35,
                    notes: '闭包概念有些模糊，需要复习'
                },
                {
                    knowledgePointId: this.knowledgePoints[1].id,
                    quality: 3, // 正确
                    correct: true,
                    time: 28,
                    notes: '事件循环理解良好'
                },
                {
                    knowledgePointId: this.knowledgePoints[2].id,
                    quality: 1, // 错误
                    correct: false,
                    time: 45,
                    notes: '变量声明区别理解错误'
                },
                {
                    knowledgePointId: this.knowledgePoints[3].id,
                    quality: 4, // 完美
                    correct: true,
                    time: 32,
                    notes: 'Promise实现思路清晰'
                }
            ];
            
            for (let i = 0; i < learningData.length; i++) {
                const data = learningData[i];
                
                // 提交答案
                await this.reviewAPI.sessions.submitAnswer(this.reviewSession.id, {
                    knowledgePointId: data.knowledgePointId,
                    qualityRating: data.quality,
                    timeSpentSeconds: data.time,
                    isSkipped: false,
                    isCorrect: data.correct,
                    studyNotes: data.notes
                }, this.testUser.id);
                
                console.log(`  ✅ 提交第 ${i+1} 题: 评分 ${data.quality}/4`);
                
                // 如果回答错误，记录错题
                if (!data.correct) {
                    await this.reviewAPI.mistakes.add({
                        userId: this.testUser.id,
                        knowledgePointId: data.knowledgePointId,
                        mistakeReason: data.notes,
                        questionType: 'text',
                        difficultyLevel: 'medium'
                    });
                    console.log(`    📝 记录错题: ${data.knowledgePointId}`);
                }
            }
            
            // 完成会话
            await this.reviewAPI.sessions.complete(this.reviewSession.id, this.testUser.id);
            console.log('✅ 会话完成');
            
            return { name: '模拟学习过程', passed: true };
            
        } catch (error: any) {
            return { name: '模拟学习过程', passed: false, message: error.message };
        }
    }

    private async analyzeAndReviewMistakes(): Promise<TestResult> {
        console.log('\n🔍 步骤5: 错题分析与复习');
        
        try {
            // 获取错题统计
            const mistakeStats = await this.reviewAPI.mistakes.getStatistics(this.testUser.id);
            console.log('📊 错题统计:', {
                total: mistakeStats.totalMistakes,
                unresolved: mistakeStats.unresolvedMistakes,
                resolved: mistakeStats.resolvedMistakes,
                resolutionRate: (mistakeStats.resolutionRate * 100).toFixed(1) + '%'
            });
            
            // 获取未解决错题
            const unresolvedMistakes = await this.reviewAPI.mistakes.getUnresolved(this.testUser.id);
            console.log(`🎯 未解决错题: ${unresolvedMistakes.length} 个`);
            
            if (unresolvedMistakes.length > 0) {
                // 创建错题复习会话
                const mistakeQuestions = await this.reviewAPI.generator.generateMistakeReview({
                    userId: this.testUser.id,
                    limit: Math.min(unresolvedMistakes.length, 5),
                    random: false
                });
                
                const mistakeSession = await this.reviewAPI.sessions.create({
                    reviewMode: 'mistake-review',
                    targetQuestionCount: mistakeQuestions.questions.length,
                    maxDurationMinutes: 25,
                    notes: '错题重点复习'
                }, this.testUser.id);
                
                await this.reviewAPI.sessions.start(mistakeSession.id, this.testUser.id);
                console.log(`✅ 创建错题复习会话: ${mistakeSession.id}`);
                
                // 模拟错题复习
                for (let i = 0; i < mistakeQuestions.questions.length; i++) {
                    await this.reviewAPI.sessions.submitAnswer(mistakeSession.id, {
                        knowledgePointId: mistakeQuestions.questions[i].knowledgePointId,
                        qualityRating: 4, // 假设现在掌握了
                        timeSpentSeconds: 20,
                        isSkipped: false,
                        isCorrect: true,
                        studyNotes: '通过错题复习，现已掌握'
                    }, this.testUser.id);
                    
                    // 标记错题为已解决
                    await this.reviewAPI.mistakes.resolve(this.testUser.id, mistakeQuestions.questions[i].knowledgePointId);
                    console.log(`  ✅ 解决错题: ${mistakeQuestions.questions[i].knowledgePointId}`);
                }
                
                await this.reviewAPI.sessions.complete(mistakeSession.id, this.testUser.id);
            }
            
            return { name: '错题分析与复习', passed: true };
            
        } catch (error: any) {
            return { name: '错题分析与复习', passed: false, message: error.message };
        }
    }

    private async analyzeLearningProgress(): Promise<TestResult> {
        console.log('\n📈 步骤6: 学习进度分析');
        
        try {
            // 获取用户会话历史
            const allSessions = await this.reviewAPI.sessions.getAll(this.testUser.id);
            const activeSessions = await this.reviewAPI.sessions.getAll(this.testUser.id);
            
            console.log('📊 会话统计:', {
                total: allSessions.length,
                active: activeSessions.length,
                completed: allSessions.filter((s: any) => s.status === 'COMPLETED').length
            });
            
            // 获取最终错题统计
            const finalStats = await this.reviewAPI.mistakes.getStatistics(this.testUser.id);
            console.log('📈 最终错题统计:', finalStats);
            
            // 生成个性化推荐
            const recommendation = await this.generatePersonalizedRecommendation(this.testUser.id, finalStats);
            console.log('🎯 个性化推荐:', recommendation);
            
            return { name: '学习进度分析', passed: true };
            
        } catch (error: any) {
            return { name: '学习进度分析', passed: false, message: error.message };
        }
    }

    private async generatePersonalizedRecommendation(userId: string, stats: any) {
        const recommendation = {
            userId,
            analysis: {
                accuracyRate: stats.resolutionRate,
                weakAreas: stats.mistakesByType,
                recommendation: ''
            },
            nextSteps: []
        };
        
        if (stats.resolutionRate < 0.7) {
            recommendation.analysis.recommendation = '需要加强错题复习';
            recommendation.nextSteps.push('优先复习错题');
            recommendation.nextSteps.push('增加复习频率');
        } else {
            recommendation.analysis.recommendation = '表现良好，继续当前节奏';
            recommendation.nextSteps.push('学习新知识');
            recommendation.nextSteps.push('定期复习');
        }
        
        return recommendation;
    }

    private async validateIntegration(): Promise<void> {
        console.log('\n✅ 集成验证');
        
        const validations = [
            {
                name: '用户数据完整性',
                check: async () => {
                    const user = await this.userAPI.auth.me();
                    return user.id === this.testUser.id;
                }
            },
            {
                name: '知识点关联正确',
                check: async () => {
                    const category = await this.knowledgeAPI.categories.getAll();
                    return category.length > 0;
                }
            },
            {
                name: '错题记录完整',
                check: async () => {
                    const mistakes = await this.reviewAPI.mistakes.getAll(this.testUser.id);
                    return mistakes.length > 0;
                }
            },
            {
                name: '会话状态正确',
                check: async () => {
                    const sessions = await this.reviewAPI.sessions.getAll(this.testUser.id);
                    return sessions.every((s: any) => ['COMPLETED', 'CANCELLED'].includes(s.status));
                }
            }
        ];
        
        for (const validation of validations) {
            try {
                const result = await validation.check();
                console.log(`${result ? '✅' : '❌'} ${validation.name}`);
            } catch (error) {
                console.log(`❌ ${validation.name}: 验证失败`);
            }
        }
    }

    private printResults(results: TestResult[]): void {
        const endTime = Date.now();
        
        console.log('\n' + '='.repeat(50));
        console.log('🎉 集成测试完成！');
        console.log(`📊 测试通过率: ${results.filter(r => r.passed).length}/${results.length}`);
        
        if (results.every(r => r.passed)) {
            console.log('✅ 所有测试通过！');
        } else {
            console.log('⚠️  部分测试失败:');
            results.filter(r => !r.passed).forEach(r => {
                console.log(`  ❌ ${r.name}: ${r.message}`);
            });
        }
    }
}

// 运行测试
const tester = new IntegrationTester();
tester.runAllTests().catch(console.error);