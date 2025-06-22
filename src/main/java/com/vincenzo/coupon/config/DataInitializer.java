package com.vincenzo.coupon.config;

import com.vincenzo.coupon.domain.Coupon;
import com.vincenzo.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner init(CouponRepository couponRepository) {
        return args -> {
            // 샘플 쿠폰 데이터 생성
            createSampleCoupons(couponRepository);
        };
    }

    private void createSampleCoupons(CouponRepository couponRepository) {
        // 1. 신규 회원 환영 쿠폰 (10% 할인, 최대 5천원)
        Coupon welcomeCoupon = Coupon.builder()
            .code("WELCOME2025")
            .title("신규 회원 환영 쿠폰")
            .label("10% 할인")
            .validFrom(LocalDateTime.now())
            .validUntil(LocalDateTime.now().plusMonths(1))
            .type("PERCENT")
            .discountValue(10)
            .maxDiscountAmount(new BigDecimal("5000"))
            .minOrderAmount(new BigDecimal("10000"))
            .rewardPointPercent(5)
            .maxIssueCount(1000)
            .issuedCount(0)
            .maxUsages(1)
            .build();
        couponRepository.save(welcomeCoupon);
        log.info("신규 회원 쿠폰 생성: {}", welcomeCoupon.getCode());

        // 2. 브랜드 데이 특별 쿠폰 (5천원 할인)
        Coupon brandDayCoupon = Coupon.builder()
            .code("BRANDDAY5000")
            .title("브랜드 데이 특별 쿠폰")
            .label("5,000원 할인")
            .validFrom(LocalDateTime.now())
            .validUntil(LocalDateTime.now().plusDays(7))
            .type("FIXED")
            .discountValue(5000)
            .minOrderAmount(new BigDecimal("30000"))
            .rewardPointPercent(3)
            .maxIssueCount(500)
            .issuedCount(0)
            .maxUsages(3)  // 3회 사용 가능
            .build();
        couponRepository.save(brandDayCoupon);
        log.info("브랜드 데이 쿠폰 생성: {}", brandDayCoupon.getCode());

        // 3. 카테고리 전용 쿠폰 (전자제품 15% 할인)
        Coupon categoryCoupon = Coupon.builder()
            .code("TECH15OFF")
            .title("전자제품 15% 할인 쿠폰")
            .label("15% 할인")
            .validFrom(LocalDateTime.now())
            .validUntil(LocalDateTime.now().plusDays(14))
            .type("PERCENT")
            .discountValue(15)
            .maxDiscountAmount(new BigDecimal("20000"))
            .minOrderAmount(new BigDecimal("50000"))
            .rewardPointPercent(null)
            .maxIssueCount(null)  // 무제한
            .issuedCount(0)
            .maxUsages(null)  // 무제한 사용
            .targetCategoryIds(Set.of(10L, 11L, 12L))  // 전자제품 카테고리
            .build();
        couponRepository.save(categoryCoupon);
        log.info("카테고리 전용 쿠폰 생성: {}", categoryCoupon.getCode());

        // 4. VIP 전용 쿠폰 (20% 할인, 최대 5만원)
        Coupon vipCoupon = Coupon.builder()
            .code("VIP20SPECIAL")
            .title("VIP 고객 전용 특별 쿠폰")
            .label("20% 할인")
            .validFrom(LocalDateTime.now())
            .validUntil(LocalDateTime.now().plusMonths(3))
            .type("PERCENT")
            .discountValue(20)
            .maxDiscountAmount(new BigDecimal("50000"))
            .minOrderAmount(new BigDecimal("100000"))
            .rewardPointPercent(10)
            .maxIssueCount(100)
            .issuedCount(0)
            .maxUsages(5)
            .build();
        couponRepository.save(vipCoupon);
        log.info("VIP 전용 쿠폰 생성: {}", vipCoupon.getCode());

        log.info("샘플 쿠폰 데이터 초기화 완료");
    }
}