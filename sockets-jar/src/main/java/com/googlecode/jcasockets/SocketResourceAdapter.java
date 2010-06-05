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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketResourceAdapter implements ResourceAdapter{
	private static ConcurrentMap<ActivationSpec, SocketListener> socketListeners = new ConcurrentHashMap<ActivationSpec, SocketListener>();

	private WorkManager workManager;
	private final Log log = LogFactory.getLog(SocketResourceAdapter.class);
	private String defaultEncoding;
	private Integer defaultMaximumConnections;
	private Integer defaultConnectionTimeoutMilliseconds;
	

	public SocketResourceAdapter() {
	}

	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		log.info("start");
    	workManager = ctx.getWorkManager();
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.start();
		}
	}

	public void stop() {
		log.info("stop");
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.release();
		}
	}

	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec activationSpec)
			throws  ResourceException {
		log.info("endpointActivation");

		if (!(activationSpec instanceof SocketActivationSpec)) {
			throw new NotSupportedException("Invalid spec" + activationSpec);
		}
		SocketActivationSpec socketActivationSpec = (SocketActivationSpec) activationSpec;
		if ( socketActivationSpec.getEncoding() == null){
			socketActivationSpec.setEncoding(defaultEncoding);
		}
		if ( socketActivationSpec.getMaximumConnections() <= 0){
			socketActivationSpec.setMaximumConnections(defaultMaximumConnections);
		}
		if ( socketActivationSpec.getConnectionTimeoutMilliseconds() <= 0){
			socketActivationSpec.setConnectionTimeoutMilliseconds(defaultConnectionTimeoutMilliseconds);
		}
		SocketListener socketListener = new SocketListener(workManager, socketActivationSpec, endpointFactory);
		socketListeners.put(activationSpec, socketListener);
		try {
			socketListener.start();
		} catch (ResourceException e) {
			socketListener.release();
			throw e;
		}
	}


	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		stop();
		log.info("endpointDeactivation");
		// nothing to do.
	}

	public XAResource[] getXAResources(ActivationSpec[] arg0) throws ResourceException {
		return new XAResource[0]; // XA is unsupported
	}

	public String getEncoding() {
		return defaultEncoding;
	}

	public void setEncoding(String defaultEncoding) {
		log.info("Setting default encoding: " + defaultEncoding);
		this.defaultEncoding = defaultEncoding;
	}

	public int getMaximumConnections() {
		return defaultMaximumConnections;
	}

	public void setMaximumConnections(Integer defaultMaximumConnections) {
		log.info("Setting default maximumConnections: " + defaultMaximumConnections);
		this.defaultMaximumConnections = defaultMaximumConnections;
	}

	public int getConnectionTimeoutMilliseconds() {
		return defaultConnectionTimeoutMilliseconds;
	}

	public void setConnectionTimeoutMilliseconds(Integer defaultConnectionTimeoutMilliseconds) {
		log.info("Setting default connectionTimeoutMilliseconds: " + defaultConnectionTimeoutMilliseconds);
		this.defaultConnectionTimeoutMilliseconds = defaultConnectionTimeoutMilliseconds;
	}

}
