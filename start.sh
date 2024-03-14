#!/bin/bash

HOME=/opt/emailTelegramAlertBot_v2/
PWD=$HOME

. "$HOME/venv/bin/activate"
python -m pip install -r $HOME/pythonMailModule/requirements.txt

#JMX_EXP_PORT=7071
#JMX_EXP_COMMAND=-javaagent:$JMX_EXP_PATH=$JMX_EXP_PORT:$JMX_EXP_CONFIG
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=true"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.access.file=$JMX_ACCESS_FILE"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.password.file=$JMX_PASSWORD_FILE"

sh -c "cd $HOME && nohup java -Dspring.config.location=$HOME/application.properties -jar "$HOME/TelegramAlertBot.jar"  <&-  > logs/logBot.log &"
