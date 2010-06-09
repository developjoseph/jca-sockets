call mvn -Dmaven.test.skip=true clean package
set sockets_build=E:\workspaces\jca-sockets\jcasockets
rem copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar f:\as\jboss-4.2.2.GA\server\default\deploy\sockets-rar.rar
rem copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar f:\as\jboss-4.2.2.GA\server\default\deploy

rem copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar f:\as\jboss-4.0.3SP1\server\default\deploy\sockets-rar.rar
rem copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar f:\as\jboss-4.0.3SP1\server\default\deploy

rem copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar f:\as\jboss-5.1.0.CR1\server\default\deploy\sockets-rar.rar
rem copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar f:\as\jboss-5.1.0.CR1\server\default\deploy


rem copy /y %sockets_build%\sockets-rar\target\sockets-rar-*.rar F:\as\glassfishv3-prelude\glassfish\domains\domain1\autodeploy\sockets-rar.rar
rem copy /y %sockets_build%\sockets-ejb\target\sockets-ejb-*.jar F:\as\glassfishv3-prelude\glassfish\domains\domain1\autodeploy

rem copy /y sockets-rar\target\sockets-rar-*.rar F:\as\glassfish\domains\domain1\autodeploy\sockets-rar.rar
rem copy /y sockets-ejb\target\sockets-ejb-*.jar F:\as\glassfish\domains\domain1\autodeploy

rem F:\as\glassfish21
rem F:\as\bea\wlserver_10.3
rem F:\as\Websphere2.1