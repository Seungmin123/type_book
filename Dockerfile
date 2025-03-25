FROM openjdk:11-jdk-slim

WORKDIR /app

ARG JAR_FILE=build/libs/kit-page-0.0.1-SNAPSHOT-boot.jar
COPY ${JAR_FILE} app.jar

# spring.profiles.active 외부에서 받을 수 있도록 환경변수 사용
ENV SPRING_PROFILES_ACTIVE=prod
ENV VERSION=0.0.1
ENV JAVAOPTS="\
   -Xms2048M -Xmx2048M \
   -XX:+UseG1GC \
   -XX:+UseStringDeduplication \
   -XX:+HeapDumpOnOutOfMemoryError \
   -XX:HeapDumpPath=/app/dump \
   -Dlog4j2.formatMsgNoLookups=true \
   -Dfile.encoding=UTF-8 \
   -Duser.timezone=Asia/Seoul \
   -DLOG_HOME=/app/logs \
   -javaagent:/opt/datadog/dd-java-agent.jar \
   -Ddd.logs.injection=true \
   -Ddd.service=KiT_Page \
   -Ddd.env=$SPRING_PROFILES_ACTIVE \
   -Ddd.version=$VERSION \
   -Ddd.profiling.enabled=true"

ENTRYPOINT sh -c 'mkdir -p /app/dump && mkdir -p /app/logs && java $JAVA_OPTS -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE'