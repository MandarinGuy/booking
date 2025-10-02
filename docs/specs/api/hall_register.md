### 요청

- 메서드: `POST`
- 경로: `/api/hall`
- 헤더

    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```

- 본문 예시 (Hall → Section → Seat 구조)

    ```json
    {
      "name": "Seoul Art Hall",
      "sections": [
        {
          "name": "A",
          "seats": [
            { "rowNumber": 1, "seatNumber": 1 },
            { "rowNumber": 1, "seatNumber": 2 }
          ]
        },
        {
          "name": "B",
          "seats": [
            { "rowNumber": 1, "seatNumber": 1 }
          ]
        }
      ]
    }
    ```

- curl 명령 예시

    ```bash
    curl -i -X POST 'http://localhost:8080/api/hall' \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsInJvbGVzIjoiUk9MRV9BRE1JTiIsInVzZXJJZCI6InRlc3QxMjM0Iiwibmlja05hbWUiOiJ0ZXN0IiwiaWF0IjoxNzU3MzExNDc5LCJleHAiOjE3NTczMTIwNzl9.xhEkuZEF0gZlvyX_F2kiAMEMGw_C2ZtGL8PmzLxhZQW32A9hmr6M0nauYEejXOFrZAb3nMdU3jFLxuhDWDbE2g' \
    -d '{
      "name": "Seoul Art Hall",
      "sections": [
        { "name": "A", "seats": [ { "rowNumber": 1, "seatNumber": 1 }, { "rowNumber": 1, "seatNumber": 2 } ] },
        { "name": "B", "seats": [ { "rowNumber": 1, "seatNumber": 1 } ] }
      ]
    }'
    ```

---

### 응답

- 상태코드: `200 OK`
- 본문 예시

    ```json
    {
      "hallId": 1
    }
    ```

---

### 테스트

- [x] ADMIN 권한의 토큰과 유효 본문으로 요청하면 SUCCESS와 hallId를 반환한다
- [x] 비ADMIN 토큰으로 요청하면 ACCESS_DENIED을 반환한다
- [x] 토큰이 무효하면 UNAUTHORIZED을 반환한다
- [x] name이 비어있으면 BAD_REQUEST을 반환한다
- [x] sections 빈 배열이면 BAD_REQUEST을 반환한다
- [x] section name이 비어있으면 BAD_REQUEST을 반환한다
- [x] seats 빈 배열이면 BAD_REQUEST을 반환한다
- [x] rowNumber 또는 seatNumber가 빈 문자인 경우 BAD_REQUEST을 반환한다
- [x] 동일 섹션 내 rowNumber와 seatNumber의 조합이 중복이면 BAD_REQUEST을 반환한다
- [x] 섹션 이름이 중복되면 BAD_REQUEST을 반환한다
- [x] hall을 등록하면 등록한 사용자 정보도 저장된다
- [x] hall name이 중복되면 INTERNAL_SERVER_ERROR을 반환한다
- [x] section name이 중복되면 BAD_REQUEST을 반환한다
- [x] 동일한 section 내에 중복된 죄석을 요청하면 BAD_REQUEST를 반환한다
- [x] hall 하위 정보가 잘못된 경우 hall도 저장되지 않는다
- [x] sections가 비어있으면 BAD_REQUEST를 반환한다
- [ ] section의 seats가 비어있으면 BAD_REQUEST를 반환한다
