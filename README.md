# [SB] 스프린트 미션 2
### Java 기반 콘솔 애플리케이션입니다.
### 1차 JCF -> 2차 File IO -> Repository + Service

## 목표
- Git과 GitHub을 통해 프로젝트를 관리할 수 있다.
- 채팅 서비스의 도메인 모델을 설계하고, Java로 구현할 수 있다.
- 인터페이스를 설계하고 구현체를 구현할 수 있다.
- 싱글톤 패턴을 구현할 수 있다.
- Java Collections Framework에 데이터를 생성/수정/삭제할 수 있다.
- Stream API를 통해 JCF의 데이터를 조회할 수 있다.

## 프로젝트 마일스톤
- 프로젝트 초기화 (Java, Gradle)
- 도메인 모델 구현
- 서비스 인터페이스 설계 및 구현체 구현
  - 각 도메인 모델별 CRUD
  - JCFx메모리 기반
- 의존성 주입

## 프로젝트 구조
```
src
└── main
└── java
└── com.sprint.mission.discodeit
├── entity
│   ├── Channel.java
│   ├── Message.java
│   └── User.java
│
├── repository
│   ├── ChannelRepository.java
│   ├── MessageRepository.java
│   └── UserRepository.java
│
│   ├── file
│   │   ├── FileChannelRepository.java
│   │   ├── FileMessageRepository.java
│   │   └── FileUserRepository.java
│   │
│   └── jcf
│       ├── JCFChannelRepository.java
│       ├── JCFMessageRepository.java
│       └── JCFUserRepository.java
│
├── service
│   ├── ChannelService.java
│   ├── MessageService.java
│   └── UserService.java
│
│   ├── basic
│   │   ├── BasicChannelService.java
│   │   ├── BasicMessageService.java
│   │   └── BasicUserService.java
│   │
│   ├── file
│   │   ├── FileChannelService.java
│   │   ├── FileMessageService.java
│   │   └── FileUserService.java
│   │
│   └── jcf
│       ├── JCFChannelService.java
│       ├── JCFMessageService.java
│       └── JCFUserService.java
│
└── run
├── JavaApplicationBasic.java
└── JavaApplicationLegacy.java
```

