# 베이스 이미지
FROM amazoncorretto:17

# Workdir 설정
WORKDIR /app

# 프로젝트 파일 복사
COPY . .

# Gradle Wrapper 실행 권한 부여 및 빌드 (테스트 제외)
RUN chmod +x gradlew
RUN ./gradlew build -x test

# 환경 변수 설정 (기본값)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 포트 노출
EXPOSE 80

# 실행 명령어
CMD java $JVM_OPTS -jar build/libs/$PROJECT_NAME-$PROJECT_VERSION.jar