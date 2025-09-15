# 테스트 정책 (booking)

본 문서는 booking 프로젝트의 테스트 작성/실행 표준을 정의합니다. 실제 저장소의 테스트 코드, Gradle 설정, 애플리케이션 프로필을 근거로 수립되었습니다. 이 문서는 코드 변경 시 항상 최신 상태로
유지되어야 하며, 테스트 실패는 정책 위반으로 간주할 수 있습니다.

근거 파일/경로:

- build.gradle: test 태스크, 라이브러리, JVM args 설정
- src/main/resources/application-test.yml: 테스트 프로필 환경
- src/test/java/**/*: 실제 테스트 코드 일체
- docs/specs/api/*.md: API별 수용 기준(체크리스트)

---

## 1. 목표와 범위

- 목표: 기능/아키텍처/보안/도메인 규칙을 신뢰성 있게 검증한다.
- 범위: 단위 테스트(Unit), 통합 테스트(Integration), 아키텍처 테스트(ArchUnit) 전반.
- 테스트 실행 환경은 Gradle test 태스크를 표준으로 한다.

명령:

- 전체 테스트: `./gradlew test`
- 테스트 프로필: Gradle가 자동으로 `spring.profiles.active=test`를 설정함 (build.gradle 근거).

---

## 2. 테스트 종류와 원칙

### 2.1 단위 테스트 (Unit Test)

- 목적: 작은 단위(도메인, 유틸, 애플리케이션 서비스의 순수 로직)의 동작을 빠르고 고립적으로 검증.
- 프레임워크 의존: 가급적 없음. Spring Context를 기동하지 않는다.
- 목킹(Mock): 외부 의존성은 Mockito 등으로 대체. 저장소/네트워크/시큐리티 등 I/O 경계를 모킹한다.
- 예시 근거:
  - 도메인: `src/test/java/org/mandarin/booking/domain/MemberTest.java`, `AbstractEntityTest.java`
  - 보안 컴포넌트 단위: `adapter/security/JwtFilterTest.java`, `CustomAuthenticationProviderTest.java`,
    `CustomAuthenticationEntryPointTest.java`
  - 공통/web 단위: `adapter/webapi/GlobalExceptionHandlerTest.java`
- 라이브러리/설정 근거:
  - Mockito inline 사용: `build.gradle` → `testImplementation 'org.mockito:mockito-inline:5.2.0'`
  - JUnit5 사용: `build.gradle` → `useJUnitPlatform()`
  - ByteBuddy javaagent 사전 부착: `build.gradle` → `jvmArgs "-javaagent:${configurations.byteBuddyAgent.singleFile}"`

권장 규칙:

- 네이밍: 테스트 클래스는 대상 클래스명 + `Test` 또는 시나리오 중심 스펙 명(`*Specs`)을 사용 가능.
- 패키지: 테스트 대상과 유사한 패키지 경로 하위에 배치하여 접근성을 높인다.
- given-when-then 주석 또는 메서드명으로 시나리오를 명확히 표현한다.
- 외부 시스템/DB 액세스 금지. 필요한 경우 포트/리포지토리를 모킹.

### 2.2 통합 테스트 (Integration Test)

- 목적: Spring Context를 실제로 기동하여, 보안 필터/컨트롤러/시리얼라이저/예외 처리 및 JPA/H2 동작을 포함해 엔드투엔드에 가까운 경로를 검증.
- 프로필/환경: `application-test.yml` 사용. H2 메모리 DB, JPA `ddl-auto: create`, JWT 시크릿/TTL 설정 포함.
- 보안: 실제 `SecurityConfig`와 `JwtFilter` 동작을 최대한 반영. 필요 시 테스트 전용 컨트롤러/설정 (`TestOnlyController`, `TestConfig`) 사용.
- 유틸리티: `IntegrationTest`, `IntegrationTestUtils`, `IntegrationTestUtilsSpecs`, `JwtTestUtils` 등 공용 유틸을 통해 테스트 준비/토큰
  생성/컨텍스트 초기화.
- 예시 근거:
  - 웹 API 스펙 테스트: `src/test/java/org/mandarin/booking/webapi/**/POST_specs.java`
  - 통합 환경 유틸: `src/test/java/org/mandarin/booking/IntegrationTest*.java`, `JwtTestUtils.java`
    권장 규칙:
- `@IntegrationTest` 커스텀 어노테이션 사용으로 공통 설정
- 각 테스트는 `IntegrationTestUtils`를 사용해 작성
  - `IntegrationTestUtils` 사용 방법은 다음과 같음
  - ```java
    @Test
    void withoutAuth(@Autowired IntegrationTestUtils testUtils) {
    // Act & Assert
    var response = testUtils.get("/test/without-auth")
    .assertSuccess(String.class);
    
            assertThat(response.getData()).isEqualTo(PONG_WITHOUT_AUTH);
    }
    ```
    - ```java
    @Test
    void failToAuth(@Autowired IntegrationTestUtils testUtils) {
        // Arrange
        var invalidToken = "invalid token";

        // Act & Assert
        var response = testUtils.get("/test/with-auth")
                .withAuthorization(invalidToken)
                .assertFailure();
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getData()).isEqualTo("유효한 토큰이 없습니다.");
    }
    ```
- 데이터 초기화는 테스트 메서드 단위로 독립되게 유지. H2 메모리 DB가 매 테스트 클래스/메서드 기준으로 깨끗한 상태를 갖도록 설계한다.
- 인증이 필요한 엔드포인트는 `JwtTestUtils`로 유효 토큰을 발급하여 헤더 `Authorization: Bearer <token>`를 부착.
- 예외/에러 응답은 `GlobalExceptionHandler` 정책에 맞춰 상태코드/본문을 검증.
- 아키텍처적으로 adapter → app → domain 경로를 실제 호출하여 레이어 간 계약을 검증.
- 검증은 최대한 `assertj`의 `assertThat`을 사용해 검증 통일.

### 2.3 아키텍처 테스트 (ArchUnit)

- 목적: 헥사고날 계층 규칙 준수 보장.
- 근거 테스트: `src/test/java/org/mandarin/booking/arch/HexagonalArchitectureTest.java`
- 핵심 규칙:
  - adapter 레이어는 어떤 레이어에도 접근 허용되지 않음(외부에서 접근 금지).
  - application 레이어는 adapter에서만 접근 가능.
  - domain 레이어는 adapter, application에서만 접근 가능.

---

## 3. 테스트-환경 설정

- Gradle test 태스크가 `spring.profiles.active=test`로 실행됨: `build.gradle` 65~70행 참고.
- H2 설정: `application-test.yml`
  - URL: `jdbc:h2:mem:test;MODE=MySQL;`
  - Hibernate Dialect: `H2Dialect` + 테스트 중 MySQL 호환 모드
  - JPA: `ddl-auto: create`, `format_sql/show_sql: true`
- JWT 설정: `application-test.yml`의 `jwt.token.secret/access/refresh`
- 정적 분석: 필요시 `./gradlew spotbugsMain spotbugsTest` 병행 실행 가능.

---

## 4. 작성 규칙

### 4.1 단위 테스트 작성 체크리스트

- [ ] 단일 책임/작은 단위만 검증한다.
- [ ] 외부 의존은 Mockito로 모킹한다.
- [ ] 스프링 컨텍스트를 기동하지 않는다.
- [ ] 성공/실패 경로를 모두 검증한다(예외 포함).
- [ ] 경계값, 널/빈값 케이스 포함.

### 4.2 통합 테스트 작성 체크리스트

- [ ] Spring 컨텍스트 기동 및 필요한 빈 주입 확인.
- [ ] 보안 필터/JWT 인증 흐름을 실제로 검증한다.
- [ ] 컨트롤러 → 앱 서비스 → 영속성(JPA/H2) 경로를 통해 상태 변화/응답을 확인한다.
- [ ] API 명세 문서(docs/specs/api/*.md)의 체크리스트를 테스트 케이스로 반영한다.
- [ ] 테스트 독립성을 보장하고, 데이터 격리를 유지한다.

### 4.3 공통 규칙

- 테스트명/메서드명은 자연어에 가깝게 시나리오를 드러낸다(한글/영문 허용).
- 반복 셋업은 유틸/추상 베이스 클래스로 추출(예: `IntegrationTestUtils`).
- 민감정보(비밀번호, 토큰 원문 등)는 로그에 남기지 않는다.

---

## 5. API 스펙 연동

- 각 API 문서의 "테스트" 체크박스를 충족하는 테스트를 작성/유지한다.
  - 로그인: `docs/specs/api/login.md`
  - 회원가입: `docs/specs/api/member_register.md`
  - 토큰 재발급: `docs/specs/api/reissue.md`
  - 공연 등록: `docs/specs/api/show_register.md`
- 체크박스는 수용 기준(acceptance criteria)로 간주하며, 누락 시 테스트 보완 또는 문서 동기화가 필요하다.

---

## 6. 실행 방법과 성능

- 표준 실행: `./gradlew test`
- 통합 테스트의 컨텍스트 초기화 비용이 크므로, 로컬 개발 중에는 대상 패키지/클래스만 선별 실행을 권장.
- 빠른 피드백: 도메인/유틸 단위 테스트 우선 실행 → 이후 통합 테스트.

---

## 7. CI 연동

- GitHub Actions 등 CI 정의는 현재 저장소에서 확인 불가.
- 향후 CI 도입 시, 최소 요구: `./gradlew clean test` + ArchUnit + SpotBugs.

---

## 8. 디렉터리/네이밍 가이드

- 테스트 루트: `src/test/java`
- 관례:
  - 단위 테스트: 대상 패키지에 맞춰 배치, 클래스명 `*Test`
  - 통합 테스트: 시나리오 중심 폴더 구조 사용 가능
    - 예시)
      - POST `/api/auth/login`: `src/test/java/org/mandarin/booking/webapi/auth/login/POST_specs.java`
      - GET `/api/show`: `src/test/java/org/mandarin/booking/webapi/show/GET_specs.java`
  - 아키텍처 테스트: `arch/*`

---

## 9. 부록: 참고 클래스

- ArchUnit: `src/test/java/org/mandarin/booking/arch/HexagonalArchitectureTest.java`
- 보안 테스트: `src/test/java/org/mandarin/booking/adapter/security/*.java`
- 웹 API 스펙: `src/test/java/org/mandarin/booking/webapi/**`
- 통합 유틸: `src/test/java/org/mandarin/booking/IntegrationTest*.java`, `JwtTestUtils.java`
