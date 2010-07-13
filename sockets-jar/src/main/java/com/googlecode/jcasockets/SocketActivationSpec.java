/*
 * Copyright 2009 Mark Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jcasockets;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;


public class SocketActivationSpec implements ActivationSpec, SocketResourceAdapterConfiguration, Serializable {
	private final Logger logger = Logger.getLogger(SocketActivationSpec.class.getName());
	
	private int port;
	private String encoding;
	private int maximumConnections;
	private int connectionTimeoutMilliseconds;

	private SocketResourceAdapter ra = null;

	private String ipAddress;

	public SocketActivationSpec() {
		logger.info("Creating Activation spec: " + this);
	}

	public ResourceAdapter getResourceAdapter() {
		return ra;
	}

	public void setResourceAdapter(ResourceAdapter resourceAdapter)
			throws ResourceException {
		ra = (SocketResourceAdapter) resourceAdapter;
	}

	public void validate() throws InvalidPropertyException {
	    if ( encoding == null ){
	    	return;
	    }
	    Map<String,Charset> availableCharsets = Charset.availableCharsets( );
	    if (  !availableCharsets.containsKey( encoding ) ){
	    	throw new InvalidPropertyException( "Encoding " + encoding + " is unknown. It should be one of: " + availableCharsets.keySet( ) );
	    }
	}

	boolean accepts(String recipientAddress) throws InvalidPropertyException {
		return true; // accept anything
	}

	public void setIpAddress(String ipAddress) {
		logger.fine("Setting ipAddress: " + ipAddress);
		this.ipAddress = ipAddress;
	}
	public void setPort(int port) {
		logger.fine("Setting port: " + port);
		this.port = port;
	}
	public void setEncoding(String encoding) {
		logger.fine("Setting encoding: " + encoding);
		this.encoding = encoding;
	}

	/* Different server behaviour. This is for JBoss. */
	public void setConnectionTimeoutMilliseconds(Integer connectionTimeoutMilliseconds) {
		doSetConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
	}

	/* Different server behaviour. This is for Glassfish */
	public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
		doSetConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
	}

	/* Different server behaviour. This is for JBoss. */
	public void setMaximumConnections(Integer maximumConnections) {
		doSetMaximumConnections(maximumConnections);
	}

	/* Different server behaviour. This is for Glassfish */
	public void setMaximumConnections(int maximumConnections) {
		doSetMaximumConnections(maximumConnections);
	}

	private void doSetConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
		logger.fine("Setting connection timeout milliseconds: " + connectionTimeoutMilliseconds);
		this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
	}

	private void doSetMaximumConnections(int maximumConnections) {
		logger.fine("Setting maximum connections: " + maximumConnections);
		this.maximumConnections = maximumConnections;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	public int getMaximumConnections() {
		return maximumConnections;
	}

	public int getConnectionTimeoutMilliseconds() {
		return connectionTimeoutMilliseconds;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + " ipAddress=" + ipAddress +  ",port=" + port + ",encoding=" + encoding 
		+ ",maximumConnections=" + maximumConnections 
		+ ",connectionTimeoutMilliseconds=" + connectionTimeoutMilliseconds 
		+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocketActivationSpec other = (SocketActivationSpec) obj;
		if (port != other.port)
			return false;
		return true;
	}


}
