# 영화 예매 시스템 도메인 설계

---

## 개요
이 문서는 영화 예매 시스템의 도메인 모델을 설계하기 위한 것입니다.

## 도메인 모델

### 영화(Movie)
_Aggregate Root_
- 상영될 콘텐츠 자체.

#### 속성
- 제목(title)
- 감독(director)
- 상영시간(runtimeMinutes, 분)
- 장르(genre: ACTION/DRAMA/COMEDY/THRILLER/ROMANCE/SF/FANTASY/HORROR/ANIMATION/DOCUMENTARY/ETC)
- 관람등급(rating: ALL/AGE12/AGE15/AGE18)
- 개봉일(releaseDate, yyyy-MM-dd)
- 줄거리(synopsis)
- 포스터 URL(posterUrl)
- 출연 배우 목록(casts: Set<String\>)

#### 행위
- `create(command: MovieCreateCommand)`: 커맨드로부터 영화를 생성합니다.

#### 관련 타입
- `MovieCreateCommand`: 영화 생성 커맨드
  - title, genre, runtimeMinutes, director, synopsis, posterUrl, releaseDate, rating, casts(Set<String\>)
- `MovieRegisterRequest` / `MovieRegisterResponse`: 웹 API 요청/응답 DTO

---

### 사용자(Member)
_Aggregate Root_
- 서비스를 사용하는 사람(회원).

#### 속성
- 닉네임(nickName)
- 아이디(userId) — unique
- 비밀번호 해시(passwordHash)
- 이메일(email)
- 권한(authorities: List<MemberAuthority\>) — 기본값 USER

#### 행위
- `create(command: MemberCreateCommand, encoder: SecurePasswordEncoder)`: 암호화된 비밀번호로 회원을 생성합니다.
- `matchesPassword(rawPassword, encoder)`: 주어진 평문 비밀번호가 저장된 해시와 일치하는지 확인합니다.

#### 관련 타입
- `MemberCreateCommand` (inner record of Member): nickName, userId, password(평문), email
- `MemberRegisterRequest` / `MemberRegisterResponse`: 웹 API 요청/응답 DTO
- `MemberAuthority`: USER/DISTRIBUTOR/ADMIN 권한 정의, 컨버터를 사용해 문자열 영속화, 추가적인 테이블 생성 방지

---

### 영화관(Cinema)
_Aggregate Root_
- 영화 상영 시설.

---

### 상영관(ScreeningRoom)
_Entity_
- 영화관 내에서 실제로 영화가 상영되는 개별 공간.

---

### 상영정보(ScreeningSchedule)
_Entity_
- 특정 영화가 특정 상영관에서 특정 날짜와 시간에 상영되는 스케줄.

---
### 좌석(Seat)
_Entity_
- 상영관 내의 개별 의자.

---

### 예매(Reservation)
_Aggregate Root_
- 사용자가 특정 상영정보의 특정 좌석의 구매를 확정한 기록.


---
