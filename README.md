# [SB] 스프린트 미션 3

Spring Boot 기반 디스코드잇(Discodeit) 프로젝트입니다.  
JCF/File 저장소, DTO 기반 서비스, 신규 도메인(ReadStatus/UserStatus/BinaryContent)을 포함합니다.

## 목표
- Spring Boot + IoC/DI 기반으로 서비스/레포지토리 구성하기
- DTO를 활용한 서비스 계층 고도화
- 신규 도메인과 연관 로직 구현
- JCF/File Repository 구현체를 설정값으로 선택

## 핵심 기능
- User/Channel/Message CRUD + DTO 응답
- Auth 로그인 기능
- ReadStatus/UserStatus 관리
- BinaryContent 저장/조회
- 시간 타입: `Instant` 통일

## 실행
- IDE에서 `DiscodeitApplication` 실행
- 또는 `./gradlew bootRun`

## 설정
`src/main/resources/application.yaml`
```
# Repository 구현체 선택 및 파일 저장 경로
# type: jcf | file

discodeit:
  repository:
    type: jcf
    file-directory: .discodeit
```

## 프로젝트 구조
```
src
└─ main
   ├─ java
   │  └─ com.sprint.mission.discodeit
   │     ├─ DiscodeitApplication.java
   │     ├─ config
   │     │  └─ RepoProps.java
   │     ├─ dto
   │     │  ├─ auth
   │     │  ├─ binary
   │     │  ├─ channel
   │     │  ├─ message
   │     │  ├─ readstatus
   │     │  ├─ user
   │     │  └─ userstatus
   │     ├─ entity
   │     │  ├─ BinaryContent.java
   │     │  ├─ Channel.java
   │     │  ├─ Message.java
   │     │  ├─ ReadStatus.java
   │     │  ├─ User.java
   │     │  └─ UserStatus.java
   │     ├─ repository
   │     │  ├─ BinaryContentRepository.java
   │     │  ├─ ChannelRepository.java
   │     │  ├─ MessageRepository.java
   │     │  ├─ ReadStatusRepository.java
   │     │  ├─ UserRepository.java
   │     │  ├─ UserStatusRepository.java
   │     │  ├─ file
   │     │  └─ jcf
   │     ├─ service
   │     │  ├─ AuthService.java
   │     │  ├─ BinaryContentService.java
   │     │  ├─ ChannelService.java
   │     │  ├─ MessageService.java
   │     │  ├─ ReadStatusService.java
   │     │  ├─ UserService.java
   │     │  ├─ UserStatusService.java
   │     │  └─ basic
   │     └─ run
   │        ├─ JavaApplicationBasic.java
   │        └─ JavaApplicationLegacy.java
   └─ resources
      └─ application.yaml
```
