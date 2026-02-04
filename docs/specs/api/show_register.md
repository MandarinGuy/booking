## 공연 등록

### 요청
- 메서드: `POST`
- 경로: `/api/show`
- 헤더
  - `Content-Type: application/json`
  - `Authorization: Bearer <accessToken>`
- 본문
  - `hallId` (number, 필수)
  - `title` (string, 필수, 공백 불가)
  - `type` (string, 필수, enum: `MUSICAL|PLAY|CONCERT|OPERA|DANCE|CLASSICAL|ETC`)
  - `rating` (string, 필수, enum: `ALL|AGE12|AGE15|AGE18`)
  - `synopsis` (string, 필수, 공백 불가)
  - `posterUrl` (string, 필수, 공백 불가)
  - `performanceStartDate` (string, `yyyy-MM-dd`, 필수, 오늘/미래)
  - `performanceEndDate` (string, `yyyy-MM-dd`, 필수)
  - `currency` (string, 필수, enum: `KRW`)
  - `ticketGrades` (array, 필수, 최소 1)
    - `name` (string, 필수, 공백 불가, 중복 불가)
    - `basePrice` (number, 필수, 양수)
    - `quantity` (number, 필수, 양수)

```json
{
  "hallId": 1,
  "title": "인셉션",
  "type": "MUSICAL",
  "rating": "AGE12",
  "synopsis": "string",
  "posterUrl": "string",
  "performanceStartDate": "2025-10-01",
  "performanceEndDate": "2025-10-31",
  "currency": "KRW",
  "ticketGrades": [
    { "name": "VIP", "basePrice": 180000, "quantity": 100 }
  ]
}
```

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `showId` (number)

```json
{
  "status": "SUCCESS",
  "data": {
    "showId": 1
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - 필수 필드 누락/공백
  - enum 값 오류
  - ticketGrades 비어있음/중복 name/양수 제약 위반
  - performanceStartDate 형식/미래 조건 위반
- `401 UNAUTHORIZED`
  - 토큰 무효
- `403 FORBIDDEN`
  - 권한 없음(ADMIN 이외)
- `404 NOT_FOUND`
  - hallId 없음
- `500 INTERNAL_SERVER_ERROR`
  - 공연 시작일 > 종료일
  - 제목 중복 등 도메인 예외

메시지(테스트 기준)
- `공연 시작 날짜는 종료 날짜 이후에 있을 수 없습니다.`
- `이미 존재하는 공연 이름입니다:`

### 테스트
- 올바른 요청을 보내면 `SUCCESS`다
- Authorization 헤더에 유효한 accessToken이 없으면 `UNAUTHORIZED`다
- title/type/rating/synopsis/posterUrl/performanceStartDate/performanceEndDate가 비어있으면 `BAD_REQUEST`다
- 허용되지 않은 type이면 `BAD_REQUEST`다
- 허용되지 않은 rating이면 `BAD_REQUEST`다
- performanceStartDate가 과거면 `BAD_REQUEST`다
- hallId가 null이면 `BAD_REQUEST`다
- 올바른 요청을 보내면 응답 본문에 showId가 존재한다
- 요청한 ticketGrades가 Show에 영속된다
- 공연 시작일이 공연 종료일 이후면 `INTERNAL_SERVER_ERROR`다
- 중복된 제목의 공연을 등록하면 `INTERNAL_SERVER_ERROR`가 발생한다
- 존재하지 않는 hallId를 보내면 `NOT_FOUND`를 반환한다
- 비ADMIN 토큰으로 요청하면 `FORBIDDEN`을 반환한다
- ticketGrades가 비어있으면 `BAD_REQUEST`다
- ticketGrade name이 중복이면 `BAD_REQUEST`다
- ticketGrade basePrice가 양수가 아니면 `BAD_REQUEST`다
- quantity가 양수가 아니면 `BAD_REQUEST`다
- currency가 비어있거나 잘못된 값이면 `BAD_REQUEST`다
