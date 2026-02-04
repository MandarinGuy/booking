## 공연 상세 조회

### 요청
- 메서드: `GET`
- 경로: `/api/show/{showId}`
- 경로 변수
  - `showId` (number, 필수, 양수)

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `showId` (number)
  - `title` (string)
  - `type` (string)
  - `rating` (string)
  - `synopsis` (string)
  - `posterUrl` (string)
  - `performanceStartDate` (string, `yyyy-MM-dd`)
  - `performanceEndDate` (string, `yyyy-MM-dd`)
  - `hallId` (number)
  - `hallName` (string)
  - `schedules` (array)
    - `scheduleId` (number)
    - `startAt` (string, `yyyy-MM-ddTHH:mm:ss`)
    - `endAt` (string, `yyyy-MM-ddTHH:mm:ss`)
    - `runtimeMinutes` (number)
  - `grades` (array)
    - `gradeId` (number)
    - `name` (string)
    - `basePrice` (number)
    - `quantity` (number)

```json
{
  "status": "SUCCESS",
  "data": {
    "showId": 1,
    "title": "string",
    "type": "MUSICAL",
    "rating": "ALL",
    "synopsis": "string",
    "posterUrl": "string",
    "performanceStartDate": "2025-10-05",
    "performanceEndDate": "2025-11-05",
    "hallId": 3,
    "hallName": "string",
    "schedules": [
      {
        "scheduleId": 10,
        "startAt": "2025-10-10T19:00:00",
        "endAt": "2025-10-10T21:30:00",
        "runtimeMinutes": 150
      }
    ],
    "grades": [
      {
        "gradeId": 1,
        "name": "VIP",
        "basePrice": 100000,
        "quantity": 100
      }
    ]
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - showId 양수 조건 위반
- `404 NOT_FOUND`
  - 존재하지 않는 showId

### 테스트
- 존재하는 showId를 요청하면 `SUCCESS`가 반환된다
- 존재하지 않는 showId 요청 시 `NOT_FOUND`를 반환한다
- 양의 정수가 아닌 showId 요청 시 `BAD_REQUEST`를 반환한다
- 존재하는 공연장 ID가 조회된다
- 공연 일정은 마감 이전의 일정만 조회된다
- 공연 일정의 런타임은 시작 시간과 종료 시간 차이와 일치한다
- schedules는 endAt ASC 순으로 정렬되어 반환된다
- 영속화된 정보가 조회된다
- synopsis가 없는 경우 빈 문자열로 반환된다
- grade에 비어있는 요소는 없다
- grades는 basePrice ASC, quantity DESC 순으로 정렬된다
