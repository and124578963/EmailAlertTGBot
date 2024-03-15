#!/bin/bash

HOME=/opt/emailTelegramAlertBot_v2/

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.22.0.7-1.el7_9.x86_64/
java_exec=$JAVA_HOME/bin/java


. "$HOME/venv/bin/activate"
pip install --upgrade pip
python -m pip install -r $HOME/pythonMailModule/requirements.txt


set -a
. $HOME/.env
set +a

#JMX_EXP_PORT=7071
#JMX_EXP_COMMAND=-javaagent:$JMX_EXP_PATH=$JMX_EXP_PORT:$JMX_EXP_CONFIG
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=true"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.access.file=$JMX_ACCESS_FILE"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.password.file=$JMX_PASSWORD_FILE"
PWD=$HOME
sh -c "cd $HOME && nohup $java_exec -Dspring.config.location=$HOME/application.properties -jar "$HOME/TelegramAlertBot.jar"  <&-  > logs/logBot.log &"
