# 기능 명세서: 공연 상세 조회

## 개요

공연의 상세 정보를 조회하는 기능이다.  
공연 기본 정보(제목, 유형, 등급, 기간, 시놉시스, 포스터, 공연장)와 함께 등록된 회차 정보를 확인할 수 있다.  
공연 목록 조회 후 특정 공연을 선택했을 때 상세 페이지로 진입하기 위한 핵심 엔드포인트이다.

## Endpoint

- Method: `GET`
- URL: `/api/show/{showId}`

## 요청 파라미터

- Path Variable
    - `showId` (Long, required): 조회할 공연의 고유 식별자

## 요청 예시

GET /api/show/1

## 응답 본문

```json

{
  "status": "SUCCESS",
  "data": {
    "showId": 1,
    "title": "라라랜드",
    "type": "MUSICAL",
    "rating": "ALL",
    "synopsis": "꿈을 좇는 두 청춘의 사랑과 음악 이야기",
    "posterUrl": "https://cdn.example.com/posters/la_la_land.jpg",
    "performanceStartDate": "2025-10-05",
    "performanceEndDate": "2025-11-05",
    "hall": {
      "hallId": 3,
      "hallName": "샤롯데씨어터"
    },
    "schedules": [
      {
        "scheduleId": 10,
        "startAt": "2025-10-10T19:00:00",
        "endAt": "2025-10-10T21:30:00",
        "runtimeMinutes": 150
      },
      {
        "scheduleId": 11,
        "startAt": "2025-10-11T14:00:00",
        "endAt": "2025-10-11T16:30:00",
        "runtimeMinutes": 150
      }
    ]
  },
  "timestamp": "2025-09-25T00:00:00Z"
}

```

응답 코드

- 200 OK: 정상적으로 조회된 경우
- 400 BAD_REQUEST: 잘못된 showId 값이 전달된 경우
- 404 NOT_FOUND: 존재하지 않는 공연을 조회한 경우

## Policy

- 공연 기간(performanceStartDate ≤ performanceEndDate)은 등록 시점에 검증되므로 조회 시 항상 유효한 값을 반환한다.
- schedules는 공연에 연결된 회차가 없으면 빈 배열을 반환한다.

테스트 시나리오

- [x] 존재하는 showId를 요청하면 200과 함께 공연 상세 정보가 반환된다
- [x] 존재하지 않는 showId 요청 시 NOT_FOUND를 반환한다
- [x] 양의 정수가 아닌 showId 요청 시 BAD_REQUEST을 반환한다
- [x] 공연에 회차가 없는 경우 schedules는 빈 배열이다
- [x] 존재하는 공연장 ID가 조회된다
- [ ] 공연 일정은 마감 이전의 일정만 조회된다
- [ ] 공연 일정의 런타임은 시작 시간과 종료 시간의 차이와 일치한다
- [ ] hall 정보는 hallId와 hallName을 모두 포함한다
- [ ] schedules는 startAt ASC, 이름 순으로 정렬되어 반환된다
