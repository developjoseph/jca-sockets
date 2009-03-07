package com.googlecode.jcasockets;

public class RemoteSocketSender implements SocketSender, SocketSenderFactory{

	private Integer port;

	public RemoteSocketSender(Integer port) {
		this.port = port;
	}

	@Override
	public String send(String send) {
		return null;
	}

	@Override
	public SocketSender createSocketSender(Integer port) {
		return new RemoteSocketSender(port);
	}

}
