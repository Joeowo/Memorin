package com.memorin.review.service;

import com.memorin.review.dto.request.CreateReviewSessionRequest;
import com.memorin.review.dto.request.SubmitAnswerRequest;
import com.memorin.review.dto.request.ReviewAlgorithmRequest;
import com.memorin.review.dto.response.ReviewSessionResponse;
import com.memorin.review.dto.response.ReviewAlgorithmResponse;
import com.memorin.review.entity.ReviewSession;
import com.memorin.review.entity.ReviewSubmission;
import com.memorin.review.entity.MistakeRecord;
import com.memorin.review.repository.ReviewSessionRepository;
import com.memorin.review.repository.ReviewSubmissionRepository;
import com.memorin.review.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 复习会话管理服务
 * 集成错题管理和SM-2算法
 * 
 * @author Memorin Team
 * @version 2.0.0
 * @since 2025-01-18
 */
@Service
@Transactional
public class ReviewSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewSessionService.class);

    @Autowired
    private ReviewSessionRepository reviewSessionRepository;

    @Autowired
    private ReviewSubmissionRepository reviewSubmissionRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private MistakeManagementService mistakeManagementService;

    @Autowired
    private ReviewAlgorithmService reviewAlgorithmService;

    /**
     * 创建新的复习会话
     */
    public ReviewSessionResponse createSession(CreateReviewSessionRequest request, String userId) {
        logger.info("创建复习会话，用户ID: {}, 复习模式: {}, 目标题目数: {}", 
                   userId, request.getReviewMode(), request.getTargetQuestionCount());

        // 1. 生成会话ID
        String sessionId = idGenerator.generateReviewSessionId();

        // 2. 创建会话实体
        ReviewSession session = new ReviewSession();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setReviewMode(request.getReviewMode());
        session.setSessionConfig(buildSessionConfig(request));
        session.setTotalQuestions(request.getTargetQuestionCount());
        session.setCurrentIndex(0);
        session.setCompletedCount(0);
        session.setCorrectCount(0);
        session.setStatus("CREATED");
        session.setNotes(request.getNotes());
        session.setIsActive(true);

        // 3. 估算会话时长
        int estimatedDuration = estimateSessionDuration(request);
        session.setEstimatedDurationMinutes(estimatedDuration);

        // 4. 生成题目列表
        String questionList = generateQuestionList(request, userId);
        session.setQuestionList(questionList);

        // 5. 保存会话
        ReviewSession savedSession = reviewSessionRepository.save(session);
        logger.info("成功创建复习会话，ID: {}, 总题目数: {}", savedSession.getId(), savedSession.getTotalQuestions());

        // 6. 如果设置了自动开始，立即启动会话
        if (Boolean.TRUE.equals(request.getAutoStart())) {
            savedSession.startSession();
            reviewSessionRepository.save(savedSession);
            logger.info("会话自动启动，ID: {}", savedSession.getId());
        }

        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 获取会话详情
     */
    public ReviewSessionResponse getSession(String sessionId, String userId) {
        logger.info("获取会话详情，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        return ReviewSessionResponse.fromEntity(session);
    }

    /**
     * 启动会话
     */
    public ReviewSessionResponse startSession(String sessionId, String userId) {
        logger.info("启动复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if (!"CREATED".equals(session.getStatus()) && !"PAUSED".equals(session.getStatus())) {
            throw new IllegalStateException("只有创建状态或暂停状态的会话才能启动");
        }

        session.startSession();
        ReviewSession savedSession = reviewSessionRepository.save(session);
        
        logger.info("会话启动成功，ID: {}, 状态: {}", savedSession.getId(), savedSession.getStatus());
        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 暂停会话
     */
    public ReviewSessionResponse pauseSession(String sessionId, String userId) {
        logger.info("暂停复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new IllegalStateException("只有活跃状态的会话才能暂停");
        }

        session.pauseSession();
        ReviewSession savedSession = reviewSessionRepository.save(session);
        
        logger.info("会话暂停成功，ID: {}, 状态: {}", savedSession.getId(), savedSession.getStatus());
        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 恢复会话
     */
    public ReviewSessionResponse resumeSession(String sessionId, String userId) {
        logger.info("恢复复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if (!"PAUSED".equals(session.getStatus())) {
            throw new IllegalStateException("只有暂停状态的会话才能恢复");
        }

        session.resumeSession();
        ReviewSession savedSession = reviewSessionRepository.save(session);
        
        logger.info("会话恢复成功，ID: {}, 状态: {}", savedSession.getId(), savedSession.getStatus());
        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 完成会话
     */
    public ReviewSessionResponse completeSession(String sessionId, String userId) {
        logger.info("完成复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if (!"ACTIVE".equals(session.getStatus()) && !"PAUSED".equals(session.getStatus())) {
            throw new IllegalStateException("只有活跃或暂停状态的会话才能完成");
        }

        session.completeSession();
        ReviewSession savedSession = reviewSessionRepository.save(session);
        
        logger.info("会话完成，ID: {}, 最终正确率: {:.2f}%", 
                   savedSession.getId(), savedSession.getAccuracyRate() * 100);
        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 取消会话
     */
    public ReviewSessionResponse cancelSession(String sessionId, String userId) {
        logger.info("取消复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if ("COMPLETED".equals(session.getStatus()) || "CANCELLED".equals(session.getStatus())) {
            throw new IllegalStateException("已完成或已取消的会话不能再次取消");
        }

        session.cancelSession();
        ReviewSession savedSession = reviewSessionRepository.save(session);
        
        logger.info("会话取消成功，ID: {}, 状态: {}", savedSession.getId(), savedSession.getStatus());
        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 提交答案并更新会话进度
     * 集成错题管理和SM-2算法
     */
    public ReviewSessionResponse submitAnswer(SubmitAnswerRequest request, String userId) {
        logger.info("提交答案，会话ID: {}, 知识点ID: {}, 质量评分: {}", 
                   request.getSessionId(), request.getKnowledgePointId(), request.getQualityRating());

        // 1. 获取会话
        ReviewSession session = reviewSessionRepository.findByIdAndUserId(request.getSessionId(), userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new IllegalStateException("只有活跃状态的会话才能提交答案");
        }

        // 2. 创建提交记录
        ReviewSubmission submission = createSubmissionRecord(request, session);

        // 3. 使用SM-2算法计算复习参数
        ReviewAlgorithmResponse algorithmResult = calculateSM2Parameters(request, userId);
        updateSubmissionWithAlgorithmResult(submission, algorithmResult);

        // 4. 处理错题记录
        handleMistakeRecord(request, userId);

        // 5. 更新错题统计
        updateMistakeStatistics(request, userId);

        // 6. 更新会话进度
        updateSessionProgress(session, request);

        // 7. 保存更新
        ReviewSession savedSession = reviewSessionRepository.save(session);
        reviewSubmissionRepository.save(submission);

        logger.info("答案提交成功，会话进度: {}/{}，正确率: {:.2f}%", 
                   savedSession.getCompletedCount(), savedSession.getTotalQuestions(),
                   savedSession.getAccuracyRate() * 100);

        // 8. 检查是否需要生成错题分析报告
        if ("COMPLETED".equals(savedSession.getStatus())) {
            generateMistakeAnalysisReport(savedSession, userId);
        }

        return ReviewSessionResponse.fromEntity(savedSession);
    }

    /**
     * 获取用户的会话列表
     */
    public List<ReviewSessionResponse> getUserSessions(String userId, String status, Integer limit) {
        logger.info("获取用户会话列表，用户ID: {}, 状态过滤: {}, 限制数量: {}", userId, status, limit);

        List<ReviewSession> sessions;
        if (status != null && !status.isEmpty()) {
            sessions = reviewSessionRepository.findByUserIdAndStatus(userId, status);
        } else {
            sessions = reviewSessionRepository.findByUserIdAndIsActiveTrue(userId);
        }

        // 应用数量限制
        if (limit != null && limit > 0) {
            sessions = sessions.stream().limit(limit).collect(Collectors.toList());
        }

        return sessions.stream()
                .map(ReviewSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的活跃会话
     */
    public List<ReviewSessionResponse> getActiveSessions(String userId) {
        logger.info("获取用户活跃会话，用户ID: {}", userId);

        List<ReviewSession> sessions = reviewSessionRepository.findActiveSessionsByUserId(userId);
        return sessions.stream()
                .map(ReviewSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 软删除会话
     */
    public void deleteSession(String sessionId, String userId) {
        logger.info("删除复习会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        ReviewSession session = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("会话不存在或已被删除"));

        // 软删除
        reviewSessionRepository.softDeleteByIdAndUserId(sessionId, userId, System.currentTimeMillis());
        
        logger.info("会话删除成功，ID: {}", sessionId);
    }

    // ============== 私有辅助方法 ==============

    /**
     * 构建会话配置JSON
     */
    private String buildSessionConfig(CreateReviewSessionRequest request) {
        // TODO: 实现复杂的配置构建逻辑
        // 这里简化处理，实际应该构建详细的JSON配置
        return String.format("{\"reviewMode\":\"%s\",\"sortOrder\":\"%s\",\"targetCount\":%d}", 
                            request.getReviewMode(), request.getSortOrder(), request.getTargetQuestionCount());
    }

    /**
     * 估算会话时长
     */
    private Integer estimateSessionDuration(CreateReviewSessionRequest request) {
        // 基于题目数量和类型估算时长
        int baseTimePerQuestion = 2; // 基础时间2分钟/题
        
        // 根据题目类型调整
        if (request.getQuestionTypes() != null) {
            if (request.getQuestionTypes().contains("code")) {
                baseTimePerQuestion = 4; // 编程题需要更多时间
            } else if (request.getQuestionTypes().contains("choice")) {
                baseTimePerQuestion = 1; // 选择题时间较短
            }
        }
        
        return request.getTargetQuestionCount() * baseTimePerQuestion;
    }

    /**
     * 生成题目列表
     */
    private String generateQuestionList(CreateReviewSessionRequest request, String userId) {
        // TODO: 实现智能题目生成逻辑
        // 这里简化处理，实际应该根据复习模式、过滤条件等生成题目
        return String.format("[{\"knowledgePointId\":\"KP_DEMO_001\",\"questionType\":\"%s\"}]", 
                            request.getQuestionTypes() != null && !request.getQuestionTypes().isEmpty() 
                            ? request.getQuestionTypes().get(0) : "text");
    }

    /**
     * 创建提交记录
     */
    private ReviewSubmission createSubmissionRecord(SubmitAnswerRequest request, ReviewSession session) {
        String submissionId = idGenerator.generateReviewSubmissionId();
        
        ReviewSubmission submission = new ReviewSubmission();
        submission.setId(submissionId);
        submission.setSessionId(request.getSessionId());
        submission.setKnowledgePointId(request.getKnowledgePointId());
        submission.setQuestionType(request.getQuestionType());
        submission.setQuestionIndex(request.getQuestionIndex());
        submission.setUserAnswer(request.getUserAnswer());
        submission.setIsCorrect(request.getIsCorrect());
        submission.setQualityRating(request.getQualityRating());
        submission.setTimeSpentSeconds(request.getTimeSpentSeconds());
        submission.setMistakeReason(request.getMistakeReason());
        submission.setStudyNotes(request.getStudyNotes());
        submission.setPerceivedDifficulty(request.getPerceivedDifficulty());
        submission.setIsSkipped(request.getIsSkipped());
        submission.setSubmissionType(request.getSubmissionType());
        
        return submission;
    }

    /**
     * 更新会话进度
     */
    private void updateSessionProgress(ReviewSession session, SubmitAnswerRequest request) {
        // 更新完成数量
        session.setCompletedCount(session.getCompletedCount() + 1);
        
        // 更新正确数量
        if (Boolean.TRUE.equals(request.getIsCorrect())) {
            session.setCorrectCount(session.getCorrectCount() + 1);
        }
        
        // 更新当前索引
        if (session.getCurrentIndex() < session.getTotalQuestions() - 1) {
            session.setCurrentIndex(session.getCurrentIndex() + 1);
        }
        
        // 检查是否完成所有题目
        if (session.getCompletedCount().equals(session.getTotalQuestions())) {
            session.completeSession();
            logger.info("会话自动完成，所有题目已答完，ID: {}", session.getId());
        }
    }

    // ======================== 新增的集成方法 ========================

    /**
     * 使用SM-2算法计算复习参数
     */
    private ReviewAlgorithmResponse calculateSM2Parameters(SubmitAnswerRequest request, String userId) {
        try {
            ReviewAlgorithmRequest algorithmRequest = new ReviewAlgorithmRequest();
            algorithmRequest.setUserId(userId);
            algorithmRequest.setKnowledgePointId(request.getKnowledgePointId());
            algorithmRequest.setQuality(request.getQualityRating());
            
            // 设置默认参数，实际应该从历史数据获取
            algorithmRequest.setEaseFactor(2.5);
            algorithmRequest.setInterval(1);
            
            ReviewAlgorithmResponse result = reviewAlgorithmService.calculateNextReview(algorithmRequest);
            
            logger.debug("SM-2算法计算完成: 知识点={}, 新易度因子={}, 新间隔={}, 下次复习时间={}", 
                        request.getKnowledgePointId(), result.getNewEaseFactor(), 
                        result.getNewInterval(), result.getNextReviewTime());
            
            return result;
        } catch (Exception e) {
            logger.error("SM-2算法计算失败: {}", e.getMessage(), e);
            // 返回默认值，确保系统不因算法失败而中断
            ReviewAlgorithmResponse defaultResult = new ReviewAlgorithmResponse();
            defaultResult.setNewEaseFactor(2.5);
            defaultResult.setNewInterval(1);
            // 修复：设置为时间戳格式
            defaultResult.setNextReviewTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000L); // 24小时后
            defaultResult.setReviewIntensity("NORMAL");
            return defaultResult;
        }
    }

    /**
     * 将SM-2算法结果更新到提交记录
     */
    private void updateSubmissionWithAlgorithmResult(ReviewSubmission submission, ReviewAlgorithmResponse algorithmResult) {
        submission.setPreviousEaseFactor(algorithmResult.getNewEaseFactor()); // 这里简化处理
        submission.setPreviousInterval(algorithmResult.getNewInterval());
        submission.setNewEaseFactor(algorithmResult.getNewEaseFactor());
        submission.setNewInterval(algorithmResult.getNewInterval());
        // 修复：直接使用Long类型的时间戳
        submission.setNextReviewTime(algorithmResult.getNextReviewTime());
        
        logger.debug("提交记录更新SM-2结果: ID={}, 易度因子={}, 间隔={}", 
                    submission.getId(), algorithmResult.getNewEaseFactor(), algorithmResult.getNewInterval());
    }

    /**
     * 处理错题记录
     */
    private void handleMistakeRecord(SubmitAnswerRequest request, String userId) {
        // 当质量评分为1(错误)时，自动添加错题记录
        if (request.getQualityRating() != null && request.getQualityRating() == 1) {
            try {
                String mistakeReason = buildMistakeReason(request);
                
                MistakeRecord mistakeRecord = mistakeManagementService.addOrUpdateMistake(
                    userId,
                    request.getKnowledgePointId(),
                    mistakeReason,
                    request.getQuestionType(),
                    // 修复：将Integer转换为String
                    request.getPerceivedDifficulty() != null ? request.getPerceivedDifficulty().toString() : "medium"
                );
                
                logger.info("自动添加错题记录: 用户={}, 知识点={}, 错题ID={}", 
                           userId, request.getKnowledgePointId(), mistakeRecord.getId());
                           
            } catch (Exception e) {
                logger.error("添加错题记录失败: {}", e.getMessage(), e);
                // 错题记录失败不应该影响主流程
            }
        }
    }

    /**
     * 构建错题原因描述
     */
    private String buildMistakeReason(SubmitAnswerRequest request) {
        StringBuilder reason = new StringBuilder();
        
        if (request.getUserAnswer() != null && !request.getUserAnswer().trim().isEmpty()) {
            reason.append("用户答案: ").append(request.getUserAnswer());
        }
        
        if (request.getMistakeReason() != null && !request.getMistakeReason().trim().isEmpty()) {
            if (reason.length() > 0) reason.append("; ");
            reason.append("错误原因: ").append(request.getMistakeReason());
        }
        
        if (request.getPerceivedDifficulty() != null) {
            if (reason.length() > 0) reason.append("; ");
            reason.append("难度: ").append(request.getPerceivedDifficulty());
        }
        
        if (reason.length() == 0) {
            reason.append("复习中答错");
        }
        
        return reason.toString();
    }

    /**
     * 更新错题统计信息
     */
    private void updateMistakeStatistics(SubmitAnswerRequest request, String userId) {
        try {
            // 无论对错都要更新错题的复习统计
            boolean isCorrect = Boolean.TRUE.equals(request.getIsCorrect());
            mistakeManagementService.recordReviewResult(userId, request.getKnowledgePointId(), isCorrect);
            
            logger.debug("更新错题统计: 用户={}, 知识点={}, 是否正确={}", 
                        userId, request.getKnowledgePointId(), isCorrect);
                        
        } catch (Exception e) {
            logger.error("更新错题统计失败: {}", e.getMessage(), e);
            // 统计更新失败不应该影响主流程
        }
    }

    /**
     * 生成错题分析报告
     */
    private void generateMistakeAnalysisReport(ReviewSession session, String userId) {
        try {
            // 获取本次会话的错题统计 - 使用现有的Repository方法
            List<ReviewSubmission> submissions = reviewSubmissionRepository.findBySessionIdOrderByIndex(session.getId());
            Map<String, Object> sessionMistakeAnalysis = analyzeSessionMistakes(submissions);
            
            // 获取用户整体错题统计
            Map<String, Object> overallMistakeStats = mistakeManagementService.getMistakeStatistics(userId);
            
            // 生成综合分析报告
            Map<String, Object> analysisReport = new HashMap<>();
            analysisReport.put("sessionId", session.getId());
            analysisReport.put("sessionAnalysis", sessionMistakeAnalysis);
            analysisReport.put("overallStats", overallMistakeStats);
            analysisReport.put("generatedAt", LocalDateTime.now());
            
            logger.info("生成错题分析报告: 会话={}, 本次错题数={}, 总错题数={}", 
                       session.getId(), 
                       sessionMistakeAnalysis.get("mistakeCount"), 
                       overallMistakeStats.get("totalMistakes"));
                       
            // TODO: 可以将报告保存到数据库或推送给用户
            
        } catch (Exception e) {
            logger.error("生成错题分析报告失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 分析会话中的错题情况
     */
    private Map<String, Object> analyzeSessionMistakes(List<ReviewSubmission> submissions) {
        Map<String, Object> analysis = new HashMap<>();
        
        long totalSubmissions = submissions.size();
        long mistakeCount = submissions.stream()
            .filter(s -> s.getQualityRating() != null && s.getQualityRating() == 1)
            .count();
        
        // 按题目类型分组统计错题 - 修复类型转换问题
        Map<String, Long> mistakesByType = new HashMap<>();
        submissions.stream()
            .filter(s -> s.getQualityRating() != null && s.getQualityRating() == 1)
            .forEach(s -> {
                String type = s.getQuestionType() != null ? s.getQuestionType() : "unknown";
                mistakesByType.put(type, mistakesByType.getOrDefault(type, 0L) + 1);
            });
        
        // 按难度分组统计错题 - 修复类型转换问题
        Map<String, Long> mistakesByDifficulty = new HashMap<>();
        submissions.stream()
            .filter(s -> s.getQualityRating() != null && s.getQualityRating() == 1)
            .forEach(s -> {
                String difficulty = s.getPerceivedDifficulty() != null ? s.getPerceivedDifficulty().toString() : "unknown";
                mistakesByDifficulty.put(difficulty, mistakesByDifficulty.getOrDefault(difficulty, 0L) + 1);
            });
        
        analysis.put("totalSubmissions", totalSubmissions);
        analysis.put("mistakeCount", mistakeCount);
        analysis.put("mistakeRate", totalSubmissions > 0 ? (double) mistakeCount / totalSubmissions : 0.0);
        analysis.put("mistakesByType", mistakesByType);
        analysis.put("mistakesByDifficulty", mistakesByDifficulty);
        
        return analysis;
    }
} 