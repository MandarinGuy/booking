# 인증 정책 (booking)

본 문서는 booking 프로젝트의 인증(Authentication) 동작과 규칙을 정리합니다. 모든 내용은 저장소의 실제 코드/설정에 근거합니다. 확인 불가한 항목은 "확인 불가"로 표기합니다.

근거 파일/경로:

- 보안 설정: `internal/src/main/java/org/mandarin/booking/adapter/SecurityConfig.java`
- JWT 필터: `internal/src/main/java/org/mandarin/booking/adapter/JwtFilter.java`
- AuthenticationProvider: `application/src/main/java/org/mandarin/booking/app/member/CustomAuthenticationProvider.java`
- 토큰 유틸: `internal/src/main/java/org/mandarin/booking/adapter/JwtTokenUtils.java`,
  `internal/src/main/java/org/mandarin/booking/adapter/TokenUtils.java`
- 예외 처리기: `internal/src/main/java/org/mandarin/booking/adapter/CustomAuthenticationEntryPoint.java`,
  `internal/src/main/java/org/mandarin/booking/adapter/CustomAccessDeniedHandler.java`
- 엔드포인트 권한 매칭:
  `application/src/main/java/org/mandarin/booking/adapter/security/ApplicationAuthorizationRequestMatcherConfigurer.java`
- 프로필/환경: `application/src/main/resources/application.yml`, `application-local.yml`, `application-test.yml`

---

## 1. 인증 흐름 개요

- 모든 `/api/**` 요청은 `@Order(1)` 체인의 보호를 받습니다. (근거: SecurityConfig.apiChain)
- 인증 헤더: `Authorization: Bearer <JWT>` (근거: JwtFilter)
- JwtFilter가 토큰을 파싱하여 사용자 식별자와 권한 정보를 추출하고, `AuthenticationManager`(= `CustomAuthenticationProvider`)를 통해 인증 토큰을 완성합니다.
- 세션 상태: Stateless (세션 생성 비활성화), CSRF 비활성화. (근거: SecurityConfig.apiChain 설정)

텍스트 시퀀스:

1) 클라이언트 → API: Authorization 헤더 전달
2) JwtFilter: `Bearer` 타입 여부 확인 → 토큰 파싱 → `userId`, `roles` 추출
3) JwtFilter: `CustomMemberAuthenticationToken(userId, authorities)` 생성 → `AuthenticationManager.authenticate(...)` 위임
4) CustomAuthenticationProvider: `MemberQueryRepository`로 사용자 조회 → `MemberDetails` 설정 → 인증 토큰 확정
5) SecurityContext에 인증 정보 저장 → 이후 필터/컨트롤러에서 사용

---

## 2. JWT 토큰 규칙

- 헤더 키: `Authorization`
- 포맷: `Bearer <token>`
- 사용 Claims (근거: JwtFilter):
  - `userId`: 사용자 식별자
  - `roles`: 문자열 리스트. 예: `["ROLE_USER", "ROLE_DISTRIBUTOR"]`
- 권한 매핑: JwtFilter는 `roles`에서 `ROLE_` 접두사를 제거한 뒤 `MemberAuthority` enum으로 변환하여 `CustomMemberAuthenticationToken`에
  부여합니다. (근거: JwtFilter.getAuthorities)
- 토큰 서명/TTL 설정: 프로필 별 설정 사용
  - `jwt.token.secret`: 서명 시크릿(Base64 인코딩 값)
  - `jwt.token.access`: Access Token 만료(ms)
  - `jwt.token.refresh`: Refresh Token 만료(ms)
  - 근거: `application-local.yml`, `application-test.yml`

주의:

- 헤더가 없거나 `Bearer` 접두사만 온 경우 익명 처리되며, 요청 속성 `exception`에 `AuthException`이 설정됩니다. (근거: JwtFilter.isTokenBlank,
  doFilterInternal)
