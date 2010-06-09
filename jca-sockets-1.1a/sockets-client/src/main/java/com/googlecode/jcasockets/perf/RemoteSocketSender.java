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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class RemoteSocketSender implements SocketSender, SocketSenderFactory {
	

	private long startTimeMillis;
	private Socket socket;
	private Integer port;
	private String ipAddress;
	private SocketAddress socketAddress;

	public RemoteSocketSender() {
	}

	public RemoteSocketSender(String ipAddress, Integer port) {
		this.ipAddress = ipAddress;
		this.port = port;
		startTimeMillis = System.currentTimeMillis();
	}

	public String send(String sendMessage) {
		socket = new Socket();
		socketAddress = new InetSocketAddress(ipAddress, port);
		StringBuilder sb = new StringBuilder(sendMessage.length());
		OutputStream outputStream;
		int timeoutMs = 5000;
		try {
			socket.connect(socketAddress, timeoutMs);
			outputStream = socket.getOutputStream();
			outputStream.write(sendMessage.getBytes());
			outputStream.flush();
			socket.shutdownOutput();

			final InputStream inputStream = socket.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

			String str;
			while ((str = rd.readLine()) != null) {
				sb.append(str);
			}
			rd.close();
		} catch (IOException e) {
			String out = "Thread " + Thread.currentThread().getName() +  " exception while sending: " + ipAddress + ":" + port + " after " + (System.currentTimeMillis() - startTimeMillis) + "ms";
			System.out.println(out);
			throw new RuntimeException( out, e );
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(
						"Thread " + Thread.currentThread().getName() +  " exception while closing: " + ipAddress + ":" + port + " after " + (System.currentTimeMillis() - startTimeMillis) + "ms", e );
			}
		}
		
		return sb.toString();
	}

	public SocketSender createSocketSender(String remoteIpAddress, Integer remotePort) {
		return new RemoteSocketSender(remoteIpAddress, remotePort);
	}

}
