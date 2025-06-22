package com.vincenzo.coupon.controller;

import com.vincenzo.coupon.domain.Coupon;
import com.vincenzo.coupon.dto.CouponCreateRequest;
import com.vincenzo.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    /**
     * 관리자용 쿠폰 생성 API
     */
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        log.info("쿠폰 생성 요청 - 코드: {}, 제목: {}", request.getCode(), request.getTitle());
        
        Coupon coupon = Coupon.builder()
            .code(request.getCode())
            .title(request.getTitle())
            .label(request.getLabel())
            .validFrom(request.getValidFrom())
            .validUntil(request.getValidUntil())
            .type(request.getType())
            .discountValue(request.getDiscountValue())
            .maxDiscountAmount(request.getMaxDiscountAmount())
            .minOrderAmount(request.getMinOrderAmount())
            .rewardPointPercent(request.getRewardPointPercent())
            .maxIssueCount(request.getMaxIssueCount())
            .maxUsages(request.getMaxUsages())
            .targetCategoryIds(request.getTargetCategoryIds())
            .targetProductIds(request.getTargetProductIds())
            .build();
            
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }
}