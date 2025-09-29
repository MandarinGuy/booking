### 요청

- 메서드: `POST`
- 경로: `/api/show/schedule`
- 헤더

    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```

- 본문 예시

    ```json
    {
      "showId": 1,
      "startAt": "2025-10-10T19:00:00",
      "endAt": "2025-10-10T21:30:00",
      "runtimeMinutes": 150
    }
    ```

- curl 명령 예시

    ```bash
    curl -i -X POST 'http://localhost:8080/api/show/schedule' \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsInJvbGVzIjoiUk9MRV9BRE1JTiIsInVzZXJJZCI6InRlc3QxMjM0Iiwibmlja05hbWUiOiJ0ZXN0IiwiaWF0IjoxNzU3MzExNDc5LCJleHAiOjE3NTczMTIwNzl9.xhEkuZEF0gZlvyX_F2kiAMEMGw_C2ZtGL8PmzLxhZQW32A9hmr6M0nauYEejXOFrZAb3nMdU3jFLxuhDWDbE2g' \
    -d '{
      "showId": 1,
      "startAt": "2025-10-10T19:00:00",
      "endAt": "2025-10-10T21:30:00"
    }'
    ```

---

### 응답

- 상태코드: `200 OK`
- 본문 예시

    ```json
    {
      "scheduleId": 1
    }
    ```

---

### 테스트

- [x] 올바른 접근 토큰과 유효한 요청을 보내면 SUCCESS 상태코드를 반환한다
- [x] ADMIN 권한을 가진 사용자가 올바른 요청을 하는 경우 SUCCESS 상태코드를 반환한다
- [x] 응답 본문에 scheduleId가 포함된다
- [x] 권한이 없는 사용자 토큰으로 요청하면 FORBIDDEN 상태코드를 반환한다
- [x] runtimeMinutes가 0 이하일 경우 BAD_REQUEST를 반환한다
- [x] runtimeMinutes은 startAt과 endAt의 차이만큼이 아니면 BAD_REQUEST를 반환한다
- [x] startAt이 endAt보다 늦은 경우 BAD_REQUEST를 반환한다
- [x] 존재하지 않는 showId를 보내면 NOT_FOUND 상태코드를 반환한다
- [x] 공연 기간 범위를 벗어나는 startAt 또는 endAt을 보낼 경우 BAD_REQUEST를 반환한다
- [x] 동일한 hallId와 시간이 겹치는 회차를 등록하려 하면 INTERNAL_SERVER_ERROR를 반환한다
