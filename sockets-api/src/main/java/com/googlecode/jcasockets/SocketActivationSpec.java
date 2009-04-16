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

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketActivationSpec implements ActivationSpec, Serializable {
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(SocketActivationSpec.class);
	private int port;
	private String encoding;

	private SocketResourceAdapter ra = null;

	public SocketActivationSpec() {
		log.debug("Creating Activation spec: " + this);
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
		return true; // sluttily accept anything
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
