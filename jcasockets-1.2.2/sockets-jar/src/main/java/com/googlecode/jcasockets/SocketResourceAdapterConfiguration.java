package com.googlecode.jcasockets;

public interface SocketResourceAdapterConfiguration {

	void setEncoding(String encoding);
	
	void setIpAddress( String ipAddress );

	/* Different server behaviour. This is for Glassfish */
	void setMaximumConnections(int maximumConnections);

	/* Different server behaviour. This is for JBoss. */
	void setMaximumConnections(Integer maximumConnections);

	/* Different server behaviour. This is for Glassfish */
	void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds);
	
	/* Different server behaviour. This is for JBoss. */
	void setConnectionTimeoutMilliseconds(Integer connectionTimeoutMilliseconds);
	
}