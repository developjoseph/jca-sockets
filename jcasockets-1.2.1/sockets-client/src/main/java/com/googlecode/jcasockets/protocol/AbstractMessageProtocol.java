package com.googlecode.jcasockets.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public abstract class AbstractMessageProtocol {

	private boolean endOfConversation;
	private final Socket socket;
	private final Reader reader;
	private final Writer writer;

	protected abstract void writeMessage(String message) throws IOException;
	protected abstract String readMessage() throws IOException;
	
	protected Reader getReader() {
		return reader;
	}

	protected Writer getWriter() {
		return writer;
	}

	protected void write( String message ) throws IOException{
		writer.write(message);
	}
	protected void write(char charToWrite) throws IOException {
		writer.write(charToWrite);
	}

	protected void flush() throws IOException {
		writer.flush();
	}

	protected void setEndOfConversation(boolean endOfConversation) {
		this.endOfConversation = endOfConversation;
	}
	protected char readCharacter() throws IOException{
		return (char) reader.read();
	}

	public AbstractMessageProtocol(Socket socket) throws IOException {
		this.socket = socket;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public boolean isEndOfConversation() {
		return endOfConversation;
	}

	public void close() throws IOException {
		socket.shutdownOutput();
		socket.shutdownInput();
		socket.close();
	}


}