package com.vincenzo.coupon.repository;

import com.vincenzo.coupon.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    boolean existsByCoupon_IdAndUserId(Long couponId, Long userId);
    Optional<UserCoupon> findByCoupon_CodeAndUserIdAndUsedFalse(String code, Long userId);
}