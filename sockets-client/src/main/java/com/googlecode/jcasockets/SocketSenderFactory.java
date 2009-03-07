package com.googlecode.jcasockets;

public interface SocketSenderFactory {
	SocketSender createSocketSender(String ipAddress, Integer port);
}
