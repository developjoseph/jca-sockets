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
import java.net.Socket;

import javax.resource.spi.work.Work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketProcessor implements Work {
	private final Log log = LogFactory.getLog(SocketProcessor.class);

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
			log.debug("Executing the onMessage(socketMessage) method");
			messageEndpoint.onMessage(socketMessage);
		} catch (Exception e) {
			log.error("Exception on execution of MDB, processing has probably failed", e);
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
			log.error("Exception on close of socketMessage, processing may have failed", e);
		}
	}

}
