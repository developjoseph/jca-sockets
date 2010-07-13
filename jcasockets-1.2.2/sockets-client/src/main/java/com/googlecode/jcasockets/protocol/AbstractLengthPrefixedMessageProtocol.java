package com.googlecode.jcasockets.protocol;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractLengthPrefixedMessageProtocol extends AbstractMessageProtocol {
	private static final String maxPad = "000000000000000";
	private final int sizeOfLengthField;

	public AbstractLengthPrefixedMessageProtocol(Socket socket, int sizeOfLengthField ) throws IOException {
		super( socket );
		this.sizeOfLengthField = sizeOfLengthField;
	}

	public boolean clientSendsToken() {
		return true;
	}
	
	abstract boolean isEndOfConversation(String message );

	@Override
	public void writeMessage(String message) throws IOException {
		writeZeroPaddedLength( message.length() );
		write(message);
		flush();
	}

	@Override
	public String readMessage() throws IOException {
		int charReadCount = 0;
		int totalCharsToRead = Integer.MAX_VALUE;
		
		StringBuilder sb = new StringBuilder();
		while (charReadCount < totalCharsToRead) {
			char charRead = readCharacter();
			charReadCount++;
			sb.append(charRead);
			if ( charReadCount == sizeOfLengthField ){
				totalCharsToRead = Integer.parseInt(sb.toString()) + sizeOfLengthField;
			}
		}
		String message =  sb.substring( sizeOfLengthField, totalCharsToRead);
		if (isEndOfConversation( message)) {
			setEndOfConversation( true );
			return null;
		}
		return message;
	}
	private void writeZeroPaddedLength(int length) throws IOException {
		String tempPad = maxPad + length;
		String paddedLength =  tempPad.substring( tempPad.length() - sizeOfLengthField);
		write(paddedLength);
	}

}