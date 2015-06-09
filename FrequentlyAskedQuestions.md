### Disclaimer ###
I am not a sockets expert. Most of this stuff is from experience and reading things from more knowledgable people. So if you see something that is wrong or misleading please let me know.


### Does the adapter support setting socket properties like keepalive, linger etc ? ###
No. I have considered adding a `LifeCycleListener` interface which would be called at various points in the lifecycle of the `EndPoint`, for example before binding the socket. Keepalive is probably the most useful, linger did not seem to do much in my tests (it seems it has patchy implementation and does not really do what you'd expect).

### Does the adapter support outgoing connections? ###
No.

### What protocol should I use? ###
You are implementing this adapter because you have legacy clients that need to connect to an application server and these clients cannot be changed. If you could change them then I would recommend **not** using this adapter and using an HTTP servlet with an XML request/Response body or a webservice(JAX-RS or JAX-WS) instead.

### How do I implement a protocol? ###
Currently you need to do this in your onMessage() method. Readers/Writers can be accessed from the `SocketMessage`.  I suppose the `onMessage()` method is misnamed it should be something like `onConnectionUntilClose()` or something like that. In the `onMessage()` method you are responsible for dealing with the conection.

Text based protocols are the most common (care needs to be taken with binary protocols if you reconstruct things like Integers as the byte order is machine dependent, google "big endian"). For text-based protocols typically you would use the Reader/Writer in the `SocketMessage` class (configure the character encoding in the XML descriptor). Binary protocols should use the Input/Output streams but don't combine streams with Readers and Writers, they are buffered.

With any protocol there must be some way of breaking up the data into meaningful chunks (parsed into some domain objects for example). Your MDB code should do that and provide the Objects to your application to be processed.


### What happens if I throw an exception from the MDB? ###
Exceptions are caught in the `EndPoint`, the exception is logged, the socket is closed and the endpoint returned to the pool.

### What happens if the client is badly behaved? ###
When finished, a client should tell the server that the conversation is over (for clients that just send a single request/response per connection this is not necessary). The server can then gracefully close the socket and release the endpoint to be reused.
Clients soemtimes do not do this and in the best case the server side will get an indication from network stack that the socket is closed, the server side socket will then throw an exception will behave as describe in the FAQ on exceptions.

### How can I protect the server against malicious attacks? ###
Like anything listening on a port the resource adapter could be attacked. There is nothing to stop this. Protection should be implemented in the infrastructure (the typical use case is to install this on an internal network which should be relativly safe from bad guys).

### What happens in case of network failure? ###
It depends. The server side may not be notified and it will hang indefinitely. If there is some indication of failure by the underlying network stack then an exception will be thrown (see FAQ on exceptions). Keep alives are supposed to handle this but currently there is no mechanisim in the resource adapter to enable them.

### What logger is used? ###
I switched from commons-logging to java.util.logging to remove the dependency. Without configuration java.util.logging goes to stderr. In JBoss stderr just goes to the standard logger. Not sure what other servers will do with this.

### Why is an `EndPoint` pool used? ###
I kept running out of `EndPoints` in JBoss for high volume tests that made a single request/response per connection. It may be a configuration in JBoss and I certainly think an Application server should handle this correctly. If anyone can shed light on this I would be happy to remove the pool. I could also disable it by setting maxConnection to 0 for example, but this is not implemented.

### Is this used in production? ###
Yes, that's why it was developed! It has been used here in Belgium at over 500 Postal Distribution centres for connecting multiplexed barcode scanners with JBoss application servers. It has been pretty reliable with the only issue reported being the limit on the number of connections.

### Where can I learn more about sockets? ###
Google it but here are some useful links:

Really basic stuff: http://java.sun.com/docs/books/tutorial/networking/sockets/index.html

I don't know python but this is pretty easy to follow: http://www.amk.ca/python/howto/sockets/

Some info on linger: http://www.developerweb.net/forum/archive/index.php/t-2982.html

SocketsOnWindows also has some useful info (not just for windows).