## 공연장(홀) 등록

### 요청
- 메서드: `POST`
- 경로: `/api/hall`
- 헤더
  - `Content-Type: application/json`
  - `Authorization: Bearer <accessToken>`
- 본문
  - `hallName` (string, 필수, 공백 불가)
  - `sections` (array, 필수, 최소 1)
    - `sectionName` (string, 필수, 공백 불가)
    - `seats` (array, 필수, 최소 1)
      - `rowNumber` (string, 필수, 공백 불가)
      - `seatNumber` (string, 필수, 공백 불가)
  - 제약
    - `sections.sectionName` 중복 불가
    - 동일 section 내 `rowNumber` + `seatNumber` 중복 불가

```json
{
  "hallName": "Seoul Art Hall",
  "sections": [
    {
      "sectionName": "A",
      "seats": [
        { "rowNumber": "1", "seatNumber": "1" }
      ]
    }
  ]
}
```

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `hallId` (number)

```json
{
  "status": "SUCCESS",
  "data": {
    "hallId": 1
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - 필수 필드 누락/공백
  - sections/seat 최소 개수 위반
  - 중복 sectionName 또는 중복 seat
- `401 UNAUTHORIZED`
  - 토큰 무효
- `403 FORBIDDEN`
  - 권한 없음(ADMIN 이외)
- `500 INTERNAL_SERVER_ERROR`
  - hallName 중복 등 도메인 예외

메시지(테스트 기준)
- `Duplicate section names are not allowed`
- `At least one section is required`
- `At least one seat is required`

### 테스트
- ADMIN 권한의 토큰과 유효 본문으로 요청하면 `SUCCESS`와 hallId를 반환한다
- 비ADMIN 토큰으로 요청하면 `FORBIDDEN`을 반환한다
- 토큰이 무효하면 `UNAUTHORIZED`를 반환한다
- hallName이 비어있으면 `BAD_REQUEST`를 반환한다
- sections가 빈 배열이면 `BAD_REQUEST`를 반환한다
- sectionName이 비어있으면 `BAD_REQUEST`를 반환한다
- seats가 빈 배열이면 `BAD_REQUEST`를 반환한다
- rowNumber 또는 seatNumber가 빈 문자인 경우 `BAD_REQUEST`를 반환한다
- 동일 섹션 내 rowNumber+seatNumber 조합이 중복이면 `BAD_REQUEST`를 반환한다
- 섹션 이름이 중복되면 `BAD_REQUEST`를 반환한다
- hall을 등록하면 등록한 사용자 정보도 저장된다
- hallName이 중복되면 `INTERNAL_SERVER_ERROR`를 반환한다
- hall 하위 정보가 잘못된 경우 hall도 저장되지 않는다
