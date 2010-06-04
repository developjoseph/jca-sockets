package com.googlecode.jcasockets.protocol;

import java.io.IOException;
import java.net.Socket;

public class RequestResponseDisconnectProtocol extends AbstractCharacterTerminatedMessageProtocol{

	public RequestResponseDisconnectProtocol(Socket socket) throws IOException {
		super(socket, '\n');
	}


}
