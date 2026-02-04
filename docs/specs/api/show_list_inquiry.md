## 공연 목록 조회

### 요청
- 메서드: `GET`
- 경로: `/api/show`
- 쿼리 파라미터
  - `page` (number, 선택, 기본 0, `>=0`)
  - `size` (number, 선택, 기본 10, `1~100`)
  - `type` (string, 선택, enum: `MUSICAL|PLAY|CONCERT|OPERA|DANCE|CLASSICAL|ETC`)
  - `rating` (string, 선택, enum: `ALL|AGE12|AGE15|AGE18`)
  - `q` (string, 선택, 공백만 입력 불가)
  - `from` (string, 선택, `yyyy-MM-dd`)
  - `to` (string, 선택, `yyyy-MM-dd`)
  - 제약
    - from > to: `BAD_REQUEST`

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `contents` (array)
    - `showId` (number)
    - `title` (string)
    - `type` (string)
    - `rating` (string)
    - `posterUrl` (string)
    - `hallName` (string)
    - `performanceStartDate` (string, `yyyy-MM-dd`)
    - `performanceEndDate` (string, `yyyy-MM-dd`)
  - `page` (number)
  - `size` (number)
  - `hasNext` (boolean)

```json
{
  "status": "SUCCESS",
  "data": {
    "contents": [
      {
        "showId": 1,
        "title": "string",
        "type": "MUSICAL",
        "rating": "ALL",
        "posterUrl": "string",
        "hallName": "string",
        "performanceStartDate": "2025-10-05",
        "performanceEndDate": "2025-11-05"
      }
    ],
    "page": 0,
    "size": 10,
    "hasNext": false
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - page/size 범위 위반
  - type/rating enum 오류
  - q 공백
  - from/to 형식 오류 또는 from > to

### 테스트
- 기본 요청 시 첫번째 페이지의 10건이 반환된다
- 공연이 존재하지 않을 경우 빈 contents, hasNext=false를 반환한다
- 실제로 저장된 공연 정보가 조회된다
- 초과 페이지 요청 시 빈 contents와 hasNext=false를 반환한다
- size가 100보다 큰 요청 시 `BAD_REQUEST`를 반환한다
- page가 0보다 작은 요청 시 `BAD_REQUEST`를 반환한다
- size가 1보다 작은 요청 시 `BAD_REQUEST`를 반환한다
- 부적절한 type으로 요청하는 경우 `BAD_REQUEST`를 반환한다
- 부적절한 rating으로 요청하는 경우 `BAD_REQUEST`를 반환한다
- 지정된 type이 존재한다면 해당 type 공연만 조회된다
- 지정된 rating이 존재한다면 해당 rating 공연만 조회된다
- q값이 비어있지 않다면 제목에 q가 포함된 공연만 조회된다
- 여러 건이 존재할 경우 performanceStartDate DESC, title ASC 순으로 정렬된다
- from에서 to까지 기간과 겹치는 공연만 조회된다
- from만 지정 시 해당 일자 이후 공연만 조회된다
- to만 지정 시 해당 일자 이전 공연만 조회된다
- 기간이 서로 맞물리지 않는 경우 빈 contents를 반환한다
- from 또는 to 형식이 잘못된 경우 `BAD_REQUEST`를 반환한다
- from이 to이후인 경우 `BAD_REQUEST`를 반환한다
- q가 공백인 경우 `BAD_REQUEST`를 반환한다
- 마지막 페이지에서 hasNext가 거짓으로 반환된다
- 마지막 페이지가 아닌 경우 hasNext가 참으로 반환된다
- hallName은 존재하는 공연장 이름이 조회된다
