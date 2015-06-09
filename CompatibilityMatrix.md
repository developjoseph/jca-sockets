# Introduction #

In theory any modern J2EE 1.4 and later server will work. Currently only the following have been tested.


# Details #
| **Server** | **Version** | **Comments** |
|:-----------|:------------|:-------------|
| JBoss      | 4.03        | OK           |
| JBoss      | 4.2         | OK           |
| JBoss      | 5.0         | OK           |
| Glassfish  | 2.1         | OK           |
| Glassfish  | 3.0         | Not OK - does not deploy|
| Weblogic   | 10.3.1      | OK tested by weberjn |
| Websphere  | X           | Not tested   |
| Geronimo   | X           | Some testing done by weberjn (see issue http://code.google.com/p/jca-sockets/issues/detail?id=13 for details). Note it should no longer be necessary to repackage the jar in the rar file. |