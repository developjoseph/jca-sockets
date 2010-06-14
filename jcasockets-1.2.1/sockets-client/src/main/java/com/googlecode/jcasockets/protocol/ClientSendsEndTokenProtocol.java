package com.googlecode.jcasockets.protocol;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;


public class ClientSendsEndTokenProtocol extends AbstractCharacterTerminatedMessageProtocol  implements Protocol, Closeable {


	public ClientSendsEndTokenProtocol(Socket socket, String endToken) throws IOException {
		super( socket, '\n' );
		this.endToken = endToken== null ? "" : endToken;
	}

}
