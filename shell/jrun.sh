#!/bin/bash

dir=`dirname $0`

RAPID_QUEUE_HOME=../${dir}

echo "RAPID_QUEUE_HOME=${RAPID_QUEUE_HOME}"

pidfile=${RAPID_QUEUE_HOME}/pid
cd ${RAPID_QUEUE_HOME} 

CP=.:config/${DWENV}
for file in lib/*;
do
        CP=${CP}:$file;
done


java -cp ${CP} $1 $2 $3 $4 $5 $6

