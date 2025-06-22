# 기여 가이드

## 프로젝트에 기여하기

이 프로젝트에 기여해주셔서 감사합니다! 다음 가이드라인을 따라주세요.

## 개발 환경 설정

1. 프로젝트를 Fork합니다.
2. 로컬에 클론합니다:
```bash
git clone https://github.com/your-username/vincenzo-coupon-system-by-java.git
cd vincenzo-coupon-system-by-java
```

3. 개발 환경을 설정합니다:
```bash
./gradlew build
```

## 코드 스타일 가이드

### Java 코드 컨벤션

- Java 17 기능을 적극 활용합니다.
- 클래스명은 PascalCase를 사용합니다.
- 메서드명과 변수명은 camelCase를 사용합니다.
- 상수는 UPPER_SNAKE_CASE를 사용합니다.
- 들여쓰기는 공백 4개를 사용합니다.

### 코드 품질

- 모든 public 메서드에는 JavaDoc을 작성합니다.
- 단위 테스트를 작성합니다.
- 코드 커버리지 80% 이상을 유지합니다.

### 패키지 구조

```
com.vincenzo.coupon
├── config/         # 설정 클래스
├── controller/    # REST 컨트롤러
├── domain/        # 엔티티 클래스
├── dto/           # 데이터 전송 객체
├── exception/     # 예외 처리
├── repository/    # JPA 레포지토리
└── service/       # 비즈니스 로직
```

## 커밋 메시지 규칙

### 커밋 타입

- `feat:` 새로운 기능 추가
- `fix:` 버그 수정
- `docs:` 문서 수정
- `style:` 코드 포맷팅, 세미콜론 누락 등
- `refactor:` 코드 리팩토링
- `test:` 테스트 추가 또는 수정
- `chore:` 빌드 프로세스 또는 보조 도구 변경

### 커밋 메시지 예시

```
feat: 카테고리별 쿠폰 필터링 기능 추가

- 쿠폰 조회 시 카테고리 ID로 필터링 가능
- CouponService에 findByCategoryId 메서드 추가
- 관련 테스트 케이스 추가
```

## Pull Request 프로세스

1. 새로운 브랜치를 생성합니다:
```bash
git checkout -b feature/your-feature-name
```

2. 변경사항을 커밋합니다:
```bash
git commit -m "feat: your feature description"
```

3. 브랜치를 푸시합니다:
```bash
git push origin feature/your-feature-name
```

4. GitHub에서 Pull Request를 생성합니다.

### PR 체크리스트

- [ ] 코드가 컴파일되고 테스트를 통과합니다
- [ ] 모든 테스트가 성공합니다
- [ ] 커밋 메시지가 규칙을 따릅니다
- [ ] 필요한 경우 문서를 업데이트했습니다
- [ ] 새로운 기능에 대한 테스트를 추가했습니다

## 테스트 작성

### 단위 테스트

```java
@Test
@DisplayName("쿠폰 발급 시 발급 카운트가 증가해야 한다")
void assignCoupon_ShouldIncreaseIssuedCount() {
    // given
    Coupon coupon = createTestCoupon();
    
    // when
    couponService.assignToUser("TEST100", 1L);
    
    // then
    assertThat(coupon.getIssuedCount()).isEqualTo(1);
}
```

### 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests CouponServiceTest
```

## 버그 리포트

버그를 발견하신 경우:

1. GitHub Issues에 새로운 이슈를 생성합니다.
2. 버그를 재현할 수 있는 최소한의 코드를 포함합니다.
3. 예상되는 동작과 실제 동작을 설명합니다.
4. 환경 정보(OS, Java 버전 등)를 포함합니다.

## 라이선스

기여하신 코드는 MIT 라이선스로 배포됩니다.

## 질문이나 도움이 필요한 경우

GitHub Issues나 Discussions를 통해 언제든 질문해주세요!