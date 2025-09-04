# 애플리케이션 아키텍처 규칙(헥사고날 아키텍처)

본 문서는 booking 프로젝트가 채택한 헥사고날 아키텍처(Hexagonal Architecture, Ports & Adapters)의 규칙을 명확히 하기 위한 가이드입니다. 이 문서는 아키텍처 테스트와 코드 리뷰의 근거가 되며, 새로운 기능 추가 시 반드시 준수해야 합니다.

## 1. 계층과 책임

프로젝트는 크게 세 계층으로 구성됩니다.

- domain: 도메인 모델과 비즈니스 규칙의 순수 영역
  - 위치: `src/main/java/org/mandarin/booking/domain`
  - 포함: 엔티티(를 표현하기 위한 매핑정보), 값 객체, 도메인 서비스(필요시), 도메인 예외, 도메인 전용 인터페이스(예: `SecurePasswordEncoder`), 유스케이스에 전달되는 순수 모델(`*Request`, `*Response`, `*Command` 등)
  - 금지: 프레임워크/외부 라이브러리 의존(JPA/Spring/Web 등), I/O 접근, 인프라 세부 사항

- app: 애플리케이션 서비스(유스케이스)와 포트 인터페이스
  - 위치: `src/main/java/org/mandarin/booking/app`
  - 포함: 유스케이스 서비스(`*Service`), 입력/출력 포트(`app/port`), 트랜잭션 경계, 조합/오케스트레이션 로직, 검증기(애플리케이션 수준), 크로스커팅(AOP, 로깅 등)
  - 의존: domain에는 의존 가능, adapter에는 의존 금지

- adapter: 외부 세계와의 연결(웹, 보안, 영속성 등)
  - 위치: `src/main/java/org/mandarin/booking/adapter`
  - 하위 영역:
    - `webapi`: REST 컨트롤러, DTO 매핑, 예외/응답 공통 처리
    - `security`: 인증/인가 컴포넌트(JwtFilter, AuthenticationProvider 등)
    - `persist`: 영속성 구현은 현재 `app/persist` 패키지에 배치되어 있으며, 어댑터 구현으로 취급합니다. JPA 리포지토리와 실제 데이터 접근 로직이 위치합니다.
  - 의존: app의 포트에만 의존해야 하며 domain, app 구현 내부로 직접 의존하지 않습니다(서비스 구현 클래스 참조 금지).

텍스트 다이어그램:

[Controllers/Security/JPA] → adapter → app(ports, services) → domain\(pure model)

## 2. 의존성 규칙

- domain -> another domain
- app -> domain (OK), adapter (금지)
- adapter -> app 포트(OK), app 서비스/구현(금지), domain(읽기 전용 OK. 단, 비즈니스 수행은 app 경유)
- DTO/엔티티 경계:
  - webapi의 요청/응답 DTO는 한시적으로 domain에 존재. 추후 변경 가능성 있음.
  - 영속성 엔티티는 domain에만 존재. domain 엔티티와 동일 클래스로 사용.

## 3. 포트와 어댑터

- 입력 포트(inbound port): 유스케이스 인터페이스. 위치: `app/port` 컨트롤러는 입력 포트를 통해서만 유스케이스 호출.
- 출력 포트(outbound port): 외부 시스템/리포지토리에 대한 인터페이스. 위치: `app/persist` 또는 `app/port` 하위에 정의 가능.
- 어댑터(adapters): 포트 인터페이스의 구현체. 위치: adapter 하위. 현재 JPA 기반 구현은 `app/persist/*Repository`를 통해 동작하며, 해당 패키지는 어댑터 계층으로 간주합니다.

권장 네이밍:
- 입력 포트: UseCase 동사형 + er (예: Registerer, UseCase)
- 출력 포트: 리소스 + 동작 + Repository/Gateway (예: ShowCommandRepository)
- 그 외에는 해당 인터페이스가 담당한 기능의 추상적 개념을 나타내는 네이밍

## 4. 트랜잭션/검증/예외/로깅 규칙

- 트랜잭션 경계: app 계층의 유스케이스 서비스 메서드 수준에서 관리(@Transactional). 컨트롤러/어댑터에서는 트랜잭션을 시작하지 않습니다.
- 검증:
  - 형태/구문 검증: adapter(webapi)에서 기본적인 바인딩/형식 검증 허용.
  - 비즈니스/정책 검증: app 또는 domain에서 수행. `Validator` 등의 컴포넌트는 app에 위치.
- 예외:
  - 도메인 오류는 domain 예외(`DomainException`의 자식 클래스)로 표현.
  - 어댑터/기술 오류는 해당 계층에서 포착하고 app/domain 의미의 예외로 변환 또는 적절히 매핑.
  - webapi는 예외를 `GlobalExceptionHandler`로 공통 변환하여 `ErrorResponse`로 응답.
