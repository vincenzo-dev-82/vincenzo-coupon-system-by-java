package com.vincenzo.coupon.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 발급된 쿠폰 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    /** 쿠폰을 발급받은 사용자 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 남은 사용 가능 횟수 */
    @Column(nullable = true)
    private Integer remainingUsages;

    /** 발급 일시 */
    @Column(nullable = false)
    private LocalDateTime assignedAt;

    /** 남은사용횟수 0 시 true */
    @Column(nullable = false)
    private Boolean used = false;
    
    @PrePersist
    public void prePersist() {
        if (this.used == null) {
            this.used = false;
        }
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
    }
}