package com.googlecode.jcasockets;


public class MockSocketSender implements SocketSenderFactory, SocketSender {

	@Override
	public String send(String send) {
		return send;
	}

	@Override
	public SocketSender createSocketSender() {
		return new MockSocketSender();
	}
}
