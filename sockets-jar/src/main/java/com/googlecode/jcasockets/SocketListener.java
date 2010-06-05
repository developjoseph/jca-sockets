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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketListener implements Runnable, Work {

	private ServerSocket serverSocket;
	private final WorkManager workManager;
	private final SocketActivationSpec activationSpec;
	private final Log log = LogFactory.getLog(SocketListener.class);

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private EndpointPool endpointPool;

	final boolean isRunning() {
		return isRunning.get();
	}

	final void setRunning(Boolean running) {
		isRunning.set(running);
	}

	public SocketListener(WorkManager workManager, SocketActivationSpec activationSpec,
			MessageEndpointFactory messageEndpointFactory) {
		this.workManager = workManager;
		this.activationSpec = activationSpec;
	    int maximumConnections = activationSpec.getMaximumConnections();
	    int connectionTimeoutMilliseconds = activationSpec.getConnectionTimeoutMilliseconds();
		endpointPool = new EndpointPool(messageEndpointFactory, maximumConnections, connectionTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	}

	public void start() throws ResourceAdapterInternalException {
		log.info("Start listening on port " + activationSpec.getPort());
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind( new InetSocketAddress( activationSpec.getPort() ));
			workManager.startWork(this);
		} catch (Throwable e) {
			log.error("Exception while starting resource adapter, rethrowing with ResourceAdapterInternalException", e);
			throw new ResourceAdapterInternalException(e);
		}
	}

	public void release() {
		log.info("Stop listening on port " + activationSpec.getPort());
		setRunning(false);
		try {
			if (serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		setRunning(true);
		try {
			while (isRunning()) {
				waitForRequestAndProcess();
			}
		} catch (Exception e) {
			if (isRunning()) {
				log.error("Error while accepting a socket request an scheduling work on the request", e);
			}
			setRunning(false);
		}
	}

	private void waitForRequestAndProcess() throws IOException, UnavailableException, WorkException {
		final Socket socket = serverSocket.accept();
//	experiments			socket.setSoLinger(true, 10);  
//				dumpSocket( socket );
		try{
			SocketMessage socketMessage = new SocketMessageImpl(socket, activationSpec.getEncoding());
			SocketMessageEndpoint messageEndpoint = endpointPool.getEndpoint();
			SocketProcessor socketProcessor = new SocketProcessor(socketMessage, messageEndpoint);
			workManager.scheduleWork(socketProcessor, WorkManager.IMMEDIATE, null, endpointPool);
		}catch ( UnavailableException e){
			log.error( "No more endpoints avaiable ine the pool", e);
		}
	}

	private void dumpSocket(Socket socket) {
		try {
			log.info("getKeepAlive         " + socket.getKeepAlive());
			log.info("getLocalPort         " + socket.getLocalPort());
			log.info("getOOBInline         " + socket.getOOBInline());
			log.info("getReceiveBufferSize " + socket.getReceiveBufferSize());
			log.info("getReuseAddress      " + socket.getReuseAddress());
			log.info("getSendBufferSize    " + socket.getSendBufferSize());
			log.info("getSoLinger          " + socket.getSoLinger());
			log.info("getSoTimeout         " + socket.getSoTimeout());
			log.info("getTcpNoDelay        " + socket.getTcpNoDelay());
			log.info("getTrafficClass      " + socket.getTrafficClass());
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

}
