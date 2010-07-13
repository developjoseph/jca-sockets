package com.googlecode.jcasockets.perf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class AbstractRemoteSocketSender {

	private long startTimeMillis;
	private Socket socket;
	private Integer port;
	private String ipAddress;
	private SocketAddress socketAddress;

	public AbstractRemoteSocketSender(String ipAddress, Integer port) {
		this.ipAddress = ipAddress;
		this.port = port;
		startTimeMillis = System.currentTimeMillis();
	}


	public AbstractRemoteSocketSender() {
	}


	protected void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

	protected void connectSocketWithTimeout(int timeoutMs) throws IOException {
		socket.connect(socketAddress, timeoutMs);
	}

	protected void closeSocketExceptionOnError() {
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(
					"Thread " + Thread.currentThread().getName() +  " exception while closing: " + ipAddress + ":" + port + " after " + (System.currentTimeMillis() - startTimeMillis) + "ms", e );
		}
	}

	protected void recordAndThrowException(IOException e) {
		String out = "Thread " + Thread.currentThread().getName() +  " exception while sending: " + ipAddress + ":" + port + " after " + (System.currentTimeMillis() - startTimeMillis) + "ms";
		System.out.println(out);
		throw new RuntimeException( out, e );
	}

	protected String readResponse() throws IOException {
		final InputStream inputStream = socket.getInputStream();
		final BufferedReader socketReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String str;
		while ((str = readFromSocket(socketReader)) != null) {
			sb.append(str);
		}
		socketReader.close();
		return sb.toString();
	}

	protected void writeMessage(String sendMessage) throws IOException {
		OutputStream outputStream;
		outputStream = socket.getOutputStream();
		outputStream.write(sendMessage.getBytes());
		outputStream.flush();
	}

	protected void bindSocket() {
		socket = new Socket();
		socketAddress = new InetSocketAddress(ipAddress, port);
	}

	protected String readFromSocket(final BufferedReader rd) {
		String readLine;
		try {
			readLine = rd.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return readLine;
	}

	public void close() throws IOException {
		socket.close();
	}

}