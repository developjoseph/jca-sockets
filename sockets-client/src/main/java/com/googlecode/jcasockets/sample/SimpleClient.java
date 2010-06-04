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
package com.googlecode.jcasockets.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SimpleClient {

  public static void main(String[] args) throws Exception {
    // Create a mockSocket without a timeout
    final InetAddress address = InetAddress.getByName("localhost");
    final int port = 9000;

    final SocketAddress socketAddress = new InetSocketAddress(address, port);
    final Socket socket = new Socket();
	socket.setSoLinger(false, 0);

    final int timeoutMs = 1; 
    socket.connect(socketAddress, timeoutMs);
    final OutputStream outputStream = socket.getOutputStream();
    outputStream.write(args[0].getBytes());
    socket.shutdownOutput();

    final InputStream inputStream = socket.getInputStream();
    final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

    String str;
    while ((str = rd.readLine()) != null) {
      System.out.println(str);
    }
    rd.close();
    socket.close();
  }
}
