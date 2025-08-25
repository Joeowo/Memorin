package com.memorin.review.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 * 为复习相关实体生成唯一标识符
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class IdGenerator {

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final AtomicInteger reviewSessionCounter = new AtomicInteger(1);
    private static final AtomicInteger reviewSubmissionCounter = new AtomicInteger(1);
    private static final AtomicInteger mistakeRecordCounter = new AtomicInteger(1);
    
    private final SimpleDateFormat dateFormat;
    
    public IdGenerator() {
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    /**
     * 生成复习会话ID
     * 格式: RS_yyyyMMdd_序号
     * 
     * @return 复习会话ID
     */
    public String generateReviewSessionId() {
        String dateStr = dateFormat.format(new Date());
        int counter = reviewSessionCounter.getAndIncrement();
        return String.format("RS_%s_%03d", dateStr, counter);
    }

    /**
     * 生成复习提交记录ID
     * 格式: RSB_yyyyMMdd_序号
     * 
     * @return 复习提交记录ID
     */
    public String generateReviewSubmissionId() {
        String dateStr = dateFormat.format(new Date());
        int counter = reviewSubmissionCounter.getAndIncrement();
        return String.format("RSB_%s_%03d", dateStr, counter);
    }

    /**
     * 生成错题记录ID
     * 格式: MR_yyyyMMdd_序号
     * 
     * @return 错题记录ID
     */
    public String generateMistakeRecordId() {
        String dateStr = dateFormat.format(new Date());
        int counter = mistakeRecordCounter.getAndIncrement();
        return String.format("MR_%s_%03d", dateStr, counter);
    }

    /**
     * 重置所有计数器（仅用于测试）
     */
    public void resetCounters() {
        reviewSessionCounter.set(1);
        reviewSubmissionCounter.set(1);
        mistakeRecordCounter.set(1);
    }
} 