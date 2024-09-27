#!/bin/bash

VERSION=0.001.365
IDLE_PROFILE=prod
IDLE_PORT=8080
################IDLE_APPLICATION_PATH=/home/ec2-user/KitPlayer-Muzlive-0.0.1-SNAPSHOT.jar

cd /home/ec2-user/nonstop/jar
echo "> build 설정" >> /home/ec2-user/nonstop/logs/deploy.log
BUILD_JAR=$(ls *-boot.jar)
JAR_NAME=$(basename $BUILD_JAR)
DEPLOY_PATH=/home/ec2-user/nonstop/jar

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/nonstop/logs/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ec2-user/nonstop/logs/deploy.log
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi
DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME
echo "> DEPLOY_JAR 배포"    >> /home/ec2-user/nonstop/logs/deploy.log
##############################################################nohup java -jar $DEPLOY_JAR >> /home/ec2-user/deploy.log 2>/home/ec2-user/deploy_err.log &
nohup java -Dlog4j2.formatMsgNoLookups=true -Xms1024M -Xmx1024M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/ec2-user/dump -Duser.timezone=Asia/Seoul -DLOG_HOME=/home/nonstop/logs -Dspring.profiles.active=$IDLE_PROFILE -jar $DEPLOY_JAR > /dev/null 2>&1 &
#nohup java -Dlog4j2.formatMsgNoLookups=true -Dspring.profiles.active=$IDLE_PROFILE -jar $DEPLOY_JAR 1> /dev/null 2>&1 &

echo "> $IDLE_PROFILE 10초 후 Health check 시작"
echo "> curl -s http://127.0.0.1:$IDLE_PORT/actuator/health "
sleep 10

for retry_count in {1..10}
do
  response=$(curl -s http://127.0.0.1:$IDLE_PORT/actuator/health)
  up_count=$(echo $response | grep 'UP' | wc -l)

  if [ $up_count -ge 1 ]
  then # $up_count >= 1 ("UP" 문자열이 있는지 검증)
      echo "> Health check 성공"
      break
  else
      echo "> Health check의 응답을 알 수 없거나 혹은 status가 UP이 아닙니다."
      echo "> Health check: ${response}"
  fi

  if [ $retry_count -eq 10 ]
  then
    echo "> Health check 실패. "
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done
