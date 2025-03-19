# blockchain-analytics


### frontend 실행 방법
1. frontend 폴더로 이동
2. npm run serve
3. http://localhost:8082/ 으로 접속

** 화면 작업만 할 경우 매번 서버를 키지않고 ctrl(command) + s 누르면 바로 반영 가능


### backend 실행 방법
1. frontend 폴더에서 npm run build 실행
2. frontend 폴더에서 npm run serve 실행
3. backend 폴더에서 src > main > java > com > timestamper > blockchain_analytics > BlockchainApplication.java 실행
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
* 브런치 이름은 이름_기능명 으로 작성하기 (ex. khkim_mainPage 화면 개발)
* 작업이 완료되면 develop 브런치로 PR 보내기 (assignee: khkim, reviewer: khkim)
* 큰 기능이 개발될 때마다 develop -> main 브런치로 khkim 가 머지 진행.


# 📌 블록체인 트랜잭션 데이터 컬럼 설명

이 테이블은 블록체인에서 발생한 트랜잭션 정보를 저장하며, 각 컬럼의 의미는 다음과 같습니다.

## 1️⃣ 트랜잭션 기본 정보
| 컬럼명 | 데이터 타입 | 설명 |
|--------|------------|------|
| `type` | `VARCHAR(20)` | 트랜잭션 유형 (예: `Send`, `IBCSend`) |
| `txhash` | `VARCHAR(100)` | 트랜잭션 해시 값 (고유한 식별자) |
| `timestamp` | `BIGINT` | 트랜잭션 발생 시간 (Unix Timestamp, 밀리초 단위) |
| `amount` | `NUMERIC(30,10)` | 전송된 토큰 개수 |
| `denom` | `VARCHAR(20)` | 내부에서 사용하는 자산 단위 (예: `uatom`, `uscrt`) |
| `dpDenom` | `VARCHAR(20)` | 사람이 이해하기 쉬운 자산 이름 (예: `ATOM`, `SCRT`) |

## 2️⃣ 송신자 및 수신자 정보
| 컬럼명 | 데이터 타입 | 설명 |
|--------|------------|------|
| `fromAddress` | `VARCHAR(100)` | 송신자의 블록체인 주소 |
| `toAddress` | `VARCHAR(100)` | 수신자의 블록체인 주소 |

## 3️⃣ 블록체인 네트워크 정보
| 컬럼명 | 데이터 타입 | 설명 |
|--------|------------|------|
| `fromChain` | `VARCHAR(50)` | 트랜잭션이 발생한 블록체인 네트워크 |
| `fromChainId` | `VARCHAR(50)` | 트랜잭션이 발생한 네트워크의 체인 ID |
| `toChain` | `VARCHAR(50)` | 트랜잭션이 전송된 대상 블록체인 네트워크 |
| `toChainId` | `VARCHAR(50)` | 대상 네트워크의 체인 ID |


## 4️⃣ DB 생성 쿼리

```sql
CREATE TABLE transaction (
     id SERIAL PRIMARY KEY,
     type VARCHAR(20) NOT NULL,
     txhash VARCHAR(100) UNIQUE NOT NULL,
     timestamp BIGINT NOT NULL,
     amount NUMERIC(30,10) NOT NULL,
     denom VARCHAR(20) NOT NULL,
     dpDenom VARCHAR(20) NOT NULL,
     fromAddress VARCHAR(100) NOT NULL,
     toAddress VARCHAR(100) NOT NULL,
     fromChain VARCHAR(50) NOT NULL,
     fromChainId VARCHAR(50) NOT NULL,
     toChain VARCHAR(50) NOT NULL,
     toChainId VARCHAR(50) NOT NULL
);

-- 인덱스 추가 (조회 성능 향상)
CREATE INDEX idx_txhash ON blockchain_transactions(txhash);
CREATE INDEX idx_from_address ON blockchain_transactions(fromAddress);
CREATE INDEX idx_to_address ON blockchain_transactions(toAddress);
```

ㅁㅁㅁㅁ

