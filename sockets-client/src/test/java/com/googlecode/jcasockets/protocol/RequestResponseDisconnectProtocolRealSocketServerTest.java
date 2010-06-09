package com.googlecode.jcasockets.protocol;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestResponseDisconnectProtocolRealSocketServerTest {
	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	private ExecutorService serverThreadPool;
	private AbstractCharacterTerminatedMessageProtocol clientProtocol;
	private static int currentPort = 9900;
	private static final String request1 = "TESTMESSAGE1";
	private static final String request2 = "TESTMESSAGE2";

	@Before
	public void setupSocketServer() throws IOException {
		Server server = new Server();
		serverThreadPool = Executors.newFixedThreadPool(1);
		serverThreadPool.submit(server);
		setupProtocolWithNewClientConnection();
	}

	@After
	public void teardownSocketServer() throws IOException {
		serverThreadPool.shutdownNow();
	}

	@Test
	public void testSimpleRequestResponseUsingProtocol() throws Exception {
		shutdownServerListener();
		assertResponseEqualsRequest(request1);
		closeClientSocket();
	}

	@Test
	public void testProtocolWithMultipleMessages() throws Exception {
		shutdownServerListener();
		assertResponseEqualsRequest(request1);
		assertResponseEqualsRequest(request2);
		closeClientSocket();
	}

	@Test
	public void testProtocolWithCloseAndReconnectBetweenRequest() throws Exception {
		assertResponseEqualsRequest(request1);
		closeClientSocket();

		setupProtocolWithNewClientConnection();
		assertResponseEqualsRequest(request2);

		shutdownServerListener();
		closeClientSocket();
	}

	private void shutdownServerListener() {
		shutdown.set(true);
	}

	private void assertResponseEqualsRequest(String request) throws IOException {
		clientProtocol.writeMessage(request);
		String response = clientProtocol.readMessage();
		assertEquals(request, response);
	}

	private void setupProtocolWithNewClientConnection() throws UnknownHostException, SocketException, IOException {
		final InetAddress address = InetAddress.getByName("localhost");
		final SocketAddress socketAddress = new InetSocketAddress(address, currentPort);
		Socket clientSocket = new Socket();
		clientSocket.connect(socketAddress);
		clientProtocol = new RequestResponseDisconnectProtocol(clientSocket);
	}

	private void closeClientSocket() throws IOException {
		clientProtocol.close();
	}

	private class Server implements Runnable {
		private ServerSocket serverSocket;

		public Server() throws IOException {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(++currentPort));
		}

		public void run() {
			Thread.currentThread().setName("Socket Server Thread");
			try {
				while (!shutdown.get()) {
					final Socket socket = serverSocket.accept();

					AbstractCharacterTerminatedMessageProtocol protocol = new RequestResponseDisconnectProtocol(socket);
					String message;
					while (!"".equals(message = protocol.readMessage())) {
						protocol.writeMessage(message);
					}
					socket.shutdownOutput();
					socket.shutdownInput();
					socket.close();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				close(serverSocket);
			}
		}
	}

	private void close(ServerSocket toClose) {
		try {
			toClose.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
