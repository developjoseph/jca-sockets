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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
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
		endpointPool = new EndpointPool(messageEndpointFactory, 15, 10, TimeUnit.SECONDS);
	}

	public void start() throws ResourceAdapterInternalException {
		log.info("Start listening on port " + activationSpec.getPort());
		try {
			serverSocket = new ServerSocket(activationSpec.getPort());
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
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		setRunning(true);
		try {
			while (isRunning()) {
				final Socket socket = serverSocket.accept();
				SocketMessage socketMessage = new SocketMessage(socket, activationSpec.getEncoding());

				SocketMessageEndpoint messageEndpoint = endpointPool.getEndpoint();

				SocketProcessor socketProcessor = new SocketProcessor(socketMessage, messageEndpoint);
				workManager.scheduleWork(socketProcessor, WorkManager.IMMEDIATE, null, endpointPool);
			}
		} catch (Exception e) {
			if (isRunning()) {
				log.error("Error while accepting a socket request an scheduling work on the request", e);
			}
			setRunning(false);
		}
	}

}
