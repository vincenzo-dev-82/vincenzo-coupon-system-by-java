package com.vincenzo.coupon.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {
    private Long id;
    private String code;
    private String title;
    private String label;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private String type;
    private Integer discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private Integer rewardPointPercent;
    private Integer maxUsages;
    private Integer remainingIssue;
    private boolean alreadyAssigned;
    private Set<Long> targetProductIds;
    private Set<Long> targetCategoryIds;
}