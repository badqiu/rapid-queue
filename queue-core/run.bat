@echo off
setLocal EnableDelayedExpansion
set CLASSPATH=.;config/
for  %%a in (lib/*.jar) do (
   set CLASSPATH=!CLASSPATH!;lib/%%a
)
set CLASSPATH=!CLASSPATH!
rem echo %CLASSPATH%
java -classpath %CLASSPATH% com.google.code.fqueue.memcached.StartServer