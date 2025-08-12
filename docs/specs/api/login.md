### 요청

- 메서드: `POST`
- 경로: `/auth/login`
- 헤더
    
    ```
    Content-Type: application/json
    ```
    
- 본문
    
    ```json
    {
        "username": "string",
        "password": "string"
    }
    
    ```
    

- curl 명령 예시
    
    ```bash
    curl -i -X POST '<http://localhost:8080/auth/login' \\
    -H 'Content-Type: application/json' \\
    -d '
      {
        "username": "string",
        "password": "string"
      }'
    ```
    

### 응답

- 상태코드: `200 OK`
- 본문
    
    ```json
    {
        "accessToken": "string",
        "refreshToken": "string"
    }
    ```
    

### 테스트

- [x] 올바른 요청을 보내면 200 OK 상태코드를 반환한다
- [x] 요청 본문의 userId가 누락된 경우 400 Bad Request 상태코드를 반환한다
- [x] 요청 본문의 password가 누락된 경우 400 Bad Request 상태코드를 반환한다
- [x] 존재하지 않는 userId 비밀번호로 요청하면 401 Unauthorized 상태코드를 반환한다
- [x] 요청 본문의 password가 userId에 해당하는 password가 일치하지 않으면 401 Unauthorized 상태코드를 반환한다
- [x] 성공적인 로그인 후 응답에 accessToken과 refreshToken가 포함되어야 한다
- [x] 전달된 토큰은 유효한 JWT 형식이어야 한다
- [x] 전달된 토큰은 만료되지 않아야한다
- [x] 전달된 토큰에는 사용자의 userId가 포함되어야 한다
