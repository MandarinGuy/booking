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
      -H 'Authentication: <<accessToken>>' \
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

- [ ] 올바른 요청을 보내면 status가 SUCCESS이다
- [ ] title, director, runtimeMinutes, genre, releaseDate, rating은 비어있을 수 없다
- [ ] runtimeMinutes은 0 이상이어야 한다
- [ ] releaseDat는 ISO 8601 양식을 준수한다
