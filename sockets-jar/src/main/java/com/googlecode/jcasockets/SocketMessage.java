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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class SocketMessage {
	private final Socket rawSocket;
	private final String encoding;
	SocketMessage(Socket rawSocket, String encoding) {
		this.rawSocket = rawSocket;
		this.encoding = encoding;
	}

	public Socket getRawSocket() {
		return rawSocket;
	}
	public String getEncoding() {
		return encoding;
	}
	public InputStream getInputStream() throws IOException {
		return rawSocket.getInputStream();
	}
	public OutputStream getOutputStream() throws IOException {
		return rawSocket.getOutputStream();
	}
	public Reader getReader(){
		try {
			return new InputStreamReader( rawSocket.getInputStream(), encoding );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public Writer getWriter(){
		try {
			return new OutputStreamWriter( rawSocket.getOutputStream(), encoding );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
