#!/bin/sh
dir=`dirname $0`

RAPID_QUEUE_HOME=../${dir}

${RAPID_QUEUE_HOME}/shell/webapp_run.sh ${RAPID_QUEUE_HOME}/web/rapid-queue-web-admin.war com.google.code.rapid.queue.jetty.JettyServer

