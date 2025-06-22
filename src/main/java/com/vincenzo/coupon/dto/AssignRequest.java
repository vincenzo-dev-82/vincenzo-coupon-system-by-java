package com.vincenzo.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** 쿠폰 코드로 사용자에게 발급 요청 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRequest {
    @NotBlank(message = "쿠폰 코드는 필수입니다.")
    private String code;
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}