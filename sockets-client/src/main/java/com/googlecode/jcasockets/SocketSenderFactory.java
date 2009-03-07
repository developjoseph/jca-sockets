package com.googlecode.jcasockets;

public interface SocketSenderFactory {
	SocketSender createSocketSender(Integer port);
}
