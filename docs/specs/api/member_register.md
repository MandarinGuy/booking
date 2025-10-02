### 요청

- 메서드: `POST`
- 경로: `/api/member`
- 헤더

    ```
    Content-Type: application/json
    ```

- 본문

    ```json
    {
        "nickName": "string",
        "userId": "string",
        "password": "string",
        "email": "string"
    }
    ```

curl 명령 예시

  ```bash
  curl -i -X POST 'http://localhost:8080/api/member' \
  -H 'Content-Type: application/json' \
  -d '{
      "nickName": "test",
      "userId": "test1234",
      "password": "myPassword123",
      "email": "test@gmail.com"
  }'
  ```

### 응답

- 상태코드: `200 OK`
- 본문

    ```json
    {
        "nickName": "test",
        "userId": "test1234",
        "email": "test@gmail.com"
    }
    ```

    ```json
    {
        "nickName": "test",
        "userId": "test1234",
        "email": "test@gmail.com"
    }
    ```

### 테스트

- [x] 올바른 요청하면 200 OK 상태코드를 반환한다
- [x] 올바른 회원가입 요청을 하면 데이터베이스에 회원 정보가 저장된다
- [x] 빈 값이나 null 값이 포함된 요청을 하면 400 Bad Request 상태코드를 반환한다
- [x] 이미 존재하는 userId로 회원가입 요청을 하면 400 Bad Request 상태코드를 반환한다
- [x] 이미 존재하는 email로 회원가입 요청을 하면 400 Bad Request 상태코드를 반환한다
- [x] 올바르지 않은 형식의 email로 회원가입을 시도하면 400 Bad Request 상태코드를 반환한다
- [x] 비밀번호가 올바르게 암호화 된다
- [x] 회원가입 후 반환된 응답에 회원 정보가 포함된다
