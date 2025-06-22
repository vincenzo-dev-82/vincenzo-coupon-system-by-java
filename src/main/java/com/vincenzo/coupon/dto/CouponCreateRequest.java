package com.vincenzo.coupon.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponCreateRequest {
    
    @NotBlank(message = "쿠폰 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "쿠폰 코드는 대문자와 숫자만 사용 가능합니다.")
    private String code;
    
    @NotBlank(message = "쿠폰 제목은 필수입니다.")
    private String title;
    
    @NotBlank(message = "쿠폰 라벨은 필수입니다.")
    private String label;
    
    @NotNull(message = "유효 시작일은 필수입니다.")
    @FutureOrPresent(message = "유효 시작일은 현재 또는 미래여야 합니다.")
    private LocalDateTime validFrom;
    
    @NotNull(message = "유효 종료일은 필수입니다.")
    @Future(message = "유효 종료일은 미래여야 합니다.")
    private LocalDateTime validUntil;
    
    @NotBlank(message = "할인 타입은 필수입니다.")
    @Pattern(regexp = "^(FIXED|PERCENT)$", message = "할인 타입은 FIXED 또는 PERCENT만 가능합니다.")
    private String type;
    
    @NotNull(message = "할인 값은 필수입니다.")
    @Positive(message = "할인 값은 0보다 커야 합니다.")
    private Integer discountValue;
    
    private BigDecimal maxDiscountAmount;
    
    @NotNull(message = "최소 주문 금액은 필수입니다.")
    @PositiveOrZero(message = "최소 주문 금액은 0 이상이어야 합니다.")
    private BigDecimal minOrderAmount;
    
    @Min(value = 0, message = "포인트 적립률은 0 이상이어야 합니다.")
    @Max(value = 100, message = "포인트 적립률은 100 이하여야 합니다.")
    private Integer rewardPointPercent;
    
    @Positive(message = "최대 발급 수는 0보다 커야 합니다.")
    private Integer maxIssueCount;
    
    @Positive(message = "최대 사용 횟수는 0보다 커야 합니다.")
    private Integer maxUsages;
    
    private Set<Long> targetCategoryIds;
    private Set<Long> targetProductIds;
}