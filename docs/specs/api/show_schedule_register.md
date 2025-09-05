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
      "hallId": 10,
      "startAt": "2025-09-10T19:00:00",
      "endAt": "2025-09-10T21:30:00",
      "runtimeMinutes": 150
    }
    ```

- curl 명령 예시

    ```bash
    curl -i -X POST 'http://localhost:8080/api/show/schedule' \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer <accessToken>' \
    -d '{
      "showId": 1,
      "hallId": 10,
      "startAt": "2025-09-10T19:00:00",
      "endAt": "2025-09-10T21:30:00",
      "runtimeMinutes": 150
    }'
    ```

---

### 응답

- 상태코드: `200 OK`
- 본문 예시

    ```json
    {
      "showId": 1,
      "hallId": 10,
      "startAt": "2025-09-10T19:00:00",
      "endAt": "2025-09-10T21:30:00",
      "runtimeMinutes": 150
    }
    ```

---

### 테스트

- [x] 올바른 접근 토큰과 유효한 요청을 보내면 SUCCESS 상태코드를 반환한다
- [x] 응답 본문에 scheduleId가 포함된다
- [ ] 권한이 없는 사용자 토큰으로 요청하면 FORBIDDEN 상태코드를 반환한다
- [ ] runtimeMinutes가 0 이하일 경우 BAD_REQUEST를 반환한다
- [ ] startAt이 endAt보다 늦은 경우 BAD_REQUEST를 반환한다
- [ ] 존재하지 않는 showId를 보내면 NOT_FOUND 상태코드를 반환한다
- [ ] 존재하지 않는 hallId를 보내면 NOT_FOUND 상태코드를 반환한다
- [ ] 공연 기간 범위를 벗어나는 startAt 또는 endAt을 보낼 경우 BAD_REQUEST를 반환한다
- [ ] 동일한 hallId와 시간이 겹치는 회차를 등록하려 하면 INTERNAL_SERVER_ERROR를 반환한다
- [ ] 동일한 요청을 여러 번 전송하면 중복 스케줄이 생성되지 않고 INTERNAL_SERVER_ERROR 또는 기존 스케줄 ID를 반환한다  
