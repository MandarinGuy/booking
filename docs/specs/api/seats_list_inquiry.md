## 회차 좌석 목록 조회

### 요청
- 메서드: `GET`
- 경로: `/api/show/schedule/{scheduleId}/seat`
- 경로 변수
  - `scheduleId` (number, 필수, 양수)

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `contents` (array)
    - `seatId` (number)
    - `gradeId` (number)
    - `status` (string, enum: `AVAILABLE|HELD|SOLD`)

```json
{
  "status": "SUCCESS",
  "data": {
    "contents": [
      { "seatId": 1001, "gradeId": 1, "status": "AVAILABLE" }
    ]
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - scheduleId 양수 조건 위반
- `404 NOT_FOUND`
  - 존재하지 않는 scheduleId

### 테스트
- 유효한 scheduleId로 요청 시 `SUCCESS`와 contents 배열을 반환한다
- 각 요소는 seatId, gradeId, status 필드를 포함한다
- scheduleId가 양의 정수가 아닌 경우 `BAD_REQUEST`를 반환한다
- 존재하지 않는 scheduleId 요청 시 `NOT_FOUND`를 반환한다
