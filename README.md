# blockchain-analytics


### frontend 실행 방법
1. frontend 폴더로 이동
2. npm run serve
3. http://localhost:8082/ 으로 접속

** 화면 작업만 할 경우 매번 서버를 키지않고 ctrl(command) + s 누르면 바로 반영 가능


### backend 실행 방법
1. frontend 폴더에서 npm run build 실행
2. frontend 폴더에서 npm run serve 실행
3. backend 폴더에서 src > main > java > com > blockchain > blockchain > BlockchainApplication.java 실행
4. http://localhost:8080/ 으로 접속

** backend 실행 시 frontend 서버도 같이 실행되어 있어야 함


### 프로젝트 구조
- frontend: Vue.js
- backend: Spring Boot (Java)
- DB: PostgreSQL (AWS RDS)


### git branch 전략
- main: 배포용 브랜치
- develop: 개발용 브랜치

* 기능 별로 잘게 쪼개서 브런치를 만들고 작업하기
* 브런치 이름은 이름_기능명 으로 작성하기 (ex. khkim_mainPage 개발)
* 작업이 완료되면 develop 브런치로 PR 보내기 (assignee: khkim, reviewer: khkim)
* 큰 기능이 개발될 때마다 develop -> main 브런치로 khkim 가 머지 진행.
