## 회원가입

### 요청
- 메서드: `POST`
- 경로: `/api/member`
- 헤더
  - `Content-Type: application/json`
- 본문
  - `nickName` (string, 필수, 공백 불가)
  - `userId` (string, 필수, 공백 불가)
  - `password` (string, 필수, 공백 불가)
  - `email` (string, 필수, 공백 불가, 이메일 정규식)

```json
{
  "nickName": "string",
  "userId": "string",
  "password": "string",
  "email": "string"
}
```

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `userId` (string)
  - `nickName` (string)
  - `email` (string)

```json
{
  "status": "SUCCESS",
  "data": {
    "userId": "string",
    "nickName": "string",
    "email": "string"
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - 필수 필드 누락/공백
  - 이메일 형식 오류
  - 중복 userId 또는 email

메시지(테스트 기준)
- `Nickname cannot be blank`
- `User ID cannot be blank`
- `Password cannot be blank`
- `Email cannot be blank`
- `Invalid email format`
- `이미 존재하는 회원입니다:`
- `이미 존재하는 이메일입니다:`

### 테스트
- 올바른 요청하면 `200 OK`를 반환한다
- 올바른 회원가입 요청을 하면 데이터베이스에 회원 정보가 저장된다
- 빈 값이나 null 값이 포함된 요청을 하면 `BAD_REQUEST`를 반환한다
- userId가 누락되거나 공백이면 `BAD_REQUEST`를 반환한다
- password가 누락되거나 공백이면 `BAD_REQUEST`를 반환한다
- email이 누락되거나 공백이면 `BAD_REQUEST`를 반환한다
- 이미 존재하는 `userId`로 회원가입 요청을 하면 `BAD_REQUEST`를 반환한다
- 이미 존재하는 `email`로 회원가입 요청을 하면 `BAD_REQUEST`를 반환한다
- 올바르지 않은 형식의 email로 회원가입을 시도하면 `BAD_REQUEST`를 반환한다
- 비밀번호가 올바르게 암호화된다
- 회원가입 후 반환된 응답에 회원 정보가 포함된다
