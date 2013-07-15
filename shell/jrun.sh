#!/bin/bash

source /etc/profile
if [ -z "$RAPID_QUEUE_HOME" ]; then
        echo "error on [RAPID_QUEUE_HOME] env is empty"
        exit 1;
fi
echo "RAPID_QUEUE_HOME=${RAPID_QUEUE_HOME}"


pidfile=${RAPID_QUEUE_HOME}/pid
cd ${RAPID_QUEUE_HOME} 

CP=.:config/${DWENV}
for file in lib/*;
do
        CP=${CP}:$file;
done


java -cp ${CP} $1 $2 $3 $4 $5 $6

