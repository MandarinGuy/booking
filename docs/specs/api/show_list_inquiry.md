# 공연 목록 조회

---

## 개요

- 공연(Show)의 목록을 조회한다.
- 검색 조건(type, rating, q, from~to)을 통해 필터링할 수 있으며, 결과는 페이지네이션된다.
- 반환 데이터는 공연 요약 정보로, 상세(회차, 가격 등)는 포함하지 않는다.

---

## 요청

- 메서드: `GET`
- 경로: `/api/show`
- 헤더

    ```
    Content-Type: application/json
    ```

- 쿼리 파라미터
    - page (선택, 기본=0, 정수 >= 0): 페이지 번호
    - size (선택, 기본=10, 1~100): 페이지 크기
    - type (선택): 공연 유형 (MUSICAL, PLAY, CONCERT, OPERA, DANCE, CLASSICAL, ETC)
    - rating (선택): 관람 등급 (ALL, AGE12, AGE15, AGE18)
    - q (선택): 공연 제목 검색 키워드
    - from (선택, yyyy-MM-dd): 조회 시작일
    - to (선택, yyyy-MM-dd): 조회 종료일  
      -> 공연 기간(performanceStartDate~performanceEndDate)이 [from, to] 구간과 겹치는 공연만 반환한다.
    - 기간 필터링: 공연 기간 [performanceStartDate, performanceEndDate] 와 조회 기간 [from, to] 가 **겹치면** 포함한다.
    - 경계 포함(폐구간): performanceStartDate <= to AND performanceEndDate >= from
    - from만 지정 시: performanceEndDate >= from
    - to만 지정 시: performanceStartDate <= to
    - from > to 인 경우: 400 BAD_REQUEST

    - 정렬:
        1) performanceStartDate ASC
        2) title ASC
        3) showId ASC (타이 브레이커 최종)

    - 페이지네이션:
        - page는 0-기반 인덱스다.
      - hasNext = 존재성 여부만 확인

- curl 명령 예시

    ```bash
    curl -i -X GET 'http://localhost:8080/api/show?page=0&size=5&type=MUSICAL&from=2025-10-01&to=2025-10-31&q=라라' \
    -H 'Content-Type: application/json'
    ```

---

## 응답

- 상태코드: `200 OK`
    - 본문 예시

        ```json
        {
          "status": "SUCCESS",
          "data": {
          "contents": [
            {
              "showId": 1,
              "title": "라라랜드",
              "type": "MUSICAL",
              "rating": "ALL",
              "posterUrl": "https://example.com/posters/lalaland.jpg",
              "venueName": "샤롯데씨어터",
              "performanceStartDate": "2025-10-05",
              "performanceEndDate": "2025-11-05"
            },
            {
              "showId": 2,
              "title": "라라랜드 2",
              "type": "MUSICAL",
              "rating": "AGE12",
              "posterUrl": "https://example.com/posters/lalaland2.jpg",
              "venueName": "샤롯데씨어터",
              "performanceStartDate": "2025-10-10",
              "performanceEndDate": "2025-11-10"
            }
          ],
          "page": 0,
          "size": 5,
          "hasNext": false
          },
          "timestamp": "2025-09-17T12:34:56.789Z"
          }
        
      ```

---

## 테스트

- [x] 잘못된 토큰/만료 토큰을 전달해도 정상 응답을 반환한다
- [x] 기본 요청 시 첫번째 페이지의 10건이 반환된다
- [x] 공연이 존재하지 않을 경우 빈 contents, hasNext=false를 반환한다
- [x] 실제로 저장된 공연 정보가 조회된다
- [x] 초과 페이지 요청 시 빈 contents와 hasNext=false를 반환한다
- [x] size가 100보다 큰 요청 시 BAD_REQUEST를 반환한다
- [x] page가 0보다 작은 요청 시 BAD_REQUEST를 반환한다
- [x] size가 1보다 작은 요청 시 BAD_REQUEST를 반환한다
- [x] 부적절한 type으로 요청하는 경우 BAD_REQUEST를 반환한다
- [x] 부적절한 rating으로 요청하는 경우 BAD_REQUEST를 반환한다
- [x] 지정된 type이 존재한다면 해당 type 공연만 조회된다
- [x] 지정된 rating이 존재한다면 해당 rating 공연만 조회된다
- [x] q값이 비어있지 않다면 제목에 q가 포함된 공연만 조회된다
- [x] 여러 건이 존재할 경우 performanceStartDate DESC, title ASC 순으로 정렬된다
- [x] from에서 to까지 기간과 겹치는 공연만 조회된다
- [x] from만 지정 시 해당 일자 이후 공연만 조회된다
- [x] to만 지정 시 해당 일자 이전 공연만 조회된다
- [x] 기간이 서로 맞물리지 않는 경우 빈 contents를 반환한다
- [x] from 또는 to 형식이 잘못된 경우 BAD_REQUEST를 반환한다
- [x] from이 to이후인 경우 BAD_REQUEST를 반환한다
- [x] q가 공백인 경우 BAD_REQUEST를 반환한다
- [x] 마지막 페이지에서 hasNext가 거짓으로 반환된다
- [x] 마지막 페이지가 아닌 경우 hasNext가 참으로 반환된다
- [ ] venueName은 존재하는 공연장 이름이 조회된다
