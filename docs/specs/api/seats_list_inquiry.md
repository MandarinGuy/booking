# 회차 좌석 목록 조회

## 개요

- 특정 회차(showSchedule)의 인벤토리에 등록된 좌석을 조회한다.
- 좌석별 등급과 상태를 함께 반환한다.
- 상태 값: `AVAILABLE`, `HELD`, `SOLD`

## 요청

- 메서드: `GET`
- 경로: `/api/show/schedule/{scheduleId}/seats`
- 헤더

  ```
  Content-Type: application/json
  ```

- 경로 변수
    - `scheduleId` (필수, Long): 조회할 회차 ID

- curl 예시

  ```bash
  curl -i -X GET 'http://localhost:8080/api/show/schedule/1/seats' \
    -H 'Content-Type: application/json'
  ```

## 응답

- 상태코드: `200 OK`
- 본문 예시

  ```json
  {
    "status": "SUCCESS",
    "data": {
      "contents": [
        { "seatId": 1001, "gradeId": 1, "status": "AVAILABLE" },
        { "seatId": 1002, "gradeId": 1, "status": "HELD" },
        { "seatId": 1003, "gradeId": 2, "status": "SOLD" }
      ]
    },
    "timestamp": "2025-09-25T00:00:00Z"
  }
  ```

## 테스트

- [ ] 유효한 scheduleId로 요청 시 200 OK와 contents 배열을 반환한다
- [ ] 각 요소는 seatId, gradeId, status 필드를 포함한다
- [ ] status 값은 AVAILABLE, HELD, SOLD 중 하나다
- [ ] 상태 소스가 없는 환경에서는 모든 좌석의 status는 AVAILABLE로 반환된다
- [ ] 등록 시 제외된 좌석은 목록에 포함되지 않는다
- [ ] 등록 시 지정한 gradeAssignments와 응답 seatId와 gradeId 매핑이 일치한다
- [ ] 응답 좌석 개수는 인벤토리의 SeatState 개수와 일치한다
- [ ] seatId 중복이 존재하지 않는다
- [ ] 존재하지 않는 scheduleId 요청 시 NOT_FOUND를 반환한다
- [ ] scheduleId가 0 이하 또는 비정상 형식이면 BAD_REQUEST를 반환한다

