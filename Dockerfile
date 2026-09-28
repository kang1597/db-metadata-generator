# ===== 1단계: 빌드 =====
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle 관련 파일 먼저 복사 (의존성 캐싱 목적)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew

# 의존성만 먼저 내려받기
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 후 빌드 (테스트는 CI에서 별도로 실행하므로 여기선 제외)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ===== 2단계: 실행 =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드 단계에서 만들어진 jar 파일만 가져옴
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]