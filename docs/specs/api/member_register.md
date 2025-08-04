### 요청

- 메서드: `POST`
- 경로: `/api/member`
- 헤더

    ```
    Content-Type: application/json
    Authorization: Bearer <accessToken>
    ```

- 본문

    ```java
    MemberRegisterRequest(String nickName, String userId, String passwordHash, String email) {
    }
    ```
 curl 명령 예시

    ```bash
    curl -i -X POST '<http://localhost:8080/api/member>' \\
    -H 'Content-Type: application/json' \\
    -d '
    {'
        "nickName": "test",
        "userId": "test1234",
        "passwordHash": "$2a$10$EIXj1Z5z5Q8b7f3e4d9eOe",
        "email": "test@gmail.com"
    }'
    ```

### 응답

- 상태코드: `200 OK`
- 본문

    ```java

    ```


### 테스트

- [x] 올바른 요청하면 200 OK 상태코드를 반환한다
- [x] 올바른 회원가입 요청을 하면 데이터베이스에 회원 정보가 저장된다
- [x] 빈 값이나 null 값이 포함된 요청을 하면 400 Bad Request 상태코드를 반환한다
- [x] 이미 존재하는 userId로 회원가입 요청을 하면 400 Bad Request 상태
