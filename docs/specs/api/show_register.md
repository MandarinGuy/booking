### 요청

- 메서드: `POST`
- 경로: `/api/show`
- 헤더

    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```

- 본문

    ```json
    {
        "hallId": 1,
        "title": "인셉션",
        "type": "MUSICAL",
        "rating": "AGE12",
        "synopsis": "타인의 꿈속에 진입해 아이디어를 주입하는 특수 임무를 수행하는 이야기.",
        "posterUrl": "https://example.com/posters/inception.jpg",
        "performanceStartDate": "2025-10-01",
        "performanceEndDate": "2025-10-31"
    }
    ```


- curl 명령 예시

    ```bash
      curl -i -X POST 'http://localhost:8080/api/show' \
      -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsInJvbGVzIjoiUk9MRV9BRE1JTiIsInVzZXJJZCI6InRlc3QxMjM0Iiwibmlja05hbWUiOiJ0ZXN0IiwiaWF0IjoxNzU3MzExNDc5LCJleHAiOjE3NTczMTIwNzl9.xhEkuZEF0gZlvyX_F2kiAMEMGw_C2ZtGL8PmzLxhZQW32A9hmr6M0nauYEejXOFrZAb3nMdU3jFLxuhDWDbE2g' \
      -H 'Content-Type: application/json' \
      -d '{
            "hallId": 1,
            "title": "인셉션",
            "type": "MUSICAL",
            "rating": "AGE12",
            "synopsis": "타인의 꿈속에 진입해 아이디어를 주입하는 특수 임무를 수행하는 이야기.",
            "posterUrl": "https://example.com/posters/inception.jpg",
            "performanceStartDate": "2025-10-01",
            "performanceEndDate": "2025-10-31"
          }'
    ```

### 응답

- 상태코드: `200 OK`
- 본문

    ```json
    {
        "status": "SUCCESS",
        "data": {
            "showId": 1
        },
        "timestamp": "2025-09-10T12:34:56.789Z"
    }
    ```

### 테스트

- [x] 올바른 요청을 보내면 status가 SUCCESS이다
- [x] Authorization 헤더에 유효한 accessToken이 없으면 status가 UNAUTHORIZED이다
- [x] title, type, rating, synopsis, posterUrl, performanceStartDate, performanceEndDate가 비어있으면 BAD_REQUEST이다
- [x] 허용되지 않은 type/rating이면 BAD_REQUEST이다
- [x] 올바른 요청을 보내면 응답 본문에 showId가 존재한다
- [x] 공연 시작일은 공연 종료일 이후면 INTERNAL_SERVER_ERROR이다
- [x] 중복된 제목의 공연을 등록하면 INTERNAL_SERVER_ERROR가 발생한다
- [x] 존재하지 않는 hallId를 보내면 NOT_FOUND 상태코드를 반환한다
- [x] 비ADMIN 토큰으로 요청하면 FORBIDDEN 상태코드를 반환한다
