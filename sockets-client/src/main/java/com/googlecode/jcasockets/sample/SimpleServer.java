package com.googlecode.jcasockets.sample;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.googlecode.jcasockets.protocol.AbstractCharacterTerminatedMessageProtocol;
import com.googlecode.jcasockets.protocol.ClientSendsEndTokenProtocol;

public class SimpleServer {

	private final int port;
	private final int numThreads;

	public SimpleServer(int port, int numThreads) {
		this.port = port;
		this.numThreads = numThreads;
	}

	public static void main(String[] args) throws Exception {
		SimpleServer server = new SimpleServer(9000, 2);
		server.start();
	}

	private ServerSocket serverSocket;

	class SocketServerWorker implements Callable<Boolean> {
		AbstractCharacterTerminatedMessageProtocol protocol;
		private final Socket socket;

		SocketServerWorker(Socket socket) throws IOException {
			this.socket = socket;
			protocol = new ClientSendsEndTokenProtocol(socket, "EXIT");
		}

		private Writer getWriter() {
			try {
				return new OutputStreamWriter(socket.getOutputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public Boolean call() throws Exception {
			String message = protocol.getMessage();
			final Writer socketOutput = getWriter();
			socketOutput.write( "Server " + message + "\n");
			socketOutput.flush();
			socket.shutdownOutput();
			if (protocol.isEndOfConversation()){
				protocol.close();
			}
			System.out.println(Thread.currentThread().getName() + " size was: "	+ message.length());
			return true;
		}
	}

	public void start() throws Exception {
		System.out.println("Start listening on port " + port);
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(port));
		while (true) {
			final Socket socket = serverSocket.accept();
			socket.setSoLinger(false, 0);
			threadPool.submit(new SocketServerWorker(socket));
		}
	}

}
