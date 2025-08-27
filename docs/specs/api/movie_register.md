### 요청

- 메서드: `POST`
- 경로: `/api/movie`
- 헤더

    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```

- 본문

    ```json
    
    ```


- curl 명령 예시

    ```bash
      curl -i -X POST 'http://localhost:8080/api/movie' \
      -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsInVzZXJJZCI6InRlc3QxMjM0Iiwibmlja05hbWUiOiJ0ZXN0IiwiaWF0IjoxNzU1ODQ3MzY1LCJleHAiOjE3NTU4NDc5NjV9.qivq2xlrm8me6P0oSwFLfieubmtoUB44NTSp2idDRRLG2wWE4S_4nNMJyEbEwjwaxfHpYQdzOTw0uscvNJCoKQ' \
      -H 'Content-Type: application/json' \
      -d '{
        "title": "인셉션",
        "director": "크리스토퍼 놀란",
        "runtimeMinutes": 148,
        "genre": "SF",
        "releaseDate": "2010-07-21",
        "rating": "12세 관람가",
        "synopsis": "타인의 꿈속에 진입해 아이디어를 주입하는 특수 임무를 수행하는 이야기.",
        "posterUrl": "https://example.com/posters/inception.jpg",
        "cast": [
          "레오나르도 디카프리오",
          "조셉 고든레빗",
          "엘렌 페이지"
        ]
      }'
    ```

### 응답

- 상태코드: `200 OK`
- 본문

    ```json
    {
    }
    ```

### 테스트

- [x] 올바른 요청을 보내면 status가 SUCCESS이다
- [x] Authorization 헤더에 유효한 accessToken이 없으면 status가 UNAUTHORIZED이다 
- [x] title, director, runtimeMinutes, genre, releaseDate, rating이 비어있으면 BAD_REQUEST이다
- [ ] runtimeMinutes은 0 미만이면 BAD_REQUEST이다
- [ ] releaseDat는 ISO 8601 양식을 준수하지 않으면 BAD_REQUEST이다
