# 쿠폰 시스템 API 문서

## Base URL
```
http://localhost:8080/api
```

## API 엔드포인트

### 1. 쿠폰 목록 조회

#### Request
```http
GET /coupons?userId={userId}
```

#### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | Long | Yes | 사용자 ID |

#### Response
```json
[
    {
        "id": 1,
        "code": "WELCOME2025",
        "title": "신규 회원 환영 쿠폰",
        "label": "10% 할인",
        "validFrom": "2025-06-22T14:00:00",
        "validUntil": "2025-07-22T14:00:00",
        "type": "PERCENT",
        "discountValue": 10,
        "maxDiscountAmount": 5000.00,
        "minOrderAmount": 10000.00,
        "rewardPointPercent": 5,
        "maxUsages": 1,
        "remainingIssue": 999,
        "alreadyAssigned": false,
        "targetProductIds": null,
        "targetCategoryIds": null
    }
]
```

### 2. 쿠폰 발급

#### Request
```http
POST /coupons/assign
Content-Type: application/json
```

#### Request Body
```json
{
    "code": "WELCOME2025",
    "userId": 12345
}
```

#### Response
- **201 Created**: 쿠폰 발급 성공
- **409 Conflict**: 이미 발급된 쿠폰
- **400 Bad Request**: 잘못된 요청

### 3. 쿠폰 사용

#### Request
```http
POST /coupons/redeem
Content-Type: application/json
```

#### Request Body
```json
{
    "code": "WELCOME2025",
    "userId": 12345,
    "orderAmount": 50000,
    "productId": 1001,
    "categoryId": 10
}
```

#### Response
```json
{
    "finalAmount": 45000.00,
    "discountApplied": 5000.00,
    "rewardPointsEarned": 2500.00
}
```

## 에러 응답

### Error Response Format
```json
{
    "timestamp": "2025-06-22T14:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "최소 주문 금액 미달입니다.",
    "validationErrors": {
        "field": "error message"
    }
}
```

## 쿠폰 타입

| Type | Description |
|------|-------------|
| FIXED | 정액 할인 (고정 금액) |
| PERCENT | 정율 할인 (퍼센트) |

## 샘플 쿠폰 코드

애플리케이션 시작 시 자동으로 생성되는 쿠폰들:

| 코드 | 설명 | 할인 | 최소주문금액 | 유효기간 |
|------|------|------|------------|--------|
| WELCOME2025 | 신규 회원 환영 | 10% (최대 5천원) | 10,000원 | 30일 |
| BRANDDAY5000 | 브랜드 데이 | 5,000원 | 30,000원 | 7일 |
| TECH15OFF | 전자제품 전용 | 15% (최대 2만원) | 50,000원 | 14일 |
| VIP20SPECIAL | VIP 전용 | 20% (최대 5만원) | 100,000원 | 90일 |

## Postman Collection

Postman에서 사용할 수 있는 커렉션 예제:

```json
{
    "info": {
        "name": "Coupon System API",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
        {
            "name": "Get Available Coupons",
            "request": {
                "method": "GET",
                "header": [],
                "url": {
                    "raw": "http://localhost:8080/api/coupons?userId=1",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "coupons"],
                    "query": [
                        {
                            "key": "userId",
                            "value": "1"
                        }
                    ]
                }
            }
        },
        {
            "name": "Assign Coupon",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"code\": \"WELCOME2025\",\n    \"userId\": 1\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/api/coupons/assign",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "coupons", "assign"]
                }
            }
        },
        {
            "name": "Redeem Coupon",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"code\": \"WELCOME2025\",\n    \"userId\": 1,\n    \"orderAmount\": 50000\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/api/coupons/redeem",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "coupons", "redeem"]
                }
            }
        }
    ]
}
```