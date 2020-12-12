#!/bin/bash
APP_HOME=/home/ubuntu/mybureau-api
PID_FILE="${APP_HOME}/run/.pid"
if [[ -f $PID_FILE ]]; then
        PID=$(cat $PID_FILE)
        PID_IN_PS=$(ps -p $PID | wc -l)
        echo "PID: $PID, PS: $PID_IN_PS"
        if [[ -n "$PID" && $PID_IN_PS == 2 ]]; then
                kill -15 "$PID"
                echo "Process Killed!"
        fi
else
        echo "No PID file found!"
fi