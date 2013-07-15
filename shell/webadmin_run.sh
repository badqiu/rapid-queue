#!/bin/bash

source /etc/profile
if [ -z "$RAPID_QUEUE_HOME" ]; then
        echo "error on [RAPID_QUEUE_HOME] env is empty"
        exit 1;
fi
echo "RAPID_QUEUE_HOME=${RAPID_QUEUE_HOME}"

${RAPID_QUEUE_HOME}/shell/webapp_run.sh ${RAPID_QUEUE_HOME}/web/rapid-queue-web-admin.war com.google.code.rapid.queue.jetty.JettyServer

