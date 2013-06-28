#!/bin/bash

war=$1
java_main_class=$2
war_name=`basename ${war}  .war`
dir_webapp=/data/webapps/${war_name}/

echo "unzip $war -d ${dir_webapp}"

unzip -o $war -d ${dir_webapp}

function webapp_java_run() {
	cd ${dir_webapp}/WEB-INF/

	CP=classes
	for file in lib/*;
	do
		CP=${CP}:$file;
	done
	java -DwebappDir=${dir_webapp} -cp ${CP} $java_main_class $4 $5 $6 
}


webapp_java_run


