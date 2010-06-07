package com.googlecode.jcasockets.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public abstract class AbstractCharacterTerminatedMessageProtocol {
	private static final char EOF = (char)-1;
	boolean endOfConversation;
	private final Socket socket;
	String endToken;

	public Reader getReader() {
		return reader;
	}

	public Writer getWriter() {
		return writer;
	}

	private final Reader reader;
	private final Writer writer;
	private final char recordTerminator;

	protected void writeMessage(String message) throws IOException {
		writer.write(message);
		writer.write(recordTerminator);
		writer.flush();
	}

	public AbstractCharacterTerminatedMessageProtocol(Socket socket, char recordTerminator) throws IOException {
		this.socket = socket;
		this.recordTerminator = recordTerminator;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public boolean clientSendsToken() {
		return true;
	}

	public boolean isEndOfConversation() {
		return endOfConversation;
	}

	public String getMessage() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean endOfRead = false;
		while (!endOfRead) {
			char charRead = (char) reader.read();
			if (charRead == recordTerminator || charRead == EOF) {
				endOfRead = true;
			} else {
				sb.append(charRead);
			}
		}
		String message =  sb.toString();
		if (message.equals(endToken)) {
			endOfConversation = true;
			return null;
		}
		return message;
	}

	public void close() throws IOException {
		socket.shutdownOutput();
		socket.shutdownInput();
		socket.close();
	}

}