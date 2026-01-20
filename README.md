# [SB] 스프린트 미션 5 - Discodeit (디스코드잇)

Spring Boot 기반의 실시간 채팅 애플리케이션 **Discodeit** 프로젝트입니다.  
RESTful API 설계, Swagger 문서화, 그리고 React 기반 프론트엔드와의 연동을 포함합니다.

## 주요 기능

- **사용자 관리**: 회원가입, 로그인, 정보 수정, 상태 관리 (온라인/오프라인)
- **채널 관리**: 공개/비공개 채널 생성 및 관리
- **메시지 기능**: 실시간 메시지 전송, 파일 첨부 (이미지 등)
- **읽음 상태**: 채널별 메시지 읽음 상태 관리
- **API 문서화**: Swagger (OpenAPI 3.0) 기반의 자동화된 API 명세 제공

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.x
- **Frontend**: React (Single Page Application)
- **Database**:
    - `JCF` (In-Memory) 또는 `File` (Serialized Object) 저장소 선택 가능
- **API Documentation**: springdoc-openapi (Swagger UI)

## 실행 방법

### 1. 백엔드 실행

프로젝트 루트에서 다음 명령어를 실행합니다.

```bash
./gradlew bootRun
```

서버는 기본적으로 `8080` 포트에서 실행됩니다.

### 2. 애플리케이션 접속

브라우저에서 다음 주소로 접속하여 애플리케이션을 사용할 수 있습니다.

- **메인 화면**: [http://localhost:8080](http://localhost:8080)

### 3. API 문서 확인 (Swagger UI)

API 명세 및 테스트를 위해 Swagger UI를 제공합니다.

- **Swagger UI
  **: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 설정 (`application.yaml`)

`src/main/resources/application.yaml` 파일에서 저장소 타입을 설정할 수 있습니다.

```yaml
discodeit:
  repository:
    # type: jcf (메모리) | file (파일 시스템)
    type: file
    file-directory: .discodeit # 파일 저장 경로
```

## 프로젝트 구조

```
src
└─ main
   ├─ java
   │  └─ com.sprint.mission.discodeit
   │     ├─ controller      # REST API 컨트롤러
   │     ├─ service         # 비즈니스 로직
   │     ├─ repository      # 데이터 저장소 (JCF/File)
   │     ├─ entity          # 도메인 엔티티
   │     ├─ dto             # 데이터 전송 객체 (Request/Response)
   │     └─ config          # 설정 클래스
   └─ resources
      ├─ static             # React 프론트엔드 빌드 파일
      └─ application.yaml   # 애플리케이션 설정
```

## API 리팩토링 내역

- **RESTful API 적용**: 자원 중심의 URL 설계 및 적절한 HTTP 메서드(GET, POST, PATCH, DELETE) 사용
- **Multipart 지원**: 프로필 이미지 및 파일 첨부 기능을 위한 `multipart/form-data` 처리 구현
- **DTO 개선**: 프론트엔드 스펙에 맞춘 요청/응답 DTO 구조 최적화
- **Swagger 적용**: `@Operation`, `@ApiResponse` 등을 활용한 상세한 API 문서화

---