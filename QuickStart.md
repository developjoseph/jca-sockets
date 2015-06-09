See also FrequentlyAskedQuestions.

These are some basic instructions to get a knowledgeable person started.

## unzip and deploy ##
  * Download the zip file from the home page and unzip.
  * Add the jar file `socket-api-X.X.X.jar` to the server classpath.
  * deploy the `socket-rar-X.X.X.rar` file to the application server.
  * deploy the example MDB `sockets-ejb-X-X-X.jar`. For Glassfish and JBoss this should work but for other servers you may need to change the descriptors in the ejb module for the application server you are using). If some need changes to work correctly please email the changes and I will include them (or let me know that the descriptor works).


## Run the simple client ##
The sample MDB just replies with what was sent.
A simple client is provided which will send a request from the command line and get and echo a response from the server.

You can run the client with:
`java -cp sockets-client\sockets-client-X.X.X.jar com.googlecode.jcasockets.sample.SimpleClient "Hello World" `

The reply for server should be:
"Hello World"

## Run the performance testing client ##
A more sophisticated client is also provided, for this you need some external jars (commons-cli) in the classpath which are not packaged in this distribution.

This is the command line required for help:
`java -cp sockets-client\sockets-client-1.2.jar:commons-cli.jar com.googlecode.jcasockets.perf.Client -h `

## Notes for Windows Users ##
A socket is identified by a pair of addresses (IP + port). For example if I am connecting from 192.168.1.1 to 10.10.10.1 listening on port 9000, then my Socket will be
{192.168.1.1:SOME\_PORT, 10.10.10.1:9000}. The SOME\_PORT is allocated dynamically by the client (these are sometimes called ephemeral ports).

When a client socket is closed it goes into a TIME\_WAIT state. The local port cannot be reused before the TIME\_WAIT has expired.

This is not normally a problem because there are usually many local ports avaiable on UNIX systems but on Windows it may be an issue if you are opening and closing many connections. Normally you should not use one connection per request/response as it is not very efficient (HTTP 1.0 did this but now HTTP 1.1 keeps the connection open for more than one request/response).

I ran into the problem when I was load testing the Resource Adapter, there are two "solutions" which may be used together.

### Reducing TIME\_WAIT ###
The length of the TIME\_WAIT in seconds set by **adding** a DWORD to the registry `HKLM\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters\TcpTimedWaitDelay` (default is 240 seconds, minimum is 30 seconds).

See [Microsoft Document on setting TcpTimedWaitDelay](http://technet.microsoft.com/en-us/library/cc938217.aspx) for details.

### Increasing available ports ###
Additionally you may run out of local sockets and you can increase these again by **adding** a DWORD to the registry `HKLM\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters\MaxUserPort` (default is 5000, maximum is 65536).

See [Microsoft Document on setting MaxUserPort](http://technet.microsoft.com/en-us/library/cc938196.aspx) for details.