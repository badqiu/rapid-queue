#!/bin/sh
ulimit -SHn 51200
dir=`dirname $0`

RAPID_QUEUE_HOME=../${dir}

echo "RAPID_QUEUE_HOME=${RAPID_QUEUE_HOME}"

pidfile=/tmp/rapid_pid
cd ${RAPID_QUEUE_HOME} 

CP=.:config/${DWENV}
for file in lib/*;
do
        CP=${CP}:$file;
done


JVM_OPTS="-Xms2500M -Xmx4000M -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -XX:+HeapDumpOnOutOfMemoryError -ve
rbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps "
#JVM_OPTS="-server -Xpausetarget=100ms -Xverbose:gcpause -Xverbose:gcreport -Xms2000m -Xmx6000m "

retval=0
# start the server
start(){
	sync

        printf 'Starting the server of FQueue\n'
        if [ -f "$pidfile" ] ; then
                pid=`cat "$pidfile"`
        printf 'Existing process: %d\n' "$pid"
                retval=1
        else
                java  ${JVM_OPTS}  -cp $CP com.google.code.rapid.queue.server.Server &
                echo $! >"$pidfile"
                if [ "$?" -eq 0 ] ; then
                        printf 'Done\n'
                else
                        printf 'The server could not started\n'
                        retval=1
                fi
        fi
}

# stop the server
stop(){
  printf 'Stopping the server\n'
  if [ -f "$pidfile" ] ; then
    pid=`cat "$pidfile"`
    printf "Sending the terminal signal to the process: %s\n" "$pid"
    PROCESSPID=`ps -ef|awk  '{print $2}'|grep "$pid"`
    if [[ $PROCESSPID -ne "$pidfile" ]] ; then
        rm -f "$pidfile";
        printf 'Done\n'
    fi
    kill -9 "$pid"
    c=0
    while true ; do
      sleep 0.1
      PROCESSPID=`ps -ef|awk  '{print $2}'|grep "$pid"`
      if [[ $PROCESSPID -eq "$pidfile" ]] ; then
        c=`expr $c + 1`
        if [ "$c" -ge 100 ] ; then
          printf 'Hanging process: %d\n' "$pid"
          retval=1
          break
        fi
      else
        printf 'Done\n'
        rm -f "$pidfile";
        break
      fi
    done
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

