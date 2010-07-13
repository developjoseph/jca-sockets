call mvn -Dmaven.test.skip=true clean package
if ERRORLEVEL 1 goto error

set sockets_build=E:\workspaces\jca-sockets\jcasockets

call gf2 stop
if ERRORLEVEL 1 goto error

copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar F:\as\glassfish\domains\domain1\autodeploy\sockets-rar.rar
copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar F:\as\glassfish\domains\domain1\autodeploy
del F:\as\glassfish\domains\domain1\logs\server.log
call gf2 start

if ERRORLEVEL 1 goto error
goto end

:error
echo error....

:end