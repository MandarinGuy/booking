## 로그인

### 요청
- 메서드: `POST`
- 경로: `/api/auth/login`
- 헤더
  - `Content-Type: application/json`
- 본문
  - `userId` (string, 필수, 공백 불가)
  - `password` (string, 필수, 공백 불가)

```json
{
  "userId": "string",
  "password": "string"
}
```

### 응답(성공)
- 상태코드: `200 OK`
- 공통 래퍼: `status`, `data`, `timestamp`
- data
  - `accessToken` (string)
  - `refreshToken` (string)

```json
{
  "status": "SUCCESS",
  "data": {
    "accessToken": "string",
    "refreshToken": "string"
  },
  "timestamp": "2025-01-01T00:00:00"
}
```

### 응답(실패)
- `400 BAD_REQUEST`
  - 필수 필드 누락/공백
- `401 UNAUTHORIZED`
  - 인증 실패(존재하지 않는 userId 또는 비밀번호 불일치)

메시지 내용은 예외 메시지 기반이며 **확인 불가**.

### 테스트
- 올바른 요청을 보내면 `SUCCESS`를 반환한다
- 요청 본문의 `userId`가 누락된 경우 `BAD_REQUEST`를 반환한다
- 요청 본문의 `password`가 누락된 경우 `BAD_REQUEST`를 반환한다
- 존재하지 않는 `userId` 비밀번호로 요청하면 `UNAUTHORIZED`를 반환한다
- `password`가 `userId`에 해당하는 password와 일치하지 않으면 `UNAUTHORIZED`를 반환한다
- 성공적인 로그인 후 응답에 `accessToken`, `refreshToken`이 포함된다
- 전달된 토큰은 유효한 JWT 형식이다
- 전달된 토큰은 만료되지 않는다
- 전달된 토큰에는 사용자의 `userId`가 포함된다
