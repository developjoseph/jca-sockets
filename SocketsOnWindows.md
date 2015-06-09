A socket is identified by a pair of addresses (IP + port). For example if I am connecting from 192.168.1.1 to 10.10.10.1 listening on port 9000, then my Socket will be
{192.168.1.1:SOME\_PORT, 10.10.10.1:9000}. The SOME\_PORT is allocated dynamically by the client (these are sometimes called ephemeral ports).

When a client socket is closed it goes into a TIME\_WAIT state. The local port cannot be reused before the TIME\_WAIT has expired.

This is not normally a problem because there are usually many local ports available on UNIX systems but on Windows it may be an issue if you are opening and closing many connections. Normally you should not use one connection per request/response as it is not very efficient (HTTP 1.0 did this but now HTTP 1.1 keeps the connection open for more than one request/response).

I ran into the problem when I was load testing the Resource Adapter, there are two "solutions" which may be used together.

### Reducing TIME\_WAIT ###
The length of the TIME\_WAIT in seconds set by **adding** a DWORD to the registry `HKLM\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters\TcpTimedWaitDelay` (default is 240 seconds, minimum is 30 seconds).

See [Microsoft Document on setting TcpTimedWaitDelay](http://technet.microsoft.com/en-us/library/cc938217.aspx) for details.

### Increasing available ports ###
Additionally you may run out of local sockets and you can increase these again by **adding** a DWORD to the registry `HKLM\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters\MaxUserPort` (default is 5000, maximum is 65536).

See [Microsoft Document on setting MaxUserPort](http://technet.microsoft.com/en-us/library/cc938196.aspx) for details.