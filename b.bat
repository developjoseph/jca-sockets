call mvn -Dmaven.test.skip=true clean package
copy sockets-rar\target\sockets-rar.rar           f:\as\jboss-4.2.2.GA\server\default\deploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar f:\as\jboss-4.2.2.GA\server\default\deploy

copy sockets-rar\target\sockets-rar.rar           f:\as\jboss-4.0.3SP1\server\default\deploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar f:\as\jboss-4.0.3SP1\server\default\deploy

copy sockets-rar\target\sockets-rar.rar           f:\as\jboss-5.0.1.GA\server\default\deploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar f:\as\jboss-5.0.1.GA\server\default\deploy


copy sockets-rar\target\sockets-rar.rar           F:\as\glassfishv3-prelude\glassfish\domains\domain1\autodeploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar F:\as\glassfishv3-prelude\glassfish\domains\domain1\autodeploy

copy sockets-rar\target\sockets-rar.rar           F:\as\glassfish21\domains\domain1\autodeploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar F:\as\glassfish21\domains\domain1\autodeploy

copy sockets-rar\target\sockets-rar.rar           C:\post\apps\pod-v000606-ld-node1\deploy
copy sockets-ejb\target\sockets-ejb-alpha.0.2.jar C:\post\apps\pod-v000606-ld-node1\deploy


rem F:\as\glassfish21
rem F:\as\bea\wlserver_10.3
rem F:\as\Websphere2.1