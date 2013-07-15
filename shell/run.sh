#!/bin/bash

##########################
# env s
#########################
ulimit -SHn 51200

source /etc/profile
if [ -z "$RAPID_QUEUE_HOME" ]; then
        echo "error on [RAPID_QUEUE_HOME] env is empty"
        exit 1;
fi
echo "RAPID_QUEUE_HOME=${RAPID_QUEUE_HOME}"


pidfile="/tmp/rapid_pid"
cd ${RAPID_QUEUE_HOME} 

CLASSPATH=.:config/${DWENV}
for file in lib/*;
do
        CLASSPATH=${CLASSPATH}:$file;
done

JVM_OPTS="-Xms2500M -Xmx4000M -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps "
#JVM_OPTS="-server -Xpausetarget=100ms -Xverbose:gcpause -Xverbose:gcreport -Xms2000m -Xmx6000m "


retval=0
# start the server
start(){
        sync

        printf 'Starting the server of FQueue\n'
        java  ${JVM_OPTS}  -cp $CLASSPATH com.google.code.rapid.queue.server.Server &
        echo $! >"$pidfile"
        if [ "$?" -eq 0 ] ; then
                printf 'Done\n'
        else
                printf 'The server could not started\n'
                retval=1
        fi
}

# stop the server
stop(){
  sync
  printf 'Stopping the server\n'
  if [ -f "$pidfile" ] ; then
    pid=`cat "$pidfile"`
    printf "kill -9 the process: %s\n" "$pid"
    kill -9 "$pid"
  else
    printf 'No process found\n'
    retval=1
  fi

  sync
}

# dispatch the command
case "$1" in
start)
  start
  ;;
stop)
  stop
  ;;
restart)
  stop
  start
  ;;
*)
  printf 'Usage: %s {start|stop|restart}\n'
  exit 1
  ;;
esac


# exit
exit "$retval"

