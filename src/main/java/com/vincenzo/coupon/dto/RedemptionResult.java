package com.vincenzo.coupon.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * 쿠폰 사용 결과 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedemptionResult {
    /** 쿠폰 적용 후 최종 결제 금액 */
    private BigDecimal finalAmount;
    
    /** 실제 적용된 할인 금액 */
    private BigDecimal discountApplied;
    
    /** 적립된 포인트 양 */
    private BigDecimal rewardPointsEarned;
}