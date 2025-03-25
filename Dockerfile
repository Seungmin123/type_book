FROM openjdk:11-jdk-slim

WORKDIR /app

ARG JAR_FILE=build/libs/kit-page-0.0.1-SNAPSHOT-boot.jar
COPY ${JAR_FILE} app.jar

# spring.profiles.active 외부에서 받을 수 있도록 환경변수 사용
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]