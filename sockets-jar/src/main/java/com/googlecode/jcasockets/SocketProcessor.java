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

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import javax.resource.spi.work.Work;

public class SocketProcessor implements Work {
	private final Logger logger = Logger.getLogger(SocketProcessor.class.getName());

	private SocketMessage socketMessage;
	private final SocketMessageEndpoint messageEndpoint;

	public SocketProcessor(SocketMessage socketMessage, SocketMessageEndpoint messageEndpoint) {
		this.socketMessage = socketMessage;
		this.messageEndpoint = messageEndpoint;
	}

	public SocketMessageEndpoint getMessageEndpoint() {
		return messageEndpoint;
	}

	public void release() {
		closeSocket();
	}

	public void run() {
		try {
			logger.fine("Executing the onMessage(socketMessage) method");
			messageEndpoint.onMessage(socketMessage);
		} catch (Exception e) {
			logger.log( FINE,  "Exception on execution of MDB, processing has probably failed", e);
		} finally {
			closeSocket();
		}
	}

	private void closeSocket() {
		try {
			Socket socket = socketMessage.getRawSocket();
			if ( !socket.isClosed()){
				socket.close();
			}
		} catch (final IOException e) {
			logger.log( SEVERE, "Exception on close of socketMessage, processing may have failed", e);
		}
	}

}
