package com.vincenzo.coupon.service;

import com.vincenzo.coupon.domain.Coupon;
import com.vincenzo.coupon.domain.UserCoupon;
import com.vincenzo.coupon.dto.CouponDto;
import com.vincenzo.coupon.dto.RedemptionResult;
import com.vincenzo.coupon.repository.CouponRepository;
import com.vincenzo.coupon.repository.UserCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon testCoupon;
    private UserCoupon testUserCoupon;

    @BeforeEach
    void setUp() {
        testCoupon = Coupon.builder()
            .id(1L)
            .code("TEST100")
            .title("테스트 쿠폰")
            .label("10% 할인")
            .validFrom(LocalDateTime.now().minusDays(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .type("PERCENT")
            .discountValue(10)
            .maxDiscountAmount(new BigDecimal("5000"))
            .minOrderAmount(new BigDecimal("10000"))
            .maxIssueCount(100)
            .issuedCount(0)
            .maxUsages(1)
            .build();

        testUserCoupon = UserCoupon.builder()
            .id(1L)
            .coupon(testCoupon)
            .userId(1L)
            .remainingUsages(1)
            .assignedAt(LocalDateTime.now())
            .used(false)
            .build();
    }

    @Test
    @DisplayName("사용 가능한 쿠폰 목록 조회")
    void listAvailableCoupons() {
        // given
        when(couponRepository.findAll()).thenReturn(List.of(testCoupon));
        when(userCouponRepository.existsByCoupon_IdAndUserId(1L, 1L)).thenReturn(false);

        // when
        List<CouponDto> result = couponService.listAvailableCoupons(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("TEST100");
        assertThat(result.get(0).isAlreadyAssigned()).isFalse();
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void assignToUser_Success() {
        // given
        when(couponRepository.findByCode("TEST100")).thenReturn(Optional.of(testCoupon));
        when(userCouponRepository.existsByCoupon_IdAndUserId(1L, 1L)).thenReturn(false);
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(testUserCoupon);
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // when
        couponService.assignToUser("TEST100", 1L);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("중복 발급 시 예외 발생")
    void assignToUser_DuplicateAssignment() {
        // given
        when(couponRepository.findByCode("TEST100")).thenReturn(Optional.of(testCoupon));
        when(userCouponRepository.existsByCoupon_IdAndUserId(1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> couponService.assignToUser("TEST100", 1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 발급된 쿠폰입니다.");
    }

    @Test
    @DisplayName("쿠폰 사용 - 정율 할인")
    void redeem_PercentDiscount() {
        // given
        BigDecimal orderAmount = new BigDecimal("50000");
        when(couponRepository.findByCode("TEST100")).thenReturn(Optional.of(testCoupon));
        when(userCouponRepository.findByCoupon_CodeAndUserIdAndUsedFalse("TEST100", 1L))
            .thenReturn(Optional.of(testUserCoupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(testUserCoupon);

        // when
        RedemptionResult result = couponService.redeem("TEST100", 1L, orderAmount, null, null);

        // then
        assertThat(result.getDiscountApplied()).isEqualTo(new BigDecimal("5000")); // 10% of 50000 = 5000
        assertThat(result.getFinalAmount()).isEqualTo(new BigDecimal("45000"));
    }

    @Test
    @DisplayName("쿠폰 사용 - 최소 주문 금액 미달")
    void redeem_MinOrderAmountNotMet() {
        // given
        BigDecimal orderAmount = new BigDecimal("5000");
        when(couponRepository.findByCode("TEST100")).thenReturn(Optional.of(testCoupon));

        // when & then
        assertThatThrownBy(() -> couponService.redeem("TEST100", 1L, orderAmount, null, null))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("최소 주문 금액 미달입니다.");
    }
}