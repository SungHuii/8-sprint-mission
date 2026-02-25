# 1. Build Stage (빌드 환경)
# 베이스 이미지
FROM amazoncorretto:17 AS builder

# Workdir 설정
WORKDIR /app

# Gradle 캐시 활용을 위해서 의존성 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (캐시 활용)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build -x test --no-daemon

# 2. Runtime Stage (실행 환경)
# 베이스 이미지
FROM amazoncorretto:17-alpine-jdk

# Workdir 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경 변수 설정
ENV JVM_OPTS=""

# 포트 노출
EXPOSE 80

# 실행 명령어 (JAR 파일명을 app.jar로 통일)
CMD java $JVM_OPTS -jar app.jar