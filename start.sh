#!/bin/bash

HOME=/opt/emailTelegramAlertBot
PWD=$HOME

. "$HOME/venv/bin/activate"

JMX_EXP_PORT=7071
JMX_EXP_COMMAND=-javaagent:$JMX_EXP_PATH=$JMX_EXP_PORT:$JMX_EXP_CONFIG
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=true"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.access.file=$JMX_ACCESS_FILE"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.password.file=$JMX_PASSWORD_FILE"

cd $HOME & nohup java -Dspring.config.location=$HOME/application.properties

sh -c "cd $HOME && nohup java $JAVA_OPTS $JMX_EXP_COMMAND -jar "$HOME/emailTelegramBot.jar"  <&-  > logBot.log &"
