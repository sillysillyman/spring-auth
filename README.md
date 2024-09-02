## Requirements

- [ ] JUnit을 이용한 테스트 코드 작성법 이해
- [ ] Spring Security를 이용한 Filter에 대한 이해
- [ ] JWT와 구체적인 알고리즘의 이해
- [ ] PR 날려보기
- [ ] 리뷰 바탕으로 개선하기
- [ ] EC2에 배포해보기

## 시나리오 설계

### Spring Security 기본 이해

- [ ] Filter란 무엇인가?(with Interception, AOP)
- [ ] Spring Security란?

### JWT 기본 이해

- [ ] JWT란 무엇인가요?

### 토큰 발행과 유효성 확인

- [ ] Access/Refresh Token 발행과 검증에 관한 테스트 시나리오 작성하기

### 유닛 테스트 작성

- [ ] JUnit을 이용한 JWT Unit 테스트 코드 작성해보기
  <br> https://preasim.github.io/39
  <br> https://velog.io/@da_na/Spring-Security-JWT-Spring-Security-Controller-Unit-Test%ED%95%98%EA%B8%B0

## 백엔드 배포하기

### 테스트 완성

- [ ] 백엔드 유닛 테스트 완성하기

### 로직 작성

- [ ] 백엔드 로직은 Spring Boot로
- [ ] 회원가입 - /signup
    - [ ] Request Message
      ```json
      {
        "username": "test_username",
        "password": "test_password",
        "nickname": "test_nickname"
      }
      ```
    - [ ] Response Message
      ```json
      {
        "username": "test_username",
        "nickname": "test_nickname",
        "authorities": [
          {
            "authorityName": "ROLE_USER"
          }
        ]		
      }
      ```
- [ ] 로그인 - /login
    - [ ] Request Message
      ```json
      {
        "username": "test_username",
        "password": "test_password"
      }
      ```
    - [ ] Response Message
      ```json
      {
        "token": "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL"
      }
      ```

### 배포해보기

- [ ] AWS EC2에 배포하기

### API 접근과 검증

- [ ] Swagger UI로 접속 가능하게 하기

## 백엔드 개선하기

### AI-assisted programming

- [ ] AI에게 코드리뷰 받아보기

### Refactoring

- [ ] 피드백 받아서 코드 개선하기

### 재배포

- [ ] AWS EC2 재배포하기