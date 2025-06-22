package com.vincenzo.coupon.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 쿠폰 코드 생성 유틸리티
 */
@Component
public class CouponCodeGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * 랜덤 쿠폰 코드 생성
     * @param length 코드 길이
     * @return 생성된 쿠폰 코드
     */
    public String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
    
    /**
     * 패턴 기반 쿠폰 코드 생성
     * @param prefix 접두사
     * @param suffix 접미사
     * @return 생성된 쿠폰 코드
     */
    public String generatePatternCode(String prefix, String suffix) {
        String randomPart = generateRandomCode(6);
        return prefix + randomPart + suffix;
    }
    
    /**
     * 날짜 기반 쿠폰 코드 생성
     * @param prefix 접두사
     * @return 생성된 쿠폰 코드
     */
    public String generateDateBasedCode(String prefix) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = generateRandomCode(4);
        return prefix + datePart + randomPart;
    }
}