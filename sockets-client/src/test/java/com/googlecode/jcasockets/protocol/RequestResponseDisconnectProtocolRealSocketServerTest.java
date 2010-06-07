package com.googlecode.jcasockets.protocol;
/*
 * The SO_LINGER option is used to specify how the close() method affects
 * socket using a connection-oriented protocol (i.e. TCP/IP, not UDP). A
 * good reference for this option is UNIX Network Programming, 2nd Edition,
 * by W. Richard Stevens, pp.187-191.
 * 
 * On invoking the close() method, there are several things that can happen,
 * depending on the state of SO_LINGER:
 * 
 * Linger set to false (default) No more receives or sends can be issued on
 * the socket. The contents of the socket send buffer are sent to the other
 * end. Data in socket receive buffer is discarded. The close() method
 * returns immediately, performing these activities in the background.
 * 
 * Linger set to true and linger time==0 No more receives or sends can be
 * issued on the socket. The socket send and receive buffers are both
 * discarded. This means that if the OS has data internally for the socket
 * this data is not sent and not received by the other end. The close()
 * method returns immediately and clears the send buffer in background.
 * 
 * Linger set to true and linger time!=0 No more receives or sends can be
 * issued on the socket. Data in socket send buffer is sent and data in the
 * receive buffer is discarded. If the linger time expires an exception will
 * occur. The close() method will block for a maximum of linger seconds or
 * until data has been sent and acknowledged at the TCP level.
 * 
 * The default scenario doesn't guarantee that the data is delivered if, for
 * example, you have a machine crash; as far as you know the return from the
 * close() implies that data has been sent at the application level.
 * However, if the remote machine crashes then the data in the send buffer
 * may be lost. The third scenario above provides this mechanism by
 * returning from the close() only when the data has been sent by the
 * system, so you can guarantee that data has been delivered (unless an
 * exception occurs). However, this can be achieved equally as well by
 * performing an application acknowledge without the use of this low-level
 * technique and I would recommend that.
 * 
 * The second scenario would be used in the event that you wish to terminate
 * the connection to the client immediately without sending data.
 */

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
	private static int currentPort = 9999;

	@Before
	public void setupSocketServer() throws IOException {
		Server server = new Server();
		serverThreadPool = Executors.newFixedThreadPool(1);
		serverThreadPool.submit(server);
		setupClientProtocol();
	}

	private void setupClientProtocol() throws UnknownHostException,
			SocketException, IOException {
		final InetAddress address = InetAddress.getByName("localhost");
		final SocketAddress socketAddress = new InetSocketAddress(address, currentPort);
		Socket clientSocket = new Socket();
		clientSocket.setSoLinger(false, 0);
		clientSocket.connect(socketAddress);

		clientProtocol = new RequestResponseDisconnectProtocol(clientSocket);
	}

	@After
	public void teardownSocketServer() throws IOException {
		serverThreadPool.shutdownNow();
	}

	@Test
	public void testProtocol() throws Exception {
		shutdown.set(true);
		String request = "TESTMESSAGE";
		clientProtocol.writeMessage(request);
		String response = clientProtocol.getMessage();
		assertEquals(request, response);
		clientProtocol.close();
	}

	@Test
	public void testProtocolWithMultipleMessages() throws Exception {
		shutdown.set(true);
		String request1 = "TESTMESSAGE1";
		String request2 = "TESTMESSAGE2";
		clientProtocol.writeMessage(request1);
		String response1 = clientProtocol.getMessage();
		assertEquals(request1, response1);
		clientProtocol.writeMessage(request2);
		String response2 = clientProtocol.getMessage();
		assertEquals(request2, response2);
		clientProtocol.close();
	}

	@Test
	public void testProtocolWithClose() throws Exception {
		String request1 = "TESTMESSAGE1";
		String request2 = "TESTMESSAGE2";
		clientProtocol.writeMessage(request1);
		String response1 = clientProtocol.getMessage();
		assertEquals(request1, response1);
		clientProtocol.close();

		setupClientProtocol();
		clientProtocol.writeMessage(request2);
		String response2 = clientProtocol.getMessage();
		assertEquals(request2, response2);
		shutdown.set(true);
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
				while ( !shutdown.get() ) {
					final Socket socket = serverSocket.accept();
					socket.setSoLinger(false, 0);
					AbstractCharacterTerminatedMessageProtocol protocol = new RequestResponseDisconnectProtocol(
							socket);
					String message;
					while (!"".equals(message = protocol.getMessage())) {
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
