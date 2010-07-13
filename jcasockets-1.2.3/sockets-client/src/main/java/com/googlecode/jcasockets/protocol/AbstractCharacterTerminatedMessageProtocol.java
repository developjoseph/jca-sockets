package com.googlecode.jcasockets.protocol;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractCharacterTerminatedMessageProtocol extends AbstractMessageProtocol {
	private static final char EOF = (char)-1;
	String endToken;

	private final char recordTerminator;

	public AbstractCharacterTerminatedMessageProtocol(Socket socket, char recordTerminator) throws IOException {
		super( socket );
		this.recordTerminator = recordTerminator;
	}

	public boolean clientSendsToken() {
		return true;
	}

	@Override
	protected void writeMessage(String message) throws IOException {
		write(message);
		write(recordTerminator);
		flush();
	}

	@Override
	public String readMessage() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean endOfRead = false;
		while (!endOfRead) {
			char charRead = readCharacter();
			if (charRead == recordTerminator || charRead == EOF) {
				endOfRead = true;
			} else {
				sb.append(charRead);
			}
		}
		String message =  sb.toString();
		if (message.equals(endToken)) {
			setEndOfConversation( true );
			return null;
		}
		return message;
	}

}