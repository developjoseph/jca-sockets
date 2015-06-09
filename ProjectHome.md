# JCA Sockets #
JCA Sockets is a JCA resource adapter that accepts simple TCP/IP socket connections from clients. It allows a J2EE/JEE developer to support legacy socket clients in an easy, standards compliant way. It does not support distributed transactions, although it is easy to start a transaction in the MDB's onMessage method.

## Usage Summary ##
  * Make sure sockets-api-X.X.jar is in the classpath
  * Write and configure an MDB (a sample EJB is provided - see the sockets-ejb module). Two clients are provided in the sockets-client module.
  * Deploy sockets-rar-X.X.rar to the server.

## JEE Containers Tested ##
See CompatibilityMatrix for more information.

## Performance/Scalability ##
@TODO

## Why ##
Well, I had a problem where a socket client needed to connect to a a JEE server. I've done this before with a simple listener and starting threads in the server (in Weblogic 6) but I wanted to follow a standards oriented approach for a new development.
After much googling I saw that many people wanting the same thing but no one provided the code to do it.
After a bit of research (see links below) I found it was easier than expected and thought that others could benefit as well.
(and there wasn't enough project budget for this...)

So it is packaged as an easy to deploy rar, all that is required is to implement a simple MDB.

## Links ##

  * The best resource is the [JCA Specification 1.5](http://java.sun.com/j2ee/connector/download.html).
  * This is an excellent Article on [Incoming JCA](http://www.theserverside.com/tt/articles/article.tss?l=J2EE1_4) from Wade Poziombka.
  * Also useful is the [JBoss source code](http://www.jboss.org/jbossas/downloads). Choose "Download" and then the source bundle. Look in the _"connector"_ folder.
  * This is an article on  [using JCA to read from  the filesystem ](http://www.jboss.org/file-access/default/members/jbossas/freezone/docs/Server_Configuration_Guide/4/html/An_Overview_of_the_JBossCX_Architecture-A_Sample_Skeleton_JCA_Resource_Adaptor.html).

