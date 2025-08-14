### 요청

- 메서드: `POST`
- 경로: `/api/auth/reissue`
- 헤더

   ```
   Content-Type: application/json
   ```

- 본문

   ```json
   {
       "refreshToken":"refreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefresh"
   }
   ```


- curl 명령 예시

   ```bash
  curl -i -X POST 'http://localhost:8080/api/auth/refresh' \
  -H 'Content-Type: application/json' \
  -d '{
      "refreshToken":"refreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefresh"
  }'
   ```

### 응답

- 상태코드: `200 OK`
- 본문

   ```json
   {
        "accessToken":"accessaccessaccessaccessaccessaccessaccessaccessaccessaccess",
        "refreshToken":"refreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefreshrefresh"
   }

   ```

### 테스트

- [x] 올바른 refresh token으로 요청하면 200을 응답한다
- [x] 올바른 refresh token으로 요청하면 새로운 access token과 refresh token을 발급해 응답한다
- [x] 응답받은 access toke과 refresh toke은 유효한 JWT 형식이다
- [ ] 요청 토큰의 서명이 잘못된 경우 401 Unauthorized가 발생한다
- [ ] 요청 body가 누락된 경우 400 Bad Request가 발생한다
- [ ] 만료된 refresh token으로 요청하면 401 Unauthorize가 발생한다
- [ ] 존재하지 않는 사용자의 refresh token을 요청하면 401 Unauthorize가 발생한다
