# booking — 공연 예매 시스템 (개요 중심 README)

- 저장소 루트: 단일 모듈(Spring Boot) 프로젝트

---

## 1. 프로젝트 소개

공연 예매(Booking) 도메인을 다루는 학습 목적의 Spring Boot 애플리케이션입니다. 헥사고날 아키텍처(Ports & Adapters)를 채택하여 도메인 규칙을 프레임워크로부터 분리하고, 보안/웹/영속성
같은 어댑터 계층을 통해 외부 세계와 상호작용합니다.

- 도메인 개요: [docs/specs/domain.md](docs/specs/domain.md)
- 아키텍처/개발 규칙: [docs/specs/policy/application.md](docs/specs/policy/application.md)
- 테스트 규칙: [docs/specs/policy/test.md](docs/specs/policy/test.md)

---

## 2. 핵심 기능

- 추후 작성 예정

테스트로 검증되는 수용 기준은 각 API 문서 하단의 체크리스트를 참고하세요.

---

## 3. 아키텍처 개요 (Hexagonal)

헥사고날 아키텍처를 일부 차용한 모듈 구조를 채택했습니다. 각 모듈의 책임은 다음과 같습니다.

- internal: 애플리케이션 내부의 생태계를 관리한다. 직접적인 비즈니스 관리영역이 아닌 '애플리케이션' 자체를 관리한다. 로그 설정, web 설정, 보안 설정등 비즈니스 요구사항을 직접적으로 나타내지 않는
  구현들이 존재한다.
- external: 외부 세계와의 통신을 담당한다. 도메인 로직은 물론 애플리케이션과도 완전 독립적인 모듈이다. MQ, STMP등등에 대한 기능의 구현이 존재한다.
- domain: 비즈니스 영역의 핵심이 되는 영역이다. 비즈니스를 해결하기 위한 도메인 그 자체를 의미하며 도메인 개념을 로직으로 풀어나가는 영역이다. Entity와 통신 객체들이 여기에 해당한다.
- common: 공통코드들을 관리한다. 파급효과가 가장 큰 영역인 만큼 라이브러리 사용을 방지하고 POJO 스타일을 원칙으로 한다. 상수와 type object들이 존재한다.
- application: 모든 영역들을 통합해 애플리케이션을 만들어 관리한다. Spring boot의 main class가 존재하며, 각 모듈들을 통합해 비즈니스 요구사항을 해결한다. 비즈니스 로직을 해결하는
  영역과 이를 전달하는 영역으로 대부분의 Service 영역과 Controller영역, 그리고 통합 테스트가 존재한다.

- 근거와 세부 규칙

- 정책 문서: [docs/specs/policy](docs/specs/policy)
- 레이어 테스트: `application/src/test/java/org/mandarin/booking/arch/ModuleDependencyRulesTest.java`
- 패키지 구조 예
  - 도메인: `src/main/java/org/mandarin/booking/domain/*`
  - 앱/포트/영속 어댑터: `src/main/java/org/mandarin/booking/app/*`
  - 웹/보안 어댑터: `src/main/java/org/mandarin/booking/adapter/{webapi,security}/*`

텍스트 다이어그램: [Controllers/Security/External] → adapter → app(ports, services) → domain

---

## 4. 도메인 모델 요약

- Show (Aggregate Root): 제목, 감독, 장르, 상영시간, 개봉일, 등급, 줄거리, 포스터URL, 출연진 등. 팩토리/커맨드 기반 생성.
- Member (Aggregate Root): 닉네임, userId, email, passwordHash, 권한 목록. 비밀번호 해시 일치 검증.
- Hall (Entity): 상영관 이름, 좌석 배치(행/열), 총 좌석 수.

자세한 속성과 규칙: [docs/specs/domain.md](docs/specs/domain.md)

---

## 5. 기술 스택과 선택 근거

- 추후 작성 예정

선택 이유(요지)

