# jichul

## 기술 스택

| 항목                | 버전     |
|-------------------|--------|
| Java              | 21     |
| Spring Boot       | 3.5.11 |
| Spring Security   | -      |
| Spring Data JPA   | -      |
| Spring Data Redis | -      |
| PostgreSQL        | 16     |
| Redis (Sentinel)  | -      |
| jjwt              | 0.12.6 |
| Lombok            | -      |
| Gradle            | 9.x    |

## 프로젝트 구조

```
src/main/java/work/seoeungi/jichul/
├── JichulApplication.java
│
├── auth/                          # 인증
│   ├── AuthController.java        # 회원가입, 로그인, 로그아웃, 토큰 갱신
│   ├── AuthService.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── TokenRefreshRequest.java
│   └── jwt/
│       ├── JwtProvider.java       # 토큰 발급, 검증, 블랙리스트
│       └── JwtAuthFilter.java     # 요청마다 토큰 검증 필터
│
├── common/
│   ├── exception/
│   │   ├── AppException.java
│   │   ├── ErrorCode.java
│   │   └── GlobalExceptionHandler.java
│   └── response/
│       └── ApiResponse.java       # 공통 응답 래퍼
│
├── config/
│   └── SecurityConfig.java        # Spring Security 설정, CORS
│
└── domain/
    ├── user/                      # 사용자
    │   ├── User.java
    │   ├── UserRepository.java
    │   ├── UserService.java
    │   └── dto/
    ├── provider/                  # 구독 제공사
    │   ├── Provider.java
    │   ├── ProviderController.java
    │   ├── ProviderRepository.java
    │   ├── ProviderService.java
    │   └── dto/
    └── subscription/              # 구독 항목
        ├── Subscription.java
        ├── SubscriptionController.java
        ├── SubscriptionRepository.java
        ├── SubscriptionService.java
        ├── SubscriptionType.java  # MONTHLY / YEARLY
        └── dto/
```

## API 엔드포인트

### 인증 (`/api/auth`)

| 메서드  | 경로                   | 인증  | 설명        |
|------|----------------------|-----|-----------|
| POST | `/api/auth/register` | 불필요 | 회원가입      |
| POST | `/api/auth/login`    | 불필요 | 로그인       |
| POST | `/api/auth/refresh`  | 불필요 | 액세스 토큰 갱신 |
| POST | `/api/auth/logout`   | 필요  | 로그아웃      |
| GET  | `/api/auth/me`       | 필요  | 내 정보 조회   |

### 제공사 (`/api/providers`)

| 메서드    | 경로                    | 설명        |
|--------|-----------------------|-----------|
| GET    | `/api/providers`      | 제공사 목록 조회 |
| POST   | `/api/providers`      | 제공사 등록    |
| PUT    | `/api/providers/{id}` | 제공사 수정    |
| DELETE | `/api/providers/{id}` | 제공사 삭제    |

### 구독 (`/api/subscriptions`)

| 메서드    | 경로                           | 설명        |
|--------|------------------------------|-----------|
| GET    | `/api/subscriptions`         | 구독 목록 조회  |
| GET    | `/api/subscriptions/summary` | 월/연 지출 요약 |
| POST   | `/api/subscriptions`         | 구독 등록     |
| PUT    | `/api/subscriptions/{id}`    | 구독 수정     |
| DELETE | `/api/subscriptions/{id}`    | 구독 삭제     |

## 공통 응답 형식

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

오류 시:

```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지"
}
```

## JWT 인증 흐름

- **액세스 토큰** 유효 기간: 30분
- **리프레시 토큰** 유효 기간: 7일, Redis에 저장
- **Refresh Token Rotation**: 갱신 시 기존 리프레시 토큰을 삭제하고 새 토큰 발급
- **로그아웃**: 액세스 토큰을 Redis 블랙리스트에 등록, 리프레시 토큰 전체 삭제
- 만료/미인증 요청 → **401**, 권한 부족 → **403**