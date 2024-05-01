REPOSITORY=/home/ubuntu/app
cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/build/libs | grep 'SNAPSHOT.jar' | tail -n 1)
echo "$JAR_NAME"
JAR_PATH=$REPOSITORY/$JAR_NAME
echo "$JAR_PATH"

CURRENT_PID=$(pgrep -f jar)

if [ -z $CURRENT_PID ]
then
  echo "> 실행 중인 jar 파일이 존재하지 않습니다"
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> jar 파일을 실행합니다"
nohup java -jar -Duser.timezone=Asia/Seoul build/libs/$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &