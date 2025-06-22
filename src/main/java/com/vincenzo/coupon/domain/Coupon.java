package com.vincenzo.coupon.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 쿠폰 코드 (고유) */
    @Column(nullable = false, unique = true)
    private String code;

    /** UI에 표시할 제목 (예: "최대 26,000원 쿠폰 +5% M포인트 적립") */
    @Column(nullable = false)
    private String title;

    /** 배지 라벨 (예: "프로모션", "10% 할인") */
    @Column(nullable = false)
    private String label;

    /** 쿠폰 사용 가능 시작일시 */
    @Column(nullable = false)
    private LocalDateTime validFrom;

    /** 쿠폰 사용 가능 종료일시 */
    @Column(nullable = false)
    private LocalDateTime validUntil;

    /** 할인 타입: FIXED(정액), PERCENT(정율) */
    @Column(nullable = false)
    private String type;

    /** 할인 값: FIXED일 때는 금액(₩), PERCENT일 때는 퍼센트(%) */
    @Column(nullable = false)
    private Integer discountValue;

    /** 퍼센트 할인 시 최대 할인 한도 (₩) */
    @Column(nullable = true)
    private BigDecimal maxDiscountAmount;

    /** 최소 주문 금액(₩) */
    @Column(nullable = false)
    private BigDecimal minOrderAmount;

    /** 포인트 적립 비율(%) */
    @Column(nullable = true)
    private Integer rewardPointPercent;

    /** 전체 발급 가능한 매수 제한 (null=무제한) */
    @Column(nullable = true)
    private Integer maxIssueCount;

    /** 현재까지 발급된 매수 */
    @Column(nullable = false)
    private Integer issuedCount = 0;

    /** 쿠폰 사용 가능 횟수 제한 (null=무제한, 1=1회성, N=횟수 제한) */
    @Column(nullable = true)
    private Integer maxUsages;

    /** 대상 카테고리 ID 목록 (상품 장바구니 내 아이템의 categoryId와 매칭) */
    @ElementCollection
    @CollectionTable(name = "coupon_target_category", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "category_id")
    private Set<Long> targetCategoryIds;

    /** 대상 상품 ID 목록 (장바구니 내 item.productId와 매칭) */
    @ElementCollection
    @CollectionTable(name = "coupon_target_product", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "product_id")
    private Set<Long> targetProductIds;
    
    @PrePersist
    public void prePersist() {
        if (this.issuedCount == null) {
            this.issuedCount = 0;
        }
    }
}