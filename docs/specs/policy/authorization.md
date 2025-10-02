# 인가 정책 (booking)

본 문서는 booking 프로젝트의 인가(Authorization) 규칙을 경로/메서드/권한 기준으로 명확히 기술합니다. 모든 내용은 실제 보안 설정 코드에 근거합니다.

근거 파일/경로:

- 보안 설정: `internal/src/main/java/org/mandarin/booking/adapter/SecurityConfig.java`
- 권한 Enum: `common/src/main/java/org/mandarin/booking/MemberAuthority.java`
- JWT 필터: `internal/src/main/java/org/mandarin/booking/adapter/JwtFilter.java`
- 권한 매칭 구성:
  `application/src/main/java/org/mandarin/booking/adapter/security/ApplicationAuthorizationRequestMatcherConfigurer.java`
- 예외 처리기: `internal/src/main/java/org/mandarin/booking/adapter/CustomAccessDeniedHandler.java`,
  `internal/src/main/java/org/mandarin/booking/adapter/CustomAuthenticationEntryPoint.java`

---

## 1. 기본 원칙

- 인가는 Spring Security의 `SecurityFilterChain` 규칙으로 정의됩니다.
- 권한 문자열은 `ROLE_` 접두사를 가진 형태로 JWT `roles` 클레임에 담깁니다. (예: `ROLE_USER`, `ROLE_DISTRIBUTOR`, `ROLE_ADMIN`) (근거:
  JwtFilter.getAuthorities, MemberAuthority)

---

## 2. 경로/메서드 별 인가 규칙

아래 표는 `SecurityConfig.apiChain`의 `authorizeHttpRequests` 설정을 반영합니다.

- 공개(permitAll):
  - `POST /api/member`
  - `POST /api/auth/login`
  - `POST /api/auth/reissue`
  - `GET /api/show`, `GET /api/show/*`

- 권한 필요(hasAuthority):
    - `POST /api/hall` → `ROLE_ADMIN`
    - `POST /api/show` → `ROLE_ADMIN`
    - `POST /api/show/schedule` → `ROLE_DISTRIBUTOR`

- 그 외 `/api/**`:
    - `anyRequest().authenticated()` → 유효한 JWT 필요(특정 권한 제한 없음)

- 퍼블릭 체인(@Order(2)):
  - `/error`, `/assets/**`, `/favicon.ico`, 및 그 외 `/**`는 permitAll (정적/오류/기타 공개 경로)

근거 코드 스니펫 요약:

- `http.securityMatcher("/api/**")`
- `.requestMatchers(HttpMethod.POST, "/api/member").permitAll()`
- `.requestMatchers("/api/auth/login").permitAll()`
- `.requestMatchers("/api/auth/reissue").permitAll()`
- `.requestMatchers(HttpMethod.GET, "/api/show").permitAll()`
- `.requestMatchers(HttpMethod.GET, "/api/show/*").permitAll()`
- `.requestMatchers(HttpMethod.POST, "/api/show/schedule").hasAuthority("ROLE_DISTRIBUTOR")`
- `.requestMatchers(HttpMethod.POST, "/api/show").hasAuthority("ROLE_ADMIN")`
- `.requestMatchers(HttpMethod.POST, "/api/hall").hasAuthority("ROLE_ADMIN")`
- `.anyRequest().authenticated()`

---

## 3. 권한 명명 규칙과 매핑

- Enum: `MemberAuthority`는 다음 권한을 가집니다(코드 참조).
  - USER, DISTRIBUTOR, ADMIN 등
- JWT `roles` → Spring Security 권한 변환: JwtFilter는 `roles`에서 `ROLE_` 접두사를 제거한 후 `MemberAuthority.valueOf(...)`로 Enum을 만들어
  `CustomMemberAuthenticationToken`에 저장합니다. (근거: JwtFilter.getAuthorities)
- hasAuthority 비교 시에는 문자열 `ROLE_*` 형태를 사용합니다. (근거: SecurityConfig 설정)

---

## 4. 예외/오류 처리

- 인증 실패(401 Unauthorized): `CustomAuthenticationEntryPoint`가 처리
- 권한 부족(403 Forbidden): `CustomAccessDeniedHandler`가 처리
- JwtFilter는 유효하지 않은 토큰, 누락된 토큰 등에 대해 `request.setAttribute("exception", AuthException)`을 설정하여 원인 정보를 예외 처리기로 전달합니다.

---

## 5. 확장 가이드(인가 규칙 추가 방법)

- 새로운 엔드포인트 추가 시 규칙 예시:
  - 공개 엔드포인트(회원가입/로그인 유사): `.requestMatchers(HttpMethod.POST, "/api/xxx").permitAll()`
  - 역할 제한 엔드포인트: `.requestMatchers(HttpMethod.PUT, "/api/show/{id}").hasAuthority("ROLE_ADMIN")`
  - 복수 권한 허용: `.requestMatchers(HttpMethod.POST, "/api/screening").hasAnyAuthority("ROLE_DISTRIBUTOR", "ROLE_ADMIN")`
- 규칙 배치 위치: `SecurityConfig.apiChain`의 `authorizeHttpRequests` 빌더에 메서드/경로/권한을 추가합니다.
- 테스트: 추가/변경 시 반드시 보안 통합 테스트를 작성하여 401/403, 성공 경로를 검증하십시오. (예: `adapter/security/*Test.java`, `webapi/**` 스펙 테스트)

---

## 6. 알 수 없는 항목(확인 불가)

- 경로 별 세부 권한 정책 문서(도메인별 Role Matrix): 현재 저장소에 상세 표 없음 → 확인 불가
- 동적 권한(도메인 데이터 소유권 기반 세분화) 정책: 확인 불가
