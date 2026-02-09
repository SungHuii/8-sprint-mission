# [SB] 스프린트 미션 6 - Discodeit (디스코드잇)

Spring Boot 기반의 실시간 채팅 애플리케이션 **Discodeit** 프로젝트입니다.  
RESTful API 설계, Swagger 문서화, 그리고 React 기반 프론트엔드와의 연동을 포함합니다.
이번 미션에서는 **Spring Data JPA**를 도입하여 데이터 접근 계층을 고도화하고, **성능 최적화** 및 **커서 기반 페이지네이션**을 적용했습니다.

## 주요 기능

- **사용자 관리**: 회원가입, 로그인, 정보 수정, 상태 관리 (온라인/오프라인)
- **채널 관리**: 공개/비공개 채널 생성 및 관리
- **메시지 기능**: 실시간 메시지 전송, 파일 첨부 (이미지 등), **커서 기반 페이징**
- **읽음 상태**: 채널별 메시지 읽음 상태 관리
- **API 문서화**: Swagger (OpenAPI 3.0) 기반의 자동화된 API 명세 제공

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.4, Spring Data JPA
- **Frontend**: React (Single Page Application)
- **Database**: PostgreSQL
- **API Documentation**: springdoc-openapi (Swagger UI)
- **Mapping**: MapStruct

## 실행 방법

### 1. 데이터베이스 준비 (PostgreSQL)

`src/main/resources/application.yaml`에 설정된 정보로 PostgreSQL 데이터베이스가 실행 중이어야 합니다.  
(기본 설정: `localhost:5432`, DB명: `discodeit`, 사용자: `discodeit_user`, 비밀번호: `discodeit1234`)

### 2. 백엔드 실행

프로젝트 루트에서 다음 명령어를 실행합니다.

```bash
./gradlew bootRun
```

서버는 기본적으로 `8080` 포트에서 실행됩니다.
최초 실행 시 `schema.sql`에 의해 테이블이 자동으로 생성됩니다.

### 3. 애플리케이션 접속

브라우저에서 다음 주소로 접속하여 애플리케이션을 사용할 수 있습니다.

- **메인 화면**: [http://localhost:8080](http://localhost:8080)

### 4. API 문서 확인 (Swagger UI)

API 명세 및 테스트를 위해 Swagger UI를 제공합니다.

- **Swagger UI
  **: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 설정 (`application.yaml`)

데이터베이스 연결 정보 및 JPA 설정을 변경할 수 있습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/discodeit
    username: discodeit_user
    password: discodeit1234
  jpa:
    hibernate:
      ddl-auto: validate # schema.sql로 관리하므로 validate 사용
    open-in-view: false # OSIV 비활성화 (성능 최적화)
```

## 프로젝트 구조

```
src
└─ main
   ├─ java
   │  └─ com.sprint.mission.discodeit
   │     ├─ controller      # REST API 컨트롤러
   │     ├─ service         # 비즈니스 로직 (@Transactional)
   │     ├─ repository      # Spring Data JPA 리포지토리
   │     ├─ entity          # JPA 엔티티
   │     ├─ dto             # 데이터 전송 객체 (Request/Response)
   │     ├─ mapper          # MapStruct 매퍼
   │     └─ storage         # 파일 저장소 (Local)
   └─ resources
      ├─ static             # React 프론트엔드 빌드 파일
      ├─ schema.sql         # DB 스키마 정의 (DDL)
      └─ application.yaml   # 애플리케이션 설정
```

## 리팩토링 및 개선 내역 (스프린트 미션 6)

### 1. Spring Data JPA 도입

- 기존 파일/메모리 기반 저장소를 **JPA (Hibernate)**로 전면 교체.
- **Entity 연관관계 매핑**: 객체 지향적인 모델링 적용 (`@ManyToOne`, `@OneToMany` 등).
- **Auditing 적용**: `BaseEntity`를 통해 생성일/수정일 자동 관리.

### 2. 성능 최적화 (N+1 문제 해결)

- **Fetch Join (`@EntityGraph`)**: `Message` 조회 시 작성자 및 첨부파일을 한 번에 로딩.
- **Batch Size (`@BatchSize`)**: `Channel` 조회 시 참여자 목록(`ReadStatus`)을 효율적으로 로딩.
- **일괄 조회 로직**: 채널 목록 조회 시 반복 쿼리 대신 `IN` 절을 활용한 일괄 조회 및 메모리 매핑 적용.
- **OSIV 비활성화**: DB 커넥션 점유 시간을 줄여 애플리케이션 확장성 확보.

### 3. 커서 기반 페이지네이션 (Cursor Pagination)

- 대용량 메시지 데이터 조회를 위해 기존 Offset 방식(`page`, `size`)을 **Cursor 방식**(`cursor`, `size`)으로 변경.
- 마지막 조회된 메시지의 시간(`createdAt`)을 커서로 사용하여 성능 저하 없이 데이터 조회 가능.

### 4. 바이너리 데이터 처리 개선

- DB에 바이너리 데이터를 직접 저장(`byte[]`)하던 방식에서 **로컬 파일 시스템 저장** 방식으로 변경.
- DB에는 파일 메타데이터만 저장하여 DB 부하 감소 및 용량 확보.

### 5. API 스펙 준수 및 문서화

- 프론트엔드 요구사항 및 변경된 API 스펙(v1.2)을 완벽하게 반영.
- Swagger를 통해 변경된 API 명세(응답 코드, 파라미터 등)를 명확하게 제공.

## 운영 안정성 및 테스트 도입 (스프린트 미션 7 추가)

### 1. 테스트 코드 작성

- **단위/슬라이스/통합 테스트**: 서비스, 레포지토리, 컨트롤러 및 전체 통합 테스트를 작성하여 안정성 확보.
- **JaCoCo 도입**: 테스트 커버리지 리포트를 생성하고 관리 (서비스 레이어 커버리지 60% 이상 달성).

### 2. 모니터링 시스템 구축

- **Spring Boot Actuator**: 애플리케이션 상태(Health, Metrics) 모니터링 엔드포인트 제공.
- **Spring Boot Admin**: 별도의 Admin 서버를 구축하여 시각화된 대시보드 제공.

### 3. 로깅 및 예외 처리 고도화

- **MDC 로깅**: 요청별 고유 ID(Request-ID)를 로그에 포함하여 트랜잭션 추적 용이성 확보.
- **전역 예외 처리**: `GlobalExceptionHandler`와 커스텀 예외 체계를 통해 일관된 에러 응답 제공.

### 4. 운영 환경 최적화

- **프로파일 분리**: 개발(`dev`)과 운영(`prod`) 환경 설정을 분리하여 유연한 배포 환경 구성.
- **유효성 검사**: DTO Validation(`@Valid`)을 통해 잘못된 요청을 조기에 차단.
