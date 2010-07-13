call mvn -Dmaven.test.skip=true clean package
if ERRORLEVEL 1 goto error

set sockets_build=E:\workspaces\jca-sockets\jcasockets
set deploy=F:\as\jboss-4.2.2.GA\server\default\deploy

rem call gf2 stop
if ERRORLEVEL 1 goto error

copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar %deploy%
copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar %deploy%
rem del F:\as\glassfish\domains\domain1\logs\server.log
rem call gf2 start

if ERRORLEVEL 1 goto error
goto end

:error
echo error....

:end