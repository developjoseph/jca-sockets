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
 *	Makes a connection for each request. This is not really scalable and if you have a 
 *  high rate of requests on windows the connections can stall (no new connections accepted)    
 */
public class RemoteSocketSenderConnectionPerRequest extends AbstractRemoteSocketSender implements SocketSender, SocketSenderFactory {

	public RemoteSocketSenderConnectionPerRequest() {
		super();
	}

	public RemoteSocketSenderConnectionPerRequest(String ipAddress, Integer port) {
		super( ipAddress, port );
	}

	public String send(String sendMessage) {
		bindSocket();
		String response = null;
		try {
			int timeoutMs = 0;
			connectSocketWithTimeout(timeoutMs);
			writeMessage(sendMessage);
			shutdownOutput();
			response =  readResponse();
		} catch (IOException e) {
			recordAndThrowException(e);
		}finally{
			closeSocketExceptionOnError();
		}
		
		return response;
	}

	public SocketSender createSocketSender(String remoteIpAddress, Integer remotePort) {
		return new RemoteSocketSenderConnectionPerRequest(remoteIpAddress, remotePort);
	}

}
