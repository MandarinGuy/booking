---
name: Feature request
about: Suggest an idea for this project
title: "[FEAT] "
labels: ''
assignees: ''

---

## 📌 기능 개요
`<method> /api/<path>` 

---

## 🎯 요청 명세

- **메서드**: `<method>`
- **경로**: `/api/<path>`
- **헤더**
    ```http
    Content-Type: application/json
    ```
- **본문**
    ```json
    {

    }
    ```
- **curl 예시**
    ```bash
    curl -i -X <method> 'http://localhost:8080/api/<path>' \
    -H 'Content-Type: application/json' \
    -d '{

    }'
    ```

---

## 📌 응답 명세
- 상태코드: `200 OK`
- 본문: 
```json
    {

    }
```

---

## ✅ 테스트 시나리오
- [ ] 

---

## 💡 비고
