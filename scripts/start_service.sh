#!/bin/bash
APP_HOME=/home/ubuntu/mybureau-api
bash "${APP_HOME}/bin/stop_service.sh"
nohup java -Dlogging.config="${APP_HOME}/conf/logback.xml" -jar "${APP_HOME}/bin/mybureau-api-0.0.1-SNAPSHOT.jar" --spring.config.location="${APP_HOME}/conf/application.properties" >/dev/null 2>&1 & echo "$!" > "${APP_HOME}/run/.pid"