- 로깅: 크로스커팅은 app 계층의 AOP(`LoggingAspect`)에서 처리. 민감 정보(비밀번호, 토큰 등)는 로그 금지.

## 5. 패키지 구조 규칙

- domain: `org.mandarin.booking.domain.{boundedContext}`
    - 예: `domain.member`, `domain.show`
- app: `org.mandarin.booking.app`
  - 하위: `port`, `persist`(출력 포트/구현), 서비스 클래스
- adapter: `org.mandarin.booking.adapter.{webapi|security|...}`
- 순환 의존 금지: 위 규칙 위반 시 컴파일/테스트 단계에서 아키텍처 테스트 실패로 간주.

## 6. 컨트롤러와 DTO 변환 규칙

- 컨트롤러는 입력 포트만 의존한다.
- 요청 DTO -> domain/app 요청 모델로 변환 후 유스케이스 호출.
- 유스케이스 반환값 -> web DTO로 매핑하여 응답한다.
- 컨트롤러에서 비즈니스 로직/트랜잭션 처리 금지.

예시(공연 등록):

- `adapter/webapi/ShowController` -> `app/port/ShowRegisterer` 호출
- `domain.show.ShowRegisterRequest`/`ShowCreateCommand` 사용하여 유스케이스 실행
- 결과를 `domain.show.ShowRegisterResponse` 받아 web 응답으로 래핑(`ApiResponse`)

## 7. 영속성 규칙(JPA)

- JPA 엔티티는 domain에, Repository는 app(persist)에만 존재.
- app 계층은 JPA 구체 타입에 의존하지 않고, 출력 포트 인터페이스를 통해서만 데이터 접근.
- 매핑 책임은 어댑터에 위치: JPA 엔티티 <-> 도메인 엔티티/모델 변환.

## 8. 보안 규칙

- 인증/인가 컴포넌트는 adapter/security에 위치: `JwtFilter`, `CustomAuthenticationProvider`, `SecurityConfig` 등.
- 보안 컨텍스트와 토큰 파싱은 어댑터에서 처리하고, app 유스케이스에는 인증된 식별자/역할만 전달.

## 9. 테스트 규칙

- `src/test/java/org/mandarin/booking/arch/HexagonalArchitectureTest.java` 는 아키텍처 규칙을 자동 검증합니다.
- 규칙 위반 예:
  - adapter가 app 서비스 구현 클래스에 직접 의존
  - app이 adapter 패키지에 의존
  - domain이 프레임워크에 의존
- 새로운 모듈/클래스 추가 시 해당 테스트가 통과하는지 반드시 확인합니다.

## 10. 확장 가이드(새 유스케이스/어댑터 추가)

새 유스케이스(예: 공연 수정) 추가 절차:

1) domain에 필요한 모델/명세 정의(예: `ShowUpdateCommand`).
2) app/port에 입력 포트 정의(예: `ShowUpdater`).
3) app에 서비스 구현(`ShowService` 내 메서드 또는 별도 서비스) 및 트랜잭션/검증 구현.
4) 필요 시 출력 포트 정의 및 어댑터 구현(persist/JPA 등).
5) adapter/webapi에 컨트롤러 엔드포인트 추가 및 DTO 매핑.
6) 아키텍처/통합 테스트 통과 확인.

새 어댑터(예: 외부 결제 API) 추가 절차:
1) app에 출력 포트 인터페이스 추가(예: `PaymentGateway`).
2) adapter 하위에 구현(예: `adapter/external/PaymentGatewayHttpClient`).
3) 구성(Security/Config)과 예외 매핑 추가.

## 11. 공통 규칙 요약(Do/Don’t)

Do
- 유스케이스 입출력은 app 포트를 통해서만 노출/호출한다.
- 도메인 모델은 순수하게 유지한다(프레임워크 의존 금지).
- 어댑터는 포트 인터페이스를 구현한다.
- 트랜잭션과 로깅은 app에서 관리한다.

Don’t
- 컨트롤러에서 비즈니스 로직 수행 금지.
- app에서 adapter 패키지/구현에 의존 금지.
- domain에서 JPA/Spring 등에 의존 금지.

## 12. 용어

- 도메인 모델: 비즈니스 개념을 표현하는 순수 객체(`Member`, `Show` 등)
- 유스케이스: 시스템이 제공하는 기능 단위(등록, 로그인 등)
- 포트: 유스케이스(입력) 또는 외부 의존(출력)을 추상화한 인터페이스
- 어댑터: 포트를 구현하여 외부 세계와 연결하는 기술 계층

본 문서는 변경 시 PR에 포함하고, 아키텍처 테스트가 통과하는지 확인해야 합니다.