- 서명 오류/만료/클레임 파싱 실패 시 `AuthException`을 설정하고 SecurityContext를 비웁니다. (근거: JwtFilter 예외 처리)

---

## 3. 인증 컴포넌트

- JwtFilter
  - 위치/순서: `UsernamePasswordAuthenticationFilter` 앞 (근거: SecurityConfig.addFilterBefore)
  - 역할: Authorization 헤더 파싱, 사용자 정보 추출, AuthenticationManager 위임, SecurityContext 설정
- CustomAuthenticationProvider
  - 지원 타입: `CustomMemberAuthenticationToken` (근거: supports)
  - 동작: 토큰 내 userId로 회원 조회(`MemberQueryRepository.findByUserId`) → 없으면 `AuthException` → 있으면 `MemberDetails` 주입, 인증 확정
- TokenUtils
  - 역할: JWT에서 개별 클레임/리스트 클레임 추출 (근거: JwtFilter에서 사용)

---

## 4. 인증 예외 처리

- 인증 실패(미인증) 시: `CustomAuthenticationEntryPoint`가 응답 생성 (상세 형식은 클래스 구현 참고)
- 인가 실패(권한 부족) 시: `CustomAccessDeniedHandler`가 응답 생성
- JwtFilter는 내부적으로 `request.setAttribute("exception", new AuthException(...))`로 실패 사유를 넘기며, 이후 예외 처리기가 이를 사용해 응답을 형성할 수
  있습니다.

---

## 5. 공개 엔드포인트와 인증 필요 엔드포인트

- 공개(permitAll): (근거: SecurityConfig.apiChain, ApplicationAuthorizationRequestMatcherConfigurer)
  - `POST /api/member`
  - `POST /api/auth/login`
  - `POST /api/auth/reissue`
  - `GET /api/show`, `GET /api/show/*`
- 인증/권한 필요: (근거: ApplicationAuthorizationRequestMatcherConfigurer)
    - `POST /api/show` → `ROLE_ADMIN`
    - `POST /api/show/schedule` → `ROLE_DISTRIBUTOR`
    - 그 외 `/api/**` → 인증 필요
- 공개 체인(@Order(2)): (근거: SecurityConfig.publicChain)
  - `/error`, `/assets/**`, `/favicon.ico` 및 그 외 `/**`는 permitAll (운영상 공개 페이지용)

---

## 6. 테스트/프로필 연계

- 테스트 실행 시 프로필 `test` 활성화: Gradle test 태스크에서 설정 (근거: build.gradle)
- 테스트 프로필에서 JWT/DB 설정은 `application-test.yml`을 따른다.
- 보안 관련 테스트는 다음을 참조: `application/src/test/java/org/mandarin/booking/adapter/security/*`,
  `internal/src/test/java/org/mandarin/booking/adapter/*Test.java`

---

## 7. 확장 가이드(인증)

- 새로운 인증 스킴 도입 시 지켜야 할 규칙:
  - JwtFilter 앞/뒤 필터 추가 시 순서 충돌 주의. 인증 헤더 파싱 필터는 반드시 UsernamePasswordAuthenticationFilter 이전.
  - CustomAuthenticationProvider는 `supports`/`authenticate` 계약을 준수하여 `Authentication` 토큰 타입을 명확히 구분.
  - 토큰 클레임 확장 시: `TokenUtils`와 `JwtFilter.getAuthorities()` 동기화. 권한 문자열은 `ROLE_` 접두사를 유지.
- 운영 비밀: 시크릿/TTL 값은 환경변수로 덮어쓰기를 권장. (application-prod.yml은 현재 비어 있음 → 확인 불가)

---

## 8. 알 수 없는 항목(확인 불가)

- 토큰 발급/서명 구현 세부(Access/Refresh 생성 로직) 문서화 수준: 확인 불가 (해당 클래스 상세는 별도 코드 참조 필요)
- 키 회전/블랙리스트/토큰 철회 전략: 확인 불가
