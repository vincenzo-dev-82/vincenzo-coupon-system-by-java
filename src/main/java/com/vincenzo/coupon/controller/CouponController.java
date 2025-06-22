package com.vincenzo.coupon.controller;

import com.vincenzo.coupon.dto.AssignRequest;
import com.vincenzo.coupon.dto.CouponDto;
import com.vincenzo.coupon.dto.RedemptionRequest;
import com.vincenzo.coupon.dto.RedemptionResult;
import com.vincenzo.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    /** 1) 사용자별 사용 가능한 쿠폰 목록 조회 */
    @GetMapping
    public ResponseEntity<List<CouponDto>> list(@RequestParam Long userId) {
        log.info("쿠폰 목록 조회 요청 - 사용자: {}", userId);
        List<CouponDto> coupons = service.listAvailableCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /** 2) 사용자 직접 발급 (쿠폰 코드 입력) */
    @PostMapping("/assign")
    public ResponseEntity<Void> assign(@Valid @RequestBody AssignRequest req) {
        log.info("쿠폰 발급 요청 - 코드: {}, 사용자: {}", req.getCode(), req.getUserId());
        service.assignToUser(req.getCode(), req.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 3) 쿠폰 사용 (결제 시) */
    @PostMapping("/redeem")
    public ResponseEntity<RedemptionResult> redeem(@Valid @RequestBody RedemptionRequest req) {
        log.info("쿠폰 사용 요청 - 코드: {}, 사용자: {}, 주문금액: {}", 
                req.getCode(), req.getUserId(), req.getOrderAmount());
        RedemptionResult result = service.redeem(
            req.getCode(),
            req.getUserId(),
            req.getOrderAmount(),
            req.getProductId(),
            req.getCategoryId()
        );
        return ResponseEntity.ok(result);
    }
}