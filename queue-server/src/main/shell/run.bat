
setLocal EnableDelayedExpansion

set RAPID_QUEUE_HOME=E:\svnroot\rapid-queue-git\queue-server\target\rapid-queue-server-0.1.0

cd %RAPID_QUEUE_HOME%
set CLASSPATH=.;config/
for  %%a in (lib/*.jar) do (
   set CLASSPATH=!CLASSPATH!;lib/%%a
)

echo "classpath=%CLASSPATH%"
set JVM_OPS=-Xms300M -Xmx1000M -server
java %JVM_OPS% -classpath %CLASSPATH% com.google.code.rapid.queue.server.Server

