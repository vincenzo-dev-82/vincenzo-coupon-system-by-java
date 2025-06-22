# 쿠폰 생성 및 발급 시스템 (Coupon Management System)

## 📋 프로젝트 개요

Spring Boot 3와 Java 17을 기반으로 구축된 쿠폰 관리 시스템입니다. 이 시스템은 다양한 유형의 쿠폰 생성, 발급, 사용을 관리하며, 실제 이커머스 환경에서 활용 가능한 수준의 기능을 제공합니다.

## 🚀 주요 기능

### 1. 쿠폰 관리
- **쿠폰 생성**: 정액/정율 할인 쿠폰 생성
- **유효기간 설정**: 시작일과 종료일 설정
- **발급 수량 제한**: 최대 발급 가능 수량 설정
- **사용 횟수 제한**: 쿠폰별 사용 가능 횟수 설정

### 2. 할인 정책
- **정액 할인**: 고정 금액 할인
- **정율 할인**: 퍼센트 할인 (최대 할인 한도 설정 가능)
- **최소 주문 금액**: 쿠폰 사용을 위한 최소 구매 금액 설정
- **카테고리/상품 제한**: 특정 카테고리나 상품에만 사용 가능

### 3. 포인트 적립
- 쿠폰 사용 시 추가 포인트 적립 기능
- 적립율 설정 가능

### 4. 사용자 쿠폰 관리
- **중복 발급 방지**: 사용자당 1개의 쿠폰만 발급
- **사용 이력 관리**: 쿠폰 사용 내역 추적
- **남은 사용 횟수 확인**: 다회 사용 가능 쿠폰의 잔여 횟수 관리

## 🛠 기술 스택

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: H2 (개발), PostgreSQL/MySQL (운영)
- **ORM**: JPA/Hibernate
- **API**: RESTful API

## 📋 요구사항

- JDK 17 이상
- Gradle 8.x

## 🚀 시작하기

### 1. 프로젝트 클론
```bash
git clone https://github.com/vincenzo-dev-82/vincenzo-coupon-system-by-java.git
cd vincenzo-coupon-system-by-java
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. H2 Console 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (비워두기)

## 📌 API 엔드포인트

### 쿠폰 목록 조회
```
GET /api/coupons?userId={userId}
```
사용자가 사용 가능한 쿠폰 목록을 조회합니다.

### 쿠폰 발급
```
POST /api/coupons/assign
Content-Type: application/json

{
  "code": "WELCOME2025",
  "userId": 12345
}
```

### 쿠폰 사용
```
POST /api/coupons/redeem
Content-Type: application/json

{
  "code": "WELCOME2025",
  "userId": 12345,
  "orderAmount": 50000,
  "productId": 1001,
  "categoryId": 10
}
```

## 📊 데이터베이스 스키마

### Coupon 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| code | VARCHAR | 쿠폰 코드 (Unique) |
| title | VARCHAR | 쿠폰 제목 |
| label | VARCHAR | 배지 라벨 |
| valid_from | TIMESTAMP | 유효 시작일 |
| valid_until | TIMESTAMP | 유효 종료일 |
| type | VARCHAR | 할인 타입 (FIXED/PERCENT) |
| discount_value | INTEGER | 할인 값 |
| max_discount_amount | DECIMAL | 최대 할인 한도 |
| min_order_amount | DECIMAL | 최소 주문 금액 |
| reward_point_percent | INTEGER | 포인트 적립율 |
| max_issue_count | INTEGER | 최대 발급 수 |
| issued_count | INTEGER | 현재 발급 수 |
| max_usages | INTEGER | 최대 사용 횟수 |

### UserCoupon 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| coupon_id | BIGINT | FK to Coupon |
| user_id | BIGINT | 사용자 ID |
| remaining_usages | INTEGER | 남은 사용 횟수 |
| assigned_at | TIMESTAMP | 발급 일시 |
| used | BOOLEAN | 사용 완료 여부 |

## 📝 비즈니스 정책

### 1. 쿠폰 발급 정책
- 동일한 쿠폰은 사용자당 1회만 발급 가능
- 발급 한도가 설정된 경우, 한도 초과 시 발급 불가
- 유효기간 내에만 발급 가능

### 2. 쿠폰 사용 정책
- 유효기간 내에만 사용 가능
- 최소 주문 금액 이상일 때만 사용 가능
- 지정된 카테고리/상품에만 사용 가능 (설정된 경우)
- 사용 횟수 제한이 있는 경우, 남은 횟수만큼만 사용 가능

### 3. 할인 계산 정책
- 정액 할인: 설정된 금액만큼 할인
- 정율 할인: 주문 금액의 설정된 비율만큼 할인 (최대 할인 한도 적용)

## 🔧 확장 가능한 기능

1. **동시성 제어**: 대량 트래픽 상황에서의 쿠폰 발급 동시성 처리
2. **쿠폰 조합**: 여러 쿠폰의 동시 사용 및 우선순위 설정
3. **회원 등급별 쿠폰**: 회원 등급에 따른 차별화된 쿠폰 발급
4. **쿠폰 알림**: 쿠폰 만료 임박 알림, 신규 쿠폰 발급 알림
5. **통계 및 분석**: 쿠폰 사용 현황, 효과 분석 대시보드

## 📄 라이선스

This project is licensed under the MIT License.

## 👥 기여하기

프로젝트 개선에 기여하고 싶으시다면 PR을 보내주세요!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request