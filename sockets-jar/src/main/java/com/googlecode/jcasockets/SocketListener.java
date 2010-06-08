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

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

public class SocketListener implements Runnable, Work {

	private ServerSocket serverSocket;
	private final WorkManager workManager;
	private final SocketActivationSpec activationSpec;
	private final Logger logger = Logger.getLogger(SocketListener.class.getName());

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private EndpointPool endpointPool;

	final boolean isRunning() {
		return isRunning.get();
	}

	final void setRunning(Boolean running) {
		isRunning.set(running);
	}

	public SocketListener(WorkManager workManager, SocketActivationSpec activationSpec,	MessageEndpointFactory messageEndpointFactory) {
		this.workManager = workManager;
		this.activationSpec = activationSpec;
		logger.info("Activating socket listener with: " + activationSpec);
	    int maximumConnections = activationSpec.getMaximumConnections();
	    int connectionTimeoutMilliseconds = activationSpec.getConnectionTimeoutMilliseconds();
		endpointPool = new EndpointPool(messageEndpointFactory, maximumConnections, connectionTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	}

	public void start() throws ResourceAdapterInternalException {
		logger.info("Start listening on port " + activationSpec.getPort());
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind( new InetSocketAddress( activationSpec.getPort() ));
			workManager.startWork(this);
		} catch (Throwable e) {
			logger.log( SEVERE, "Exception while starting resource adapter, rethrowing with ResourceAdapterInternalException", e);
			throw new ResourceAdapterInternalException(e);
		}
	}

	public void release() {
		logger.info("Stop listening on port " + activationSpec.getPort());
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
				logger.log( SEVERE, "Error while accepting a socket request an scheduling work on the request", e);
			}
			setRunning(false);
		}
	}

	private void waitForRequestAndProcess() throws IOException, UnavailableException, WorkException {
		final Socket socket = serverSocket.accept();
		try{
			SocketMessage socketMessage = new SocketMessageImpl(socket, activationSpec.getEncoding());
			SocketMessageEndpoint messageEndpoint = endpointPool.getEndpoint();
			
			SocketProcessor socketProcessor = new SocketProcessor(socketMessage, messageEndpoint);
			startSocketProcessingWorkImmediately(socketProcessor);
		}catch ( UnavailableException e){
			logger.log( SEVERE, "No more endpoints avaiable in the pool", e);
		}
	}

	private void startSocketProcessingWorkImmediately(SocketProcessor socketProcessor) throws WorkException {
		ExecutionContext executionContext = null;
		workManager.scheduleWork(socketProcessor, WorkManager.IMMEDIATE, executionContext, endpointPool);
	}

	public void dumpSocket(Socket socket) {
		try {
			logger.info("getKeepAlive         " + socket.getKeepAlive());
			logger.info("getLocalPort         " + socket.getLocalPort());
			logger.info("getOOBInline         " + socket.getOOBInline());
			logger.info("getReceiveBufferSize " + socket.getReceiveBufferSize());
			logger.info("getReuseAddress      " + socket.getReuseAddress());
			logger.info("getSendBufferSize    " + socket.getSendBufferSize());
			logger.info("getSoLinger          " + socket.getSoLinger());
			logger.info("getSoTimeout         " + socket.getSoTimeout());
			logger.info("getTcpNoDelay        " + socket.getTcpNoDelay());
			logger.info("getTrafficClass      " + socket.getTrafficClass());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
