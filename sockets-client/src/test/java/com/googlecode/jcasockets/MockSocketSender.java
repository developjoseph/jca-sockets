package com.googlecode.jcasockets;

public class MockSocketSender  implements SocketSender{

	private int messageCount;

	@Override
	public String send(String send) {
		++messageCount;
		return null;
	}

	public int getMessageCount() {
		return messageCount;
	}

}
