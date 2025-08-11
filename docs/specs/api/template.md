### 요청

- 메서드: `POST`
- 경로: `/<requestPath>`
- 헤더
    
    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```
    
- 본문
    
    ```java
    
    ```
    

- curl 명령 예시
    
    ```bash
    curl -i -X POST '<http://localhost:8080/<requestPath>>' \\
    -H 'Content-Type: application/json' \\
    -H 'Authorization: Bearer <accessToken>' \\
    -d ''
    ```
    

### 응답

- 상태코드: `200 OK`
- 본문
    
    ```java
    
    ```
    

### 테스트

- [ ]  올바른 접근 토큰과 함께 요청하면 200 OK 상태코드를 반환한다.
