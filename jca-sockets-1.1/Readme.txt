These are some basic instructions to get a knowledgeable person started.

After unzipping, deploy the socket-rar.rar file to the application server.

An example MDB is also included in sockets-ejb.jar, it is only currently tested for JBoss 
(for other servers you may need to include or fix the included server specific XML descriptor like jboss.xml).
The MDB just replies with what was sent. 

Also a simple client (is provided which will send a request from the command line and get and echo a response from the server).
You can run the client with:

java -cp bin/sockets-client-alpha.0.2.jar com.googlecode.jcasockets.sample.SimpleClient "Hello World"

The reply for server should be:
"Hello World"

A more sophisticated client is also provided, for this you need some external jars (commons-logging, commons-cli)
which are not yet packaged in this distribution.

java -cp bin/sockets-client-alpha.0.2.jar com.googlecode.jcasockets.perf.Client -h

