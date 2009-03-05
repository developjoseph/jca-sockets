package com.googlecode.jcasockets;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;

public class ConformanceClient {

	private SocketSender socketSender;
	private ConformanceClientCli clientCli;
	Random random = new Random();
	private String filledString;

	public ConformanceClient(String string) throws ParseException {
		clientCli = new ConformanceClientCli();
		String[] args = string.split(" ");
		clientCli.parseArguments(args);
		char[] chars = new char[ clientCli.getMaximumMessageSize() ]; 
		Arrays.fill(chars, 'X');
		filledString = new String(chars);
	}

	void setSender(SocketSender socketSender) {
		this.socketSender = socketSender;
	}

	public void execute() {
		long incrementNanos = TimeUnit.SECONDS.toNanos( clientCli.getExecutionSeconds() );
		long endNanos = System.nanoTime() + incrementNanos;
		while ( endNanos > System.nanoTime()){
			String message = generateMessage();
			socketSender.send(message);
		}
	}

	private String generateMessage() {
		int range = clientCli.getMaximumMessageSize() - clientCli.getMinimumMessageSize();
		int size = random.nextInt(range) + clientCli.getMinimumMessageSize();
		return filledString.substring(0, size);
	}

}
