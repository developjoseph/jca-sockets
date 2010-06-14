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
package com.googlecode.jcasockets.perf;

import java.io.IOException;

/**
 *	keeps the connection for multiple requests.     
 */
public class RemoteSocketSenderKeepConnection extends AbstractRemoteSocketSender implements SocketSender, SocketSenderFactory {

	public RemoteSocketSenderKeepConnection() {
		super();
	}

	public RemoteSocketSenderKeepConnection(String ipAddress, Integer port) throws Exception {
		super( ipAddress, port );
		bindSocket();
		int timeoutMs = 100;
		connectSocketWithTimeout(timeoutMs);
	}

	public String send(String sendMessage) {
		String response = null;
		try {
			writeMessage(sendMessage);
			response =  readResponse();
		} catch (IOException e) {
			recordAndThrowException(e);
		}
		return response;
	}

	public SocketSender createSocketSender(String remoteIpAddress, Integer remotePort) {
		try {
			return new RemoteSocketSenderKeepConnection(remoteIpAddress, remotePort);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
