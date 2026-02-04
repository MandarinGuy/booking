## 토큰 재발급

### 요청
- 메서드: `POST`
- 경로: `/api/auth/reissue`
- 헤더
  - `Content-Type: application/json`
- 본문
  - `refreshToken` (string, 필수, 공백 불가)

```json
{
  "refreshToken": "string"
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
  - refresh token 서명 오류/만료/사용자 불일치

메시지 내용은 예외 메시지 기반이며 **확인 불가**.

### 테스트
- 올바른 refresh token으로 요청하면 `SUCCESS`를 반환한다
- 올바른 refresh token으로 요청하면 새로운 access token과 refresh token을 발급한다
- 응답받은 access token과 refresh token은 유효한 JWT 형식이다
- 응답받은 access token과 refresh token은 만료되지 않는다
- 요청 토큰의 서명이 잘못된 경우 `UNAUTHORIZED`가 발생한다
- 요청 body가 누락된 경우 `BAD_REQUEST`가 발생한다
- refreshToken이 공백/빈 문자열이면 `BAD_REQUEST`가 발생한다
- 존재하지 않는 사용자의 refresh token을 요청하면 `UNAUTHORIZED`가 발생한다
- 만료된 refresh token으로 요청하면 `UNAUTHORIZED`가 발생한다
