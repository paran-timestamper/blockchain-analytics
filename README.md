# blockchain-analytics

Cosmos SDK 기반 체인의 2년치 트랜잭션 약 2억 건을 적재해, 어떤 토큰에 돈이 몰리고 어느 체인을 거쳐 움직이는지를 비전공자도 읽을 수 있게 만든 온체인 데이터 가시화 프로젝트입니다. 아주대학교 파란학기제 2025-1 성과발표에서 수상했습니다.

## 프로젝트 개요
- **데이터**: Cosmos SDK 기반 체인의 2년치 트랜잭션 약 2억 건을 PostgreSQL(AWS RDS)에 적재했습니다.
- **가시화**: 노드 맵, 히트맵, 트리맵 등 6가지 표현을 같은 데이터에 붙여 어떤 표현이 무엇을 잘 보여주는지 비교했습니다. 2년치를 3개월 단위 동심원과 하단 타임라인으로 나눠 시계열 변화를 볼 수 있게 했습니다.
- **추적 범위**: 체인 내부 단위(`uatom`)와 표시 이름(`ATOM`), 송신·수신 체인을 따로 저장해 IBC로 체인을 건너는 흐름까지 그릴 수 있습니다.
- **팀**: 3명. fl0wizy는 UI 설계, 수집부터 화면까지의 데이터 파이프라인 설계, Vue 프론트엔드를 맡았습니다.
- **기간**: 2025.03 ~ 2025.06

### Overview (English)
An on-chain data visualization project built on about 200 million transactions covering two years of Cosmos SDK chains. It puts six views (node maps, heatmaps, treemaps and more) on the same data so non-specialists can see which tokens draw money and which chains it moves through, and it records base units apart from display names and source and destination chains separately to trace IBC transfers. Team of 3; fl0wizy owned the UI design, the pipeline design from collection to screen, and the Vue frontend. Award at the Ajou Paran Semester 2025-1 showcase.

## 개발 가이드

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
| 컬럼명 | 데이터 타입 | 설명                                    |
|--------|------------|---------------------------------------|
| `type` | `VARCHAR(20)` | 트랜잭션 유형 (예: `Send`, `IBCSend`)        |
| `txhash` | `VARCHAR(100)` | 트랜잭션 해시 값 (고유한 식별자)                   |
| `timestamp` | `BIGINT` | 트랜잭션 발생 시간 (Unix Timestamp, 밀리초 단위)   |
| `amount` | `NUMERIC(30,10)` | 전송된 토큰 개수                             |
| `denom` | `VARCHAR(20)` | 내부에서 사용하는 토큰 단위 (예: `uatom`, `uscrt`) |
| `dpDenom` | `VARCHAR(20)` | 사람이 이해하기 쉬운 토큰 이름 (예: `ATOM`, `SCRT`) |

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
     amount NUMERIC(40,10) NOT NULL,
     denom VARCHAR(100) NOT NULL,
     dpDenom VARCHAR(30) NOT NULL,
     fromAddress VARCHAR(100) NOT NULL,
     toAddress VARCHAR(100) NOT NULL,
     fromChain VARCHAR(50) NOT NULL,
     fromChainId VARCHAR(50) NOT NULL,
     toChain VARCHAR(50) NOT NULL,
     toChainId VARCHAR(50) NOT NULL
);

-- 인덱스 추가 (조회 성능 향상)
CREATE INDEX idx_txhash ON transaction(txhash);
CREATE INDEX idx_from_address ON transaction(fromAddress);
CREATE INDEX idx_to_address ON transaction(toAddress);


-- 타임스탬프 변환을 위한 컬럼 추가
ALTER TABLE transaction
    ADD COLUMN converted_date TIMESTAMP;

UPDATE transaction
SET converted_date = TO_TIMESTAMP(timestamp / 1000);

-- category column 추가
ALTER TABLE token ADD COLUMN category VARCHAR(20);

ALTER TABLE token ADD COLUMN generation_date VARCHAR(20);
```

## API 명세 보는법
- backend 서버 실행 후 다음 주소로 접근 (http://localhost:8080/swagger-ui.html)
