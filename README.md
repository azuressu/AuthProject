## Spring Boot 기반 JWT 인증/인가 및 AWS 배포

### 기능 구현
🚩 **Spring Boot**를 이용한 JWT 인증/인가 로직 및 API 구현
<br>
🚩 **Junit** 기반의 테스트 코드 작성
<br>
🚩 **Swagger** 로 API 문서화
<br>
🚩 애플리케이션을 **AWS EC2**에 배포하고, 실제 환경에서 실행되도록 구성
<br>

### API 명세서
[SWAGGER UI 주소(API 명세서)](http://13.125.213.208:8080/swagger-ui/index.html#)

### API 엔드포인트 URL
- [회원가입](http://13.125.213.208:8080/signup)
  ```json
  {
    "username": "LI",
    "password": "passpass",
    "nickname": "LIGHT"
  }
  ```
- [로그인](http://13.125.213.208:8080/login)
  ```json
  {
    "username": "LI",
    "password": "passpass"
  }
  ```
- [관리자 로그인](http://13.125.213.208:8080/admin/login)
  ```json
  {
    "username": "BE",
    "password": "passpass",
    "nickname": "admin",
    "adminKey": "adminKey"
  }
  ```
- [관리자 권한 부여](http://13.125.213.208:8080/admin/users/1/roles)

### ERD
![스크린샷 2025-06-11 134335](https://github.com/user-attachments/assets/2e87923f-daef-4faf-8f8f-c14b578eea57)