- Hexagonal: 테스트 용이성과 변경 격리를 위해 계층 경계를 명확히. 또한, 추후 모듈화 or MSA 전환시 이점을 위해 애플리케이션 아키텍처를 영역에 따라 구분.
- Spring Security + JWT: 무상태(stateless) API 인증과 확장성.
- JPA + RDB(H2/MySQL): 표준 ORM과 빠른 테스트 사이클.
- Spring Modulith: 명확한 Bounded Context 경계 분리 및 추수 MSA 전환 대비

---

## 6. 개발 방식과 테스트 전략

- 테스트 주도 개발(TDD) 지향: 테스트 우선, 기능 추가 시 관련 스펙 테스트 동반.
- 테스트 정책 문서: [docs/specs/policy/test.md](docs/specs/policy/test.md)
- 통합 테스트: Spring Context 기동, 보안 필터/컨트롤러/JPA 연동을 포함한 경로 검증.
  - 예시: `src/test/java/org/mandarin/booking/webapi/**/POST_specs.java`
- 모듈 구조 테스트: 모듈간 의존관계 테스트
    - 예시: `application/src/test/java/org/mandarin/booking/arch/ModuleDependencyRulesTest.java`

Build/Test 구성 근거: `build.gradle`의 `tasks.named('test')` 설정(Profiles, JUnit Platform, ByteBuddy javaagent).

---

## 7. 보안 개요

- 필터 기반 JWT 인증: `JwtFilter`가 Authorization `Bearer <token>`을 파싱해 SecurityContext 설정.
- 경로별 권한: `SecurityConfig`의 `@Order(1) apiChain`
    - 차후 추가 작성
- 예외 처리: `CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`

근거: `src/main/java/org/mandarin/booking/adapter/security/*`

---

## 8. 데이터/환경 구성

- 프로필: `local`(기본), `test`, `prod(비어있음)`
  - 근거: `src/main/resources/application.yml` 및 `application-*.yml`
- local: MySQL + JPA `ddl-auto: create`, JWT 시크릿/TTL 설정
    - 근거: `application-local.yml`, Docker Compose: [compose.yaml](application/src/main/resources/compose.yaml)
- test: H2 메모리 + MySQL 호환 모드 + JPA `ddl-auto: create`
  - 근거: `application-test.yml`

민감정보는 운영 환경에서 환경변수로 주입하는 것을 권장합니다(로컬에 예시 값 존재).

---

## 9. API 문서

- 로그인: [docs/specs/api/login.md](docs/specs/api/login.md)
- 회원 가입: [docs/specs/api/member_register.md](docs/specs/api/member_register.md)
- 토큰 재발급: [docs/specs/api/reissue.md](docs/specs/api/reissue.md)
- 공연 등록: [docs/specs/api/show_register.md](docs/specs/api/show_register.md)

각 문서 하단의 테스트 체크리스트가 수용 기준입니다.

---

## 10. 프로젝트 상태 및 향후 계획

- CI/CD, 코드 포매터, 마이그레이션 도구(Flyway/Liquibase)는 현재 문서/설정 부재로 "확인 불가" 상태입니다.
- TODO/메모: [docs/devlog/*](docs/devlog), [docs/todo.md](docs/todo.md)
- 권장 향후 작업
  - prod 프로필 구성과 비밀 주입 전략 수립
  - CI 파이프라인(.github/workflows) 도입
  - DB 마이그레이션 도구 채택 및 규약 수립
  - 인증/인가 정책 문서
    구체화: [docs/specs/policy/authentication.md](docs/specs/policy/authentication.md), [docs/specs/policy/authorization.md](docs/specs/policy/authorization.md)

---

## 11. 버전/도구 근거 링크

- Spring Boot/Java/Gradle
  버전: [build.gradle](application/build.gradle), [gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties)
- 애플리케이션 엔트리포인트: `src/main/java/org/mandarin/booking/BookingApplication.java`
- 보안 설정/필터: `src/main/java/org/mandarin/booking/adapter/security/SecurityConfig.java`,
  `src/main/java/org/mandarin/booking/adapter/security/JwtFilter.java`
- 아키텍처 규칙: [docs/specs/policy/application.md](docs/specs/policy/application.md)
- 테스트 정책: [docs/specs/policy/test.md](docs/specs/policy/test.md)
