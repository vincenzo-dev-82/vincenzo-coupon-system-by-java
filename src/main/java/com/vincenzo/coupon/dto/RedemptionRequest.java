package com.vincenzo.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/** 결제 시 쿠폰 사용 요청 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedemptionRequest {
    @NotBlank(message = "쿠폰 코드는 필수입니다.")
    private String code;
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @NotNull(message = "주문 금액은 필수입니다.")
    @Positive(message = "주문 금액은 0보다 커야 합니다.")
    private BigDecimal orderAmount;
    
    private Long productId;   // 장바구니 내 상품 ID
    private Long categoryId;  // 장바구니 내 카테고리 ID
}