## 회차 등록

### 요청
- 메서드: `POST`
- 경로: `/api/show/schedule`
- 헤더
  - `Content-Type: application/json`
  - `Authorization: Bearer <accessToken>`
- 본문
  - `showId` (number, 필수)
  - `startAt` (string, `yyyy-MM-ddTHH:mm:ss`, 필수)
  - `endAt` (string, `yyyy-MM-ddTHH:mm:ss`, 필수)
  - `use` (object, 필수)
    - `sectionId` (number, 필수)
    - `excludeSeatIds` (array<number>, 선택, 중복 불가)
    - `gradeAssignments` (array, 필수, 최소 1)
      - `gradeId` (number, 필수, 중복 불가)
      - `seatIds` (array<number>, 필수, 최소 1, 전역 중복 불가)
  - 제약
    - `endAt` > `startAt`

```json
{
  "showId": 1,
  "startAt": "2025-10-10T19:00:00",
  "endAt": "2025-10-10T21:30:00",
  "use": {
    "sectionId": 10,
    "excludeSeatIds": [1003],
    "gradeAssignments": [
      { "gradeId": 1, "seatIds": [1001, 1002] }
    ]
  }
}
```

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `scheduleId` (number)

```json
{
  "status": "SUCCESS",
  "data": {
    "scheduleId": 1
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - 필수 필드 누락
  - endAt <= startAt
  - excludeSeatIds/gradeAssignments 중복
  - seatIds 전역 중복
- `401 UNAUTHORIZED`
  - 토큰 무효
- `403 FORBIDDEN`
  - 권한 없음
- `404 NOT_FOUND`
  - showId/sectionId/gradeId/seatId 없음
- `500 INTERNAL_SERVER_ERROR`
  - 동일 홀/시간 중복 회차

비고
- runtimeMinutes는 요청 필드가 아니며 서버에서 계산된다.

메시지(테스트 기준)
- `The end time must be after the start time`
- `존재하지 않는 공연입니다.`
- `공연 기간 범위를 벗어나는 일정입니다.`
- `해당 회차는 이미 공연 스케줄이 등록되어 있습니다.`
- `excludeSeatIds must not contain duplicates`
- `gradeAssignments gradeIds must not contain duplicates`
- `gradeAssignments seatIds must not contain duplicates across all assignments`
- `해당 섹션 좌석과 총 좌석이 상이합니다.`

### 테스트
- 올바른 접근 토큰과 유효한 요청을 보내면 `SUCCESS`를 반환한다
- DISTRIBUTOR 권한 사용자 요청이면 `SUCCESS`를 반환한다(ADMIN 허용)
- 응답 본문에 scheduleId가 포함된다
- 권한이 없는 사용자 토큰이면 `FORBIDDEN`을 반환한다
- endAt <= startAt이면 `BAD_REQUEST`를 반환한다
- use가 null이면 `BAD_REQUEST`를 반환한다
- sectionId가 null이면 `BAD_REQUEST`를 반환한다
- gradeAssignments가 비어있으면 `BAD_REQUEST`를 반환한다
- seatIds가 비어있으면 `BAD_REQUEST`를 반환한다
- 존재하지 않는 showId면 `NOT_FOUND`를 반환한다
- 공연 기간 범위를 벗어나는 startAt/endAt이면 `BAD_REQUEST`를 반환한다
- 동일 hallId와 시간이 겹치는 회차면 `INTERNAL_SERVER_ERROR`를 반환한다
- showId에 해당하는 hall의 sectionId를 찾을 수 없으면 `NOT_FOUND`를 반환한다
- excludeSeatIds에 해당 section의 id가 아닌 좌석 id가 포함되면 `BAD_REQUEST`를 반환한다
- excludeSeatIds에 중복된 좌석이 있으면 `BAD_REQUEST`를 반환한다
- gradeAssignments gradeId가 show에 없으면 `NOT_FOUND`를 반환한다
- gradeAssignments gradeId가 중복이면 `BAD_REQUEST`를 반환한다
- gradeAssignments seatIds에 hall의 seat id가 없으면 `BAD_REQUEST`를 반환한다
- gradeAssignments seatIds에 중복된 좌석이 존재하면 `BAD_REQUEST`를 반환한다
- 제외 좌석과 등록 좌석 전체가 section의 모든 좌석과 다른 경우 `BAD_REQUEST`를 반환한다
- 일정이 정상 등록되면 inventory에 해당 회차의 좌석이 모두 생성된다
- excludeSeatIds에 중복이 없으면 검증이 통과되고 `SUCCESS`를 반환한다
