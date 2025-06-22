package com.vincenzo.coupon.service;

import com.vincenzo.coupon.domain.Coupon;
import com.vincenzo.coupon.domain.UserCoupon;
import com.vincenzo.coupon.dto.CouponDto;
import com.vincenzo.coupon.dto.RedemptionResult;
import com.vincenzo.coupon.repository.CouponRepository;
import com.vincenzo.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepo;
    private final UserCouponRepository userCouponRepo;

    /**
     * (1) 유효한 쿠폰 목록 조회
     * - 현재 시각이 validFrom ~ validUntil 범위 내
     * - 사용자에게 이미 발급되었는지 여부 포함
     */
    @Transactional(readOnly = true)
    public List<CouponDto> listAvailableCoupons(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepo.findAll().stream()
            // 유효기간 필터
            .filter(c -> !now.isBefore(c.getValidFrom()) && !now.isAfter(c.getValidUntil()))
            .map(c -> {
                CouponDto dto = new CouponDto();
                BeanUtils.copyProperties(c, dto);
                // 이미 발급된 쿠폰인지
                dto.setAlreadyAssigned(
                    userCouponRepo.existsByCoupon_IdAndUserId(c.getId(), userId));
                // 남은 발급 수 계산
                dto.setRemainingIssue(
                    c.getMaxIssueCount() == null
                        ? Integer.MAX_VALUE
                        : Math.max(0, c.getMaxIssueCount() - c.getIssuedCount())
                );
                return dto;
            })
            .toList();
    }

    /**
     * (2) 쿠폰 생성 (관리자용)
     */
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepo.existsByCode(coupon.getCode())) {
            throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
        }
        return couponRepo.save(coupon);
    }

    /**
     * (3) 사용자 발급 (코드 직접 입력)
     */
    public void assignToUser(String code, Long userId) {
        Coupon c = couponRepo.findByCode(code)
            .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다."));
            
        // 유효기간 체크
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(c.getValidFrom()) || now.isAfter(c.getValidUntil())) {
            throw new IllegalStateException("유효 기간이 아닌 쿠폰입니다.");
        }
        
        // 발급 제한 체크
        if (c.getMaxIssueCount() != null && c.getIssuedCount() >= c.getMaxIssueCount()) {
            throw new IllegalStateException("발급 한도를 초과했습니다.");
        }
        
        // 중복 발급 금지
        if (userCouponRepo.existsByCoupon_IdAndUserId(c.getId(), userId)) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }
        
        // UserCoupon 생성
        UserCoupon uc = UserCoupon.builder()
            .coupon(c)
            .userId(userId)
            .remainingUsages(c.getMaxUsages())
            .assignedAt(LocalDateTime.now())
            .used(false)
            .build();
        userCouponRepo.save(uc);

        // 발급 카운트 증가
        c.setIssuedCount(c.getIssuedCount() + 1);
        couponRepo.save(c);
        
        log.info("쿠폰 발급 완료 - 코드: {}, 사용자: {}", code, userId);
    }

    /**
     * (4) 쿠폰 사용(장바구니/결제 시)
     * - 유효기간, 최소주문금액, 대상 상품/카테고리 체크
     * - 할인액 계산(정율+캐/정액)
     * - 사용횟수 차감, 포인트 적립량 계산
     */
    public RedemptionResult redeem(String code, Long userId, BigDecimal orderAmount,
                                   Long productId, Long categoryId) {
        LocalDateTime now = LocalDateTime.now();

        // 쿠폰 존재 & 유효기간 체크
        Coupon c = couponRepo.findByCode(code)
            .orElseThrow(() -> new IllegalStateException("쿠폰이 존재하지 않습니다."));
        if (now.isBefore(c.getValidFrom()) || now.isAfter(c.getValidUntil())) {
            throw new IllegalStateException("유효 기간이 아닌 쿠폰입니다.");
        }
        
        // 최소 주문 금액 체크
        if (orderAmount.compareTo(c.getMinOrderAmount()) < 0) {
            throw new IllegalStateException("최소 주문 금액 미달입니다.");
        }
        
        // 대상 상품/카테고리 체크 (지정된 게 있으면 반드시 포함)
        if (c.getTargetProductIds() != null && !c.getTargetProductIds().isEmpty()
            && !c.getTargetProductIds().contains(productId)) {
            throw new IllegalStateException("해당 상품에 사용할 수 없는 쿠폰입니다.");
        }
        if (c.getTargetCategoryIds() != null && !c.getTargetCategoryIds().isEmpty()
            && !c.getTargetCategoryIds().contains(categoryId)) {
            throw new IllegalStateException("해당 카테고리에 사용할 수 없는 쿠폰입니다.");
        }

        // 사용자 발급 내역 조회
        UserCoupon uc = userCouponRepo
            .findByCoupon_CodeAndUserIdAndUsedFalse(code, userId)
            .orElseThrow(() -> new IllegalStateException("사용 가능한 쿠폰이 없습니다."));

        // 할인액 계산
        BigDecimal rawDiscount;
        if ("PERCENT".equals(c.getType())) {
            // 정율 할인
            rawDiscount = orderAmount
                .multiply(BigDecimal.valueOf(c.getDiscountValue()))
                .divide(BigDecimal.valueOf(100));
            // 최대 할인 한도 캐 적용
            if (c.getMaxDiscountAmount() != null) {
                rawDiscount = rawDiscount.min(c.getMaxDiscountAmount());
            }
        } else {
            // 정액 할인
            rawDiscount = BigDecimal.valueOf(c.getDiscountValue());
        }

        // 사용횟수 차감
        if (uc.getRemainingUsages() != null) {
            uc.setRemainingUsages(uc.getRemainingUsages() - 1);
            if (uc.getRemainingUsages() <= 0) {
                uc.setUsed(true);
            }
        } else {
            // 무제한 사용인 경우엔 used 플래그만 false로 유지
        }
        userCouponRepo.save(uc);

        // 포인트 적립량 계산 (예: 결제금액 기준)
        BigDecimal rewardPoints = BigDecimal.ZERO;
        if (c.getRewardPointPercent() != null) {
            rewardPoints = orderAmount
                .multiply(BigDecimal.valueOf(c.getRewardPointPercent()))
                .divide(BigDecimal.valueOf(100));
        }

        // 최종 결제액 = 주문금액 - 할인액
        BigDecimal finalAmount = orderAmount.subtract(rawDiscount);
        
        log.info("쿠폰 사용 완료 - 코드: {}, 사용자: {}, 할인액: {}, 최종금액: {}", 
                code, userId, rawDiscount, finalAmount);

        return RedemptionResult.builder()
            .finalAmount(finalAmount)
            .discountApplied(rawDiscount)
            .rewardPointsEarned(rewardPoints)
            .build();
    }
